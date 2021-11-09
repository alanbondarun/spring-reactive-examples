package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

    data class Person(
        val name: String,
        val age: Int,
    )

    data class TaskResponse(
        val data: Int?,
    )

    @GetMapping("/test")
    open fun test() = TestResponse()

    /**
     * Request handler which accepts the request body as a Flow.
     * When given a request body as a JSON array, Spring automatically feeds the body after transforming it to a Flow.
     */
    @PostMapping("/test/_batch")
    open suspend fun batchTest(@RequestBody persons: Flow<Person>): TestResponse =
        TestResponse(
            data = "Hello" +
                persons
                    .fold("") { accumulator, value ->
                        "$accumulator, ${value.name}"
                    } +
                "!"
        )

    /**
     * WebFlux API endpoint executing a long-running task (simulated by just delaying the function).
     * This function blocks the worker thread, which prevents other threads from handling another requests.
     */
    @GetMapping("/task")
    open fun task() = longTaskService.getResult()
        .map { TaskResponse(it) }
        .defaultIfEmpty(TaskResponse(null))

    /**
     * Suspending WebFlux API endpoint executing a long-running task (simulated by just delaying the function).
     * Just using suspending functions can prevent worker thread from getting blocked.
     */
    @GetMapping("/task2")
    open suspend fun task2() =
        TaskResponse(data = longTaskService.getResult2())
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

    suspend fun getResult2(): Int? {
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
