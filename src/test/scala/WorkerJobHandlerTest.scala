import org.camunda.community.zeebe.testutils.stubs.ActivatedJobStub
import org.camunda.community.zeebe.testutils.stubs.JobClientStub
import org.junit.jupiter.api.Test
import java.util.Collections
import org.assertj.core.data.MapEntry.entry
import org.camunda.community.zeebe.testutils.ZeebeWorkerAssertions.assertThat


class WorkerJobHandlerTest {
  final private val sutJubHandler = new WorkerJobHandler

  @Test def testDefaultBehavior(): Unit = { // given
    val jobClient = new JobClientStub
    val activatedJob = jobClient.createActivatedJob
    // when
    sutJubHandler.handle(jobClient, activatedJob)
    // then
    assertThat(activatedJob).completed.extractingOutput.containsOnly(entry("message", "Hello Zeebe user!"))
  }

  @Test def testMessageGeneration(): Unit = {
    val jobClient = new JobClientStub
    val activatedJob = jobClient.createActivatedJob
    activatedJob.setCustomHeaders(Collections.singletonMap("greeting", "Howdy"))
    activatedJob.setInputVariables(Collections.singletonMap("name", "ladies and gentlemen"))
    sutJubHandler.handle(jobClient, activatedJob)
    assertThat(activatedJob).completed.extractingOutput.containsOnly(entry("message", "Howdy ladies and gentlemen!"))
  }
}