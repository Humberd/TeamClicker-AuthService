package com.teamclicker.authservice

import mu.KLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class PageViewEventSource(
    val kafkaSender: KafkaSender
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val runnable = Runnable {
            logger.info { "foobar" }
            try {
                kafkaSender.send(Foob(Math.random()))
            } catch (e: Exception) {
                logger.error(e) {}
            }
        }
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS)
    }

    companion object : KLogging()

}

@Service
class KafkaSender(
    val kafkaTemplate: KafkaTemplate<String, Foob>
) {
    companion object : KLogging()

    fun send(data: Foob) {
        logger.info { "sending $data" }
        kafkaTemplate.send("foobar2", data)
    }
}

@Service
class KafkaReceiver {
    companion object : KLogging()

    @KafkaListener(topics = ["foobar2"])
    fun result(@Payload data: Foob) {
        logger.info { "receiving $data" }
    }
}