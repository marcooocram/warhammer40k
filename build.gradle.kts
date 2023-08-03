import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("org.sonarqube") version "4.2.1.3168"
    jacoco
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation ("io.kotest:kotest-runner-junit5:5.4.2")

    //csv-to-dataclass
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")
    implementation("io.github.blackmo18:kotlin-grass-core-jvm:1.0.0")
    implementation("io.github.blackmo18:kotlin-grass-parser-jvm:0.8.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}

tasks.sonar {
    dependsOn(tasks.test)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}
