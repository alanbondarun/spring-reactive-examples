package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import kotlin.random.Random

@SpringBootApplication
open class Application

@RestController
open class Controller(
    private val longTaskService: LongTaskService,
) {
    data class TestResponse(
        val data: String = "Hello",
    )

    data class TaskResponse(
        val data: Int?,
    )

    @GetMapping("/test")
    open fun test() = TestResponse()

    @GetMapping("/task")
    open fun task() = longTaskService.getResult()
        .map { TaskResponse(it) }
        .defaultIfEmpty(TaskResponse(null))
}

@Service
class LongTaskService {
    fun getResult(): Mono<Int> {
        runBlocking {
            delay(10000)
        }

        return if (Random.nextBoolean()) {
            Mono.just(Random.nextInt(10))
        } else {
            Mono.empty()
        }
    }
}

fun main() {
    runApplication<Application>()
}
