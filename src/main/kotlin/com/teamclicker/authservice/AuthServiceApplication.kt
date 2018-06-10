package com.teamclicker.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding

@SpringBootApplication
class AuthserviceApplication

fun main(args: Array<String>) {
    runApplication<AuthserviceApplication>(*args)
}