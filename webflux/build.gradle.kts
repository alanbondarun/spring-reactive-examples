plugins {
    kotlin("jvm") version "1.5.31"
    id("org.springframework.boot") version ("2.5.6")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")

    implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.6")
}
