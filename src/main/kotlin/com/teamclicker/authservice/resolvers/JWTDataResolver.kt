package com.teamclicker.authservice.resolvers

import com.teamclicker.authservice.security.JWTAuthenticationToken
import com.teamclicker.authservice.security.JWTData
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Configuration
class JWTDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType.isAssignableFrom(JWTData::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        if (webRequest.userPrincipal === null ){
            return null
        }
        val userPrincipal = webRequest.userPrincipal as JWTAuthenticationToken
        return userPrincipal.jwtData
    }

}