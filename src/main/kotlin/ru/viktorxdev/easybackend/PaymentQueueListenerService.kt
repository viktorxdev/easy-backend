package ru.viktorxdev.easybackend

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PaymentQueueListenerService(
    private val paymentRepository: PaymentRepository
) {

    private val log = KotlinLogging.logger { }

    @KafkaListener(
        topics = ["\${app.kafka.payment-topic}"],
        groupId = "easy"
    )
    fun handleRecord(record: ConsumerRecord<Long, String>) = runCatching {
        log.info { "receive message: $record" }
        record.key() to record.value().toBigDecimal()
    }.onSuccess {
        paymentRepository.savePaymentInfo(it.first, it.second)
    }.onFailure {
        log.error(it) { "fail to process message: $record" }
    }
}
