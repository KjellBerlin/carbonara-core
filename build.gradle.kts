import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"

	kotlin("jvm") version "2.0.0"
	kotlin("plugin.spring") version "2.0.0"

	application
}

group = "com.carbonara"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// logging
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.slf4j:slf4j-api:2.0.13")
	implementation("ch.qos.logback:logback-classic:1.4.12")

	// Necessary for asynchronous code
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

	// GraphQl
	implementation("com.expediagroup:graphql-kotlin-spring-server:8.0.0-alpha.1")

	// MongoDB
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// Auth0
	implementation("com.okta.spring:okta-spring-boot-starter:3.0.7")

	// Google Cloud secret manager
	implementation("com.google.cloud:spring-cloud-gcp-starter-secretmanager:5.5.0")

	// Google places
	implementation("com.google.maps:google-maps-services:2.2.0")

	// Mollie
	implementation("be.woutschoovaerts:mollie:4.3.0")

	// Slack
	implementation("com.slack.api:slack-api-model-kotlin-extension:1.42.0")
	implementation("com.slack.api:slack-api-client-kotlin-extension:1.42.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.11")
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_17)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
