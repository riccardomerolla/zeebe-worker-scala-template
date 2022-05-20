import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.client.api.worker.JobWorker

import java.util
import java.util.Map
import java.util.concurrent.CountDownLatch


/**
 * Example application that connects to a cluster on Camunda Cloud, or a locally deployed cluster.
 *
 * <p>When connecting to a cluster in Camunda Cloud, this application assumes that the following
 * environment variables are set:
 *
 * <ul>
 * <li>ZEEBE_ADDRESS
 * <li>ZEEBE_CLIENT_ID (implicitly required by {@code ZeebeClient} if authorization is enabled)
 * <li>ZEEBE_CLIENT_SECRET (implicitly required by {@code ZeebeClient} if authorization is enabled)
 * <li>ZEEBE_AUTHORIZATION_SERVER_URL (implicitly required by {@code ZeebeClient} if authorization is enabled)
 * </ul>
 *
 * <p><strong>Hint:</strong> When you create client credentials in Camunda Cloud you have the option
 * to download a file with above lines filled out for you.
 *
 * <p>When connecting to a local cluster, you only need to set {@code ZEEBE_ADDRESS}.
 * This application also assumes that authentication is disabled for a locally deployed clusterL
 */
object Worker {
  private val JOB_TYPE = "greet"

  @throws[InterruptedException]
  def main(args: Array[String]): Unit = {
    println("Starting worker...")
    val zeebeAddress = getEnvironmentVariable("ZEEBE_ADDRESS")
    println(s"Connecting to $zeebeAddress")
    val client = createZeebeClient(zeebeAddress)
    println(s"Registering worker for jobType: $JOB_TYPE")
    val jobWorker = client.newWorker.jobType(JOB_TYPE).handler(new WorkerJobHandler).open
    val countDownLatch = new CountDownLatch(1)
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      def foo() = {
        println(s"Closing worker for jobType: $JOB_TYPE")
        jobWorker.close()
        println(s"Closing client connected to $zeebeAddress")
        client.close()
        println("Worker Shutdown Complete")
        countDownLatch.countDown()
      }

      foo()
    }))
    countDownLatch.await()
  }

  private def createZeebeClient(gatewayAddress: String) = if (gatewayAddress.contains("zeebe.camunda.io")) {
    checkEnvVars("ZEEBE_CLIENT_ID", "ZEEBE_CLIENT_SECRET", "ZEEBE_AUTHORIZATION_SERVER_URL")
    /* Connect to Camunda Cloud Cluster, assumes that credentials are set in environment variables.
     * See JavaDoc on class level for details
     */
    ZeebeClient.newClientBuilder.gatewayAddress(gatewayAddress).build
  }
  else { // connect to local deployment; assumes that authentication is disabled
    ZeebeClient.newClientBuilder.gatewayAddress(gatewayAddress).usePlaintext.build
  }

  private def getEnvironmentVariable(key: String) = {
    checkEnvVars(key)
    val envVars = System.getenv
    envVars.get(key)
  }

  private def checkEnvVars(keys: String*): Unit = {
    val envVars = System.getenv
    for (key <- keys) {
      if (!envVars.containsKey(key)) throw new IllegalStateException("Unable to find mandatory environment variable " + key)
    }
  }
}
