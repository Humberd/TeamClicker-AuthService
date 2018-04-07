package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.extensions.deleteForEntity
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AccountControllerHelper(private val http: TestRestTemplate) {
    fun deleteAccount() = DeleteAccountEndpointBuilder()

    inner class DeleteAccountEndpointBuilder :
        EndpointBuilder<DeleteAccountEndpointBuilder, Void, Void>(Void::class.java, http) {

        fun accountId(value: Any) = this.addParam("accountId", value)

        override fun <T> build(httpEntity: HttpEntity<Void>, responseBodyType: Class<T>): ResponseEntity<T> {
            return http.deleteForEntity(
                "/api/auth/accounts/{accountId}",
                httpEntity,
                responseBodyType,
                urlParams
            )
        }
    }
}