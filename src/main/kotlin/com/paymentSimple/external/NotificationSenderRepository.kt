package com.paymentSimple.external

import com.paymentSimple.domain.user.User

interface NotificationSenderRepository {
    suspend fun sendNotification(user: List<User>): Unit
}