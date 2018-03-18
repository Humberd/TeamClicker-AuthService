package com.teamclicker.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class AuthserviceApplication

fun main(args: Array<String>) {
    runApplication<AuthserviceApplication>(*args)
//
//    val serviceAccount = FileInputStream("src/main/resources/firebase-config.json")
//
//    val options = FirebaseOptions.Builder()
//            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//            .setDatabaseUrl("https://team-clicker.firebaseio.com")
//            .build()
//    FirebaseApp.initializeApp(options)
//
//    val foo = FirebaseAuth.getInstance().getUserByEmailAsync("admin@admin.com").get()
//    foo.providerData
}

