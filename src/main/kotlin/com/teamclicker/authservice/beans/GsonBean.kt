package com.teamclicker.authservice.beans

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.teamclicker.authservice.serializers.DateDeserializer
import com.teamclicker.authservice.serializers.DateSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class GsonBean {
    @Bean
    fun gson(): Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Date::class.java, DateSerializer())
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .create()
}

