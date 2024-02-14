package com.youngblood.webapp

import io.klogging.config.DEFAULT_CONSOLE
import io.klogging.config.loggingConfiguration
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class WebappApplication

fun main(args: Array<String>) {
  loggingConfiguration { DEFAULT_CONSOLE() }
  runApplication<WebappApplication>(*args)
}

@RestController
@RequestMapping("/api")
class WebappController {
  @Operation(summary = "Get Billing Support Details for RQ")
  @ApiResponse(responseCode = "200", description = "Get Billing Decision Support info for RQ.")
  @GetMapping(value = ["/sample"])
  suspend fun responseData(): ResponseEntity<ServiceResponse> = ResponseEntity.ok(ServiceResponse("Something"))
}

data class ServiceResponse(val data: Any)

// @Component
// class RabbitMQListener(val amqpAdmin: AmqpAdmin) : NoCoLogging {
//
//  // TODO : Auto Create queues if needed - can remove and/or only create in dev mode....
//  @PostConstruct
//  fun createQueues() {
//    println("Create Queues")
//    with(amqpAdmin) {
//      // Make this loop over all queues if needed
//      this.getQueueInfo("incomingEvent").let { queueInfo ->
//        if (queueInfo == null) {
//          this.declareQueue(Queue("incomingEvent", true))
//        }
//      }
//    }
//  }
//
//  @RabbitListener(queues = ["incomingEvent"])
//  fun onEvent(event: String) {
//    println("Event $event")
//    logger.info("Received and event: $event")
//  }
// }
