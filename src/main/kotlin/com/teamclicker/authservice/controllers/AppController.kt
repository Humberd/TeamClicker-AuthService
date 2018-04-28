package com.teamclicker.authservice.controllers

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController()
@RequestMapping("/api/app")
class AppController {

    @ApiOperation(
        value = "Status of the deployed app", notes = """
Returns a status of the deployed app including:
 - Docker container id
 - Jenkins build number
 - Last git commit id
         """
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok")
        ]
    )
    @PreAuthorize("permitAll()")
    @GetMapping("/status", produces = ["text/html"])
    fun getStatus(): String {
        return """
            <h2>Status</h2>
            <div>Service: Auth Service</div>
            <div>Container Id: ${System.getenv("HOSTNAME")}</div>
            <div>Build number: ${System.getenv("BUILD_NUMBER")}</div>
            <div>Commit hash: ${System.getenv("COMMIT_HASH")}</div>
            """
    }

    @ApiOperation(value = "Pings the app")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok")
        ]
    )
    @PreAuthorize("permitAll()")
    @GetMapping("/ping", produces = ["text/plain"])
    fun ping(): String {
        return "pong"
    }
}