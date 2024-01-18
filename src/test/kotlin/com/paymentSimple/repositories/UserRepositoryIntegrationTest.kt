package com.paymentSimple.repositories

import com.paymentSimple.Common.Companion.buildUser
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
class UserRepositoryIntegrationTest {
    @Nested
    @TestConfiguration(proxyBeanMethods = false)
    inner class TestPaymentSimpleApplication {

        @Bean
        @ServiceConnection
        fun postgresContainer(): PostgreSQLContainer<*> {
            return PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
        }

        @Test
        fun `should return saved users`(@Autowired userRepository: UserRepository) = runBlocking {
            val user = buildUser().copy(id = null, document = "1122334455")

            userRepository.save(user)

            val result = userRepository.findAll()

            println(result)
            assert(result.count() > 0)
            assert(result.filter { it -> it.document == user.document }.count() > 0)


        }

    }
}