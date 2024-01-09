package com.paymentSimple.external

import com.paymentSimple.domain.user.User
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient

@Repository
class NotificationSenderClient : NotificationSenderRepository {

    private val webClient: WebClient = WebClient.create("https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6")
    override suspend fun sendNotification(user: List<User>): Unit {
        webClient.get().retrieve().toBodilessEntity()
        webClient.get().retrieve().toBodilessEntity()
    }

}