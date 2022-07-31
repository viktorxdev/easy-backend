package ru.viktorxdev.easybackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableKafka
@EnableScheduling
class EasyBackendApplication

fun main(args: Array<String>) {
    runApplication<EasyBackendApplication>(*args)
}
