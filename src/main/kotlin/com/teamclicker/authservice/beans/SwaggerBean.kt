package com.teamclicker.authservice.beans

import com.teamclicker.authservice.security.JWTData
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerBean {
    @Bean
    fun getDocker(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .forCodeGeneration(true)
            .useDefaultResponseMessages(false)
            .apiInfo(DEFAULT_API_INFO)
            .produces(DEFAULT_PRODUCES)
            .consumes(DEFAULT_CONSUMES)
            .ignoredParameterTypes(JWTData::class.java)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.teamclicker.authservice.controllers"))
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(listOf(apiKey()))
    }

    private fun apiKey(): ApiKey {
        return ApiKey("Authorization", "Authorization", "header")
    }

    companion object {
        val DEFAULT_PRODUCES = setOf("application/json")
        val DEFAULT_CONSUMES = setOf("application/json")

        // TODO: change to a proper data
        val DEFAULT_API_INFO = ApiInfoBuilder()
            .title("Team Clicker Auth Service")
            .description("REST API Service responsible for authorizing users")
            .license("Apache 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .termsOfServiceUrl("http://swagger.io/terms/")
            .version("0.0.1")
            .contact(
                Contact(
                    "Maciej Sawicki",
                    "teamclicker.com",
                    "humberd.dev@gmail.com"
                )
            )
            .build()
    }
}