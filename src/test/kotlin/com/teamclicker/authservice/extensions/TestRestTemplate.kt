package com.teamclicker.authservice.extensions

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

fun <T> TestRestTemplate.deleteForEntity(
    url: String,
    request: HttpEntity<*>,
    responseType: Class<T>,
    urlVariables: Map<String, *>
): ResponseEntity<T> {
    return this.exchange(url, HttpMethod.DELETE, request, responseType, urlVariables)
}

fun <T> TestRestTemplate.putForEntity(
    url: String,
    request: HttpEntity<*>,
    responseType: Class<T>,
    urlVariables: Map<String, *>
): ResponseEntity<T> {
    return this.exchange(url, HttpMethod.PUT, request, responseType, urlVariables)
}

