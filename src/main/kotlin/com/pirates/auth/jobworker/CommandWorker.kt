package com.pirates.auth.jobworker

import com.pirates.auth.exception.EnumException
import com.pirates.auth.exception.ErrorException
import com.pirates.auth.service.CommandService
import com.pirates.chat.model.bpe.CommandMessage
import com.pirates.chat.model.bpe.ResponseDto
import com.pirates.chat.model.bpe.getEnumExceptionResponseDto
import com.pirates.chat.model.bpe.getErrorExceptionResponseDto
import com.pirates.chat.utils.toJson
import com.pirates.chat.utils.toObject
import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.response.ActivatedJob
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CommandWorker(
                    private val zeebeClient: ZeebeClient,
                    private val commandService: CommandService
) {

    init {
        zeebeClient.newWorker()
                .jobType("AuthCommand")
                .handler(execution())
                .timeout(Duration.ofSeconds(10))
                .open()
    }

    fun startZeebeProcess(cm: CommandMessage) {
        zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(cm.command.value())
                .latestVersion()
                .variables(cm)
                .send()
                .join().workflowInstanceKey
    }

    private fun execution() = JobHandler { client, job ->
        val variables = job.variablesAsMap
        val cm = toObject(CommandMessage::class.java, toJson(variables))
        try {
            val response = commandService.execute(cm)
            variables["data"] = response.data
            variables["context"] = response.context
            client.newCompleteCommand(job.key).variables(variables).send()
        } catch (ex: ErrorException) {
            processException(getErrorExceptionResponseDto(ex, cm.id), job, variables)
        } catch (ex: EnumException) {
            processException(getEnumExceptionResponseDto(ex, cm.id), job, variables)
        }
    }

    private fun processException(response: ResponseDto, job: ActivatedJob, variables: MutableMap<String, Any>) {
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("error")
                .latestVersion()
                .variables(response)
                .send()
                .join()
                .run {
                    variables["errorsPID"] = this.workflowInstanceKey
                    variables["errors"] = response.errors ?: "error"
                    zeebeClient.newSetVariablesCommand(job.headers.workflowInstanceKey)
                            .variables(variables)
                            .send()
                            .join()
                            .run {
                                zeebeClient.newCancelInstanceCommand(job.headers.workflowInstanceKey).send()
                            }
                }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CommandWorker::class.java)
    }
}
