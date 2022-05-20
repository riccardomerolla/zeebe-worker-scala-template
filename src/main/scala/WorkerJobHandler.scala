import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.client.api.worker.JobClient
import io.camunda.zeebe.client.api.worker.JobHandler
import java.util.Collections


class WorkerJobHandler extends JobHandler {
  override def handle(client: JobClient, job: ActivatedJob): Unit = {
    val greeting = job.getCustomHeaders.getOrDefault("greeting", "Hello")
    val name = job.getVariablesAsMap.getOrDefault("name", "Zeebe user").asInstanceOf[String]
    val message = String.format("%s %s!", greeting, name)
    client.newCompleteCommand(job.getKey).variables(Collections.singletonMap("message", message)).send.join
  }
}
