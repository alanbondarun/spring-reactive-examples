package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.fold
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToFlow
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json
import kotlin.random.Random

@SpringBootApplication
open class Application(
    private val handler: Handler,
) {
    @Bean
    open fun router(): RouterFunction<*> =
        coRouter {
            "/test".nest {
                GET("", handler::test)
                GET("/{name}", handler::testWithName)
                GET("/_exception/{name}", handler::testWithException)
                POST("/_batch", handler::batchTest)
                onError<InvalidNameException> { exception, _ ->
                    ServerResponse.badRequest()
                        .json()
                        .bodyValueAndAwait(mapOf("error" to exception.message))
                }
            }
            "/task".nest {
                GET("", handler::task)
            }
        }
}

@Controller
class Handler(
    private val longTaskService: LongTaskService,
) {
    data class TestResponse(
        val data: String = "Hello",
    )

    data class Person(
        val name: String,
        val age: Int,
    )

    data class TaskResponse(
        val data: Int?,
    )

    suspend fun test(request: ServerRequest) =
        ServerResponse.ok()
            .json()
            .bodyValueAndAwait(TestResponse())

    suspend fun testWithName(request: ServerRequest): ServerResponse {
        val name = request.pathVariable("name")
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(TestResponse("Hello, $name"))
    }

    /**
     * Request handler which accepts the request body as a Flow.
     * When given a request body as a JSON array, this handler parses the body to desired data classes.
     */
    suspend fun batchTest(request: ServerRequest): ServerResponse {
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                TestResponse(
                    data = "Hello" +
                        request.bodyToFlow<Person>()
                            .fold("") { accumulator, value ->
                                "$accumulator, ${value.name}"
                            } +
                        "!"
                )
            )
    }

    /**
     * Request handler which may throw an exception.
     * InvalidNameException thrown in this handler is handled at onError() in the router definition.
     */
    suspend fun testWithException(request: ServerRequest): ServerResponse {
        val name = request.pathVariable("name")
        if (!name.elementAt(0).isLetter()) {
            throw InvalidNameException(message = "$name is not a valid name.")
        }

        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(TestResponse("Hello, $name"))
    }

    /**
     * WebFlux API endpoint executing a long-running task (simulated by just delaying the function).
     * Just using suspending functions can prevent worker thread from getting blocked.
     */
    suspend fun task(request: ServerRequest): ServerResponse {
        val result = longTaskService.getResult()

        return if (result != null) {
            ServerResponse.ok()
                .json()
                .bodyValueAndAwait(TaskResponse(data = result))
        } else {
            ServerResponse.notFound()
                .buildAndAwait()
        }
    }
}

class InvalidNameException(message: String) : Exception(message)

@Service
class LongTaskService {
    suspend fun getResult(): Int? {
        delay(10000)

        return if (Random.nextBoolean()) {
            Random.nextInt(10)
        } else {
            null
        }
    }
}

fun main() {
    runApplication<Application>()
}
