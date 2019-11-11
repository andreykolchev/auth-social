package com.pirates.auth.jobworker

import com.pirates.auth.model.Constants.CONTEXT
import com.pirates.auth.model.Constants.DATA
import com.pirates.auth.service.CommandService
import com.pirates.auth.model.bpe.CommandMessage
import com.pirates.auth.utils.toJson
import com.pirates.auth.utils.toObject
import io.zeebe.client.ZeebeClient
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
        val response = commandService.execute(cm)
        variables[DATA] = response.data
        variables[CONTEXT] = response.context
        client.newCompleteCommand(job.key).variables(variables).send()
        LOG.info("command: " + cm.command + " id: " + cm.id)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CommandWorker::class.java)
    }
}
