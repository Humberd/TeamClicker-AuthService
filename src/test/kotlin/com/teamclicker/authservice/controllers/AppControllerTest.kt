package com.teamclicker.authservice.controllers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class AppControllerTest {
    @Autowired
    lateinit var http: TestRestTemplate

    @Test
    fun getStatus() {
        val response = http.getForEntity("/api/app/status", String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertTrue(response.body?.length!! > 0)
    }

    @Test
    fun ping() {
        val response = http.getForEntity("/api/app/ping", String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals("pong", response.body)
    }

}