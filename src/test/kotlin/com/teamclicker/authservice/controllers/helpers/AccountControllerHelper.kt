package com.teamclicker.authservice.controllers.helpers

import com.teamclicker.authservice.dto.AccountUpdateRolesDTO
import com.teamclicker.authservice.extensions.deleteForEntity
import com.teamclicker.authservice.extensions.putForEntity
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AccountControllerHelper(private val http: TestRestTemplate) {
    fun deleteAccount() = DeleteAccountEndpointBuilder()
    fun undeleteAccount() = UndeleteAccountEndpointBuilder()
    fun updateRoles() = UpdateRolesEndpointBuilder()

    inner class DeleteAccountEndpointBuilder :
        EndpointBuilder<DeleteAccountEndpointBuilder, Void, String>(String::class.java, http) {

        fun accountId(value: Long) = this.addParam("accountId", value)

        override fun <T> build(httpEntity: HttpEntity<Void>, responseBodyType: Class<T>): ResponseEntity<T> {
            return http.deleteForEntity(
                "/api/auth/accounts/{accountId}/delete",
                httpEntity,
                responseBodyType,
                urlParams
            )
        }
    }

    inner class UndeleteAccountEndpointBuilder :
        EndpointBuilder<UndeleteAccountEndpointBuilder, Void, String>(String::class.java, http) {

        fun accountId(value: Long) = this.addParam("accountId", value)

        override fun <T> build(httpEntity: HttpEntity<Void>, responseBodyType: Class<T>): ResponseEntity<T> {
            return http.postForEntity(
                "/api/auth/accounts/{accountId}/undelete",
                httpEntity,
                responseBodyType,
                urlParams
            )
        }
    }

    inner class UpdateRolesEndpointBuilder :
        EndpointBuilder<UpdateRolesEndpointBuilder, AccountUpdateRolesDTO, String>(String::class.java, http) {

        fun accountId(value: Long) = this.addParam("accountId", value)

        override fun <T> build(
            httpEntity: HttpEntity<AccountUpdateRolesDTO>,
            responseBodyType: Class<T>
        ): ResponseEntity<T> {
            return http.putForEntity(
                "/api/auth/accounts/{accountId}/roles",
                httpEntity,
                responseBodyType,
                urlParams
            )
        }
    }
}