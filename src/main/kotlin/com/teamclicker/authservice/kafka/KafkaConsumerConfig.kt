package com.teamclicker.authservice.kafka

import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import java.util.HashMap



//@Configuration
//@EnableKafka
//@ConfigurationProperties("spring.kafka")
//class KafkaConsumerConfig {
////    @Value("\${spring.kafka.bootstrap-servers}")
//    lateinit var bootstrapServers: String
//
//    @Bean
//    fun consumerConfigs(): Map<String, Any> {
//        val props = HashMap<String, Any>()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "json"
//        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//        return props
//    }
//
//    @Bean
//    fun consumerFactory(): ConsumerFactory<String, Any> {
//        return DefaultKafkaConsumerFactory(
//            consumerConfigs(),
//            StringDeserializer(),
//            JsonDeserializer<Any>()
//        )
//    }
//
//    @Bean
//    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
//        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
//        factory.setConsumerFactory(consumerFactory())
//        return factory
//    }
//}