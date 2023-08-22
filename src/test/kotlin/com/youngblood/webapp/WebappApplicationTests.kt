package com.youngblood.webapp

import com.youngblood.webapp.common.AbstractContainerTests
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WebappApplicationTests : AbstractContainerTests() {

  @Autowired
  lateinit var rabbitTemplate: RabbitTemplate

  @Test
  fun contextLoads() {
    rabbitTemplate.convertAndSend(
      "incomingEvent",
      "asdasd"
    )
    Thread.sleep(2000)
  }
}
