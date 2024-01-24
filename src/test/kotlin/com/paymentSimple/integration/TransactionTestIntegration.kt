package com.paymentSimple.integration

import com.paymentSimple.Common.Companion.buildTransactionRequest
import com.paymentSimple.Common.Companion.buildUser
import com.paymentSimple.common.toModel
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.external.TransactionValidatorRepository
import com.paymentSimple.repositories.UserRepository
import com.paymentSimple.services.TransactionService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestConfiguration(proxyBeanMethods = false)
class TransactionTestIntegration(
//    @Autowired val webTestClient: WebTestClient,

    @Autowired
    private val userRepository: UserRepository,

    @Autowired
    private val transactionService: TransactionService,

    @Autowired
    private val transactionValidatorRepository: TransactionValidatorRepository
) {
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
    }

    @AfterEach
    fun deleteAll() = runBlocking {
        userRepository.deleteAll()
    }

    @Test
    @Order(1)
    fun `should complete transaction`() = runBlocking {

        val sender =
            userRepository.save(
                buildUser().copy(id = null, userType = UserType.COMMON, document = "3333")
                    .copy(balance = BigDecimal(100))
            )
        val receiver =
            userRepository.save(buildUser().copy(id = null, userType = UserType.MERCHANT, document = "4444"))


        val transaction = buildTransactionRequest().copy(
            payer = sender.id!!,
            payee = receiver.id!!,
            value = BigDecimal(50)
        ).toModel()

//        coEvery { transactionValidatorRepository.validateTransaction(transaction) } returns TransactionApprovalResponse(
//            MessageApproval.Autorizado
//        )
        val transactionResult = transactionService.send(transaction)

        val userResult = userRepository.findById(receiver.id!!)

        assert(userResult!!.balance == BigDecimal(50))


    }

}