package com.youngblood.webapp

import io.klogging.NoCoLogging
import io.klogging.config.DEFAULT_CONSOLE
import io.klogging.config.loggingConfiguration
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode
import javax.annotation.PostConstruct

@SpringBootApplication
class WebappApplication

fun main(args: Array<String>) {
  loggingConfiguration { DEFAULT_CONSOLE() }
  runApplication<WebappApplication>(*args)
}

// Web - HTMX

@Controller
class WebApp {
  @GetMapping("/")
  fun index(): String {
    return "index"
  }

  @GetMapping("/clicked")
  fun button(model: Model): String {
    model.addAttribute("test", "ing")
    return "clicked"
  }
}

// API Can be puuled out
@RestController
@RequestMapping("/api")
class WebappController {
  @Operation(summary = "Get Billing Support Details for RQ")
  @ApiResponse(responseCode = "200", description = "Get Billing Decision Support info for RQ.")
  @GetMapping(value = ["/sample"])
  suspend fun responseData(): ResponseEntity<ServiceResponse> = ResponseEntity.ok(ServiceResponse("Something"))
}

data class ServiceResponse(val data: Any)

@Component
class RabbitMQListener(
//  val amqpAdmin: AmqpAdmin
) : NoCoLogging {

  // TODO : Auto Create queues if needed - can remove and/or only create in dev mode....
  @PostConstruct
  fun createQueues() {
    println("Create Queues")
//    with(amqpAdmin) {
//      // Make this loop over all queues if needed
//      this.getQueueInfo("incomingEvent").let { queueInfo ->
//        if (queueInfo == null) {
//          this.declareQueue(Queue("incomingEvent", true))
//        }
//      }
//    }
  }

//  @RabbitListener(queues = ["incomingEvent"])
//  fun onEvent(event: String) {
//    println("Event $event")
//    logger.info("Received and event: $event")
//  }
}

@Component
class ThmyleafConfig {
  @Bean
  fun templateResolver(applicationContext: ApplicationContext): SpringResourceTemplateResolver {
    // SpringResourceTemplateResolver automatically integrates with Spring's own
    // resource resolution infrastructure, which is highly recommended.
    val templateResolver = SpringResourceTemplateResolver()
    templateResolver.setApplicationContext(applicationContext)
    templateResolver.prefix = "/WEB-INF/templates/"
    templateResolver.suffix = ".html"
    // HTML is the default value, added here for the sake of clarity.
    templateResolver.setTemplateMode(TemplateMode.HTML)
    // Template cache is true by default. Set to false if you want
    // templates to be automatically updated when modified.
    templateResolver.isCacheable = false
    return templateResolver
  }

//  @Bean
//  fun templateEngine(thymleafResolver: SpringResourceTemplateResolver): SpringTemplateEngine {
//    // SpringTemplateEngine automatically applies SpringStandardDialect and
//    // enables Spring's own MessageSource message resolution mechanisms.
//    val templateEngine = SpringTemplateEngine()
//    templateEngine.setTemplateResolver(thymleafResolver)
//    // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
//    // speed up execution in most scenarios, but might be incompatible
//    // with specific cases when expressions in one template are reused
//    // across different data types, so this flag is "false" by default
//    // for safer backwards compatibility.
//    templateEngine.setEnableSpringELCompiler(true)
//    return templateEngine
//  }
}
