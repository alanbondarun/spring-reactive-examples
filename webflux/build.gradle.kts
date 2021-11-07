plugins {
    kotlin("jvm") version "1.5.31"
    id("org.springframework.boot") version "2.5.6"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2")

    implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.6")
}
