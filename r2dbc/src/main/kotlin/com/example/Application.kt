package com.example

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.reactive.asFlow
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json
import org.springframework.web.reactive.function.server.plus
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
@EnableR2dbcRepositories
open class Application(
    private val handler: Handler,
) {
    @Bean
    open fun routerFunction(): RouterFunction<*> =
        coRouter {
            "/person".nest {
                GET("/_count", handler::personCount)
            }
        }
}

@Controller
class Handler(
    private val personRepository: PersonRepository,
) {
    data class DataResponse(
        val data: Int,
    )

    suspend fun personCount(request: ServerRequest): ServerResponse {
        val count = personRepository.count().asFlow().first().toInt()
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(DataResponse(data = count))
    }
}

data class Person(
    @Id
    val id: Int,
    val name: String,
    val age: String,
)

interface PersonRepository : ReactiveCrudRepository<Person, Long>

fun main() {
    runApplication<Application>()
}
