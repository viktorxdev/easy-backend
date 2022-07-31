package ru.viktorxdev.easybackend

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.util.concurrent.TimeUnit

@Service
class PaymentCsvWriterService(
    private val paymentRepository: PaymentRepository,
    @Value("\${app.path.transactions}") private val path: String,
) {

    private val log = KotlinLogging.logger { }

    private val format = CSVFormat.Builder.create()
        .setDelimiter(';')
        .setRecordSeparator("\n")
        .build()

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    fun enrichCsvFile() {
        val payments = paymentRepository.getNewPayments()
        if (payments.isEmpty()) return
        val file = File(path)
        FileWriter(file, true).use { fileWriter ->
            CSVPrinter(fileWriter, format).use { csvWriter ->
                if (file.length() == 0L) {
                    csvWriter.printRecord("id", "account_id", "amount", "datetime_transaction")
                }
                csvWriter.printRecords(payments.values)
            }
        }
        log.info { "${payments.size} lines were appended to csv file" }
    }

    private val List<PaymentInfo>.values
        get() = map {
            listOf(it.id, it.accountId, it.amount, it.datetimeTransaction)
        }
}