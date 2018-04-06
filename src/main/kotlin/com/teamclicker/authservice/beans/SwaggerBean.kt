package com.teamclicker.authservice.beans

import com.google.common.base.Predicates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
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
            .useDefaultResponseMessages(false)
            .apiInfo(DEFAULT_API_INFO)
            .produces(DEFAULT_PRODUCES)
            .consumes(DEFAULT_CONSUMES)
//                .ignoredParameterTypes(UserDAO::class.java)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.cloud")))
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.data.rest.webmvc")))
            .build()
    }

    companion object {

        val DEFAULT_PRODUCES = setOf("application/json")
        val DEFAULT_CONSUMES = setOf("application/json")

        // TODO: change to a proper data
        val DEFAULT_API_INFO = ApiInfo(
            "Team Clicker Auth Service",
            "An API Service responsible for authorizing users.",
            "0.0.1",
            "admin.com",
            Contact(
                "Maciej Sawicki",
                "admin.com",
                "admin@admin.com"
            ),
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            emptyList()
        )

    }
}