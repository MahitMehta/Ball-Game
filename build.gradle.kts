import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.7.0"
	`maven-publish`
}

group = "com.ballgame"
version = "1.0-SNAPSHOT"
description = "ballgame"

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.google.code.gson:gson:2.9.0")
	implementation("org.dyn4j:dyn4j:4.2.1")
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "16"
}

publishing {
	publications.create<MavenPublication>("maven") {
		from(components["kotlin"])
	}
}
