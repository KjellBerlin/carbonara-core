import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.5"

	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"

	application
}

group = "com.carbonara"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux"){exclude(group = "ch.qos.logback")}
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.slf4j:slf4j-simple:2.0.13")

	// GraphQl
	implementation("com.expediagroup:graphql-kotlin-spring-server:8.0.0-alpha.1"){exclude(group = "ch.qos.logback")}

	// MongoDB
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive"){exclude(group = "ch.qos.logback")}
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb"){exclude(group = "ch.qos.logback")}

	// Auth0
	implementation("com.okta.spring:okta-spring-boot-starter:3.0.6"){exclude(group = "ch.qos.logback")}

	testImplementation("org.springframework.boot:spring-boot-starter-test"){exclude(group = "ch.qos.logback")}
	testImplementation("io.mockk:mockk:1.13.10")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
