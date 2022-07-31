package ru.viktorxdev.easybackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EasyBackendApplication

fun main(args: Array<String>) {
    runApplication<EasyBackendApplication>(*args)
}
