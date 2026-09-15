plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.3.21"

    // Backend Kotlin 코드의 formatting과 style 규칙을 검사한다.
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "com.ariadne"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Integration Test 실행 시 임시 MySQL 8.4 Container를 제공
    // Testcontainers 2.x 모듈들의 버전을 동일하게 관리
    testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.5"))

    // Integration Test에서 실제 MySQL Container를 사용
    testImplementation("org.testcontainers:testcontainers-mysql")

    // DB Schema 변경 이력을 버전 단위로 관리
    // 개발, 테스트, CI에서 동일한 Migration을 적용한
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.flywaydb:flyway-mysql")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()

    // 모든 Backend 테스트는 test profile을 기본으로 사용
    // 로컬 DB에 의존하지 않도록 테스트 실행 환경을 통일
    systemProperty("spring.profiles.active", "test")
}
