package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
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

    /**
     * WebMVC API endpoint executing a long-running task (simulated by just delaying the function).
     */
    @GetMapping("/task")
    open fun task() = TaskResponse(data = longTaskService.getResult())
}

@Service
class LongTaskService {
    fun getResult(): Int? {
        runBlocking {
            delay(10000)
        }

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
