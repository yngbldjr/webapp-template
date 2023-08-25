package com.youngblood.webapp.common

import io.klogging.NoCoLogging
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(initializers = [AbstractContainerTests.Initializer::class])
@Transactional
abstract class AbstractContainerTests : NoCoLogging {

  companion object {
    @Container
    private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

//    @Container
//    var rabbitMQContainer: RabbitMQContainer = RabbitMQContainer("rabbitmq:3")
  }

  internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
      postgreSQLContainer.start()
      TestPropertyValues.of(
        "spring.datasource.url=${postgreSQLContainer.jdbcUrl}",
        "spring.datasource.username=${postgreSQLContainer.username}",
        "spring.datasource.password=${postgreSQLContainer.password}"
      ).applyTo(configurableApplicationContext.environment)

//      rabbitMQContainer.start()
//
//      TestPropertyValues.of(
//        "spring.rabbitmq.host=${rabbitMQContainer.host}",
//        "spring.rabbitmq.port=${rabbitMQContainer.amqpPort}",
//        "spring.rabbitmq.username=${rabbitMQContainer.adminUsername}",
//        "spring.rabbitmq.password=${rabbitMQContainer.adminPassword}"
//      ).applyTo(configurableApplicationContext.environment)
    }
  }
}
