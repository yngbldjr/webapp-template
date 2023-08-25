import org.asciidoctor.gradle.editorconfig.AsciidoctorEditorConfigGenerator
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springdoc.openapi.gradle.plugin.OpenApiGeneratorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.text.SimpleDateFormat
import java.util.*

plugins {
	id("org.springframework.boot") version "2.7.3"
	id("io.spring.dependency-management") version "1.0.13.RELEASE"
	id("org.graalvm.buildtools.native") version "0.9.23"
	id("org.springdoc.openapi-gradle-plugin") version "1.6.0"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	id("com.diffplug.spotless") version "6.11.0"
	id("org.sonarqube") version "3.1.1"
	id("org.flywaydb.flyway") version "9.14.1"
	id("com.adarshr.test-logger") version "3.2.0"
	id("org.jetbrains.kotlinx.kover") version "0.6.1"
	// Documentation
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	id("org.asciidoctor.editorconfig") version "3.3.2"
	id("io.github.redgreencoding.plantuml") version "0.2.0"
}

group = "com.youngblood"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

spotless {
	kotlin {
		// version, setUseExperimental, userData and editorConfigOverride are all optional
		ktlint("0.45.2")
			.setUseExperimental(true)
			.editorConfigOverride(mapOf("indent_size" to 2))
	}
}

springBoot {
	buildInfo()
}

sourceSets {
	create("docs") {
		kotlin {
			compileClasspath += main.get().output
			runtimeClasspath += output + compileClasspath
		}
		dependencies {
			"docsImplementation"("io.github.chriskn:structurizr-c4puml-extension:0.8.0")
			"docsImplementation"("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")
			"docsImplementation"("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
			"docsImplementation"("com.structurizr:structurizr-analysis:1.3.5")
			"docsImplementation"("com.structurizr:structurizr-spring:1.3.5")
			// make spring annotations available to use it with AnnotationTypeMatcher
			"docsImplementation"("org.springframework.boot:spring-boot-starter")

			"docsImplementation"("io.klogging:klogging-jvm:0.4.9")
			"docsImplementation"("com.squareup.okhttp3:okhttp:4.10.0")
			"docsImplementation"("org.jetbrains.exposed:exposed-spring-boot-starter:0.41.1")
		}
	}
}

// Client api version
val openAPIVersion = "1.7.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// Open API Spec
	implementation("org.springdoc:springdoc-openapi-ui:$openAPIVersion")
	implementation("org.springdoc:springdoc-openapi-kotlin:$openAPIVersion")
	implementation("org.springdoc:springdoc-openapi-webflux-ui:$openAPIVersion")
	implementation("org.springdoc:springdoc-openapi-webflux-core:$openAPIVersion")

	// Exposed ORM
	implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.41.1")
	implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.41.1")

	// Arrow
	implementation("io.arrow-kt:arrow-core:1.2.0-RC")

	// Jackson Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")

	// Logging
	implementation("io.klogging:klogging-jvm:0.4.9")

	// Dev
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}

	// Use mockk instead of mockito
	testImplementation("com.ninja-squad:springmockk:4.0.0")

	// Testcontainers
	testImplementation("org.testcontainers:junit-jupiter:1.17.6")
	testImplementation("org.testcontainers:postgresql:1.17.6")
//	testImplementation("org.testcontainers:rabbitmq:1.19.0")

	testImplementation("io.projectreactor:reactor-test")
//	testImplementation("org.springframework.amqp:spring-rabbit-test")




}

flyway {
	url = "jdbc:postgresql://localhost:5432/postgres"
}

tasks.withType<BootJar> {
	// to play it safe, we're setting this from IGNORE to FAIL,
	// so that gradle aborts the build if it tries to include two JARs with the same name
	duplicatesStrategy = DuplicatesStrategy.FAIL
}

repositories {
	mavenCentral()
	maven(url = "https://packages.confluent.io/maven/")
}

tasks.withType<KotlinCompile> {
	dependsOn("spotlessApply")
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs = listOf("--add-opens","java.base/java.time=ALL-UNNAMED")
}

tasks.create<Delete>("removeOpenAPISpec") {
	delete("$projectDir/openapi-spec")
}

tasks.withType<OpenApiGeneratorTask> {
	dependsOn("removeOpenAPISpec")
}

openApi {
	outputDir.set(file("$projectDir/openapi-spec"))
}

kover {
	verify {
		rule {
			name = "Minimal line coverage rate in percent"
			bound {
				minValue = 75
			}
		}
	}
}

// Documentation

val asciiAttributes = mapOf(
	"imagesdir" to ".",
	"plantUmlDir" to "./plantuml",
	"toc" to "left",
	"toclevels" to 3,
	"max-width" to "100%",
	"projectName" to rootProject.name,
	"dateTime" to SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ").format(Date())
)

tasks.withType(AsciidoctorTask::class) {
	setSourceDir(file("./docs/resources"))
	setBaseDir(file("./docs/resources"))
	setOutputDir(file("./docs/resources"))
	attributes(asciiAttributes)
	options(mapOf("doctype" to "book"))
	isLogDocuments = true
	dependsOn("writeDiagrams")
}

tasks.withType(AsciidoctorEditorConfigGenerator::class) {
	setAttributes(asciiAttributes)
	setDestinationDir("./docs/resources")
	group = "documentation"
}

tasks.named("processDocsResources") {
	dependsOn("asciidoctorEditorConfig")
}

tasks.register("writeDiagrams", JavaExec::class) {
	classpath += sourceSets["docs"].runtimeClasspath
	mainClass.set("docsascode.WriteDiagramsKt")
	group = "documentation"
}

plantuml {
	options {
		// where should the .svg be generated to (defaults to build/plantuml)
		outputDir = project.file("docs/resources/plantuml")

		// output format (lowercase, defaults to svg)
		format = "png"
	}

	diagrams {
		create("components") {
			sourceFile = File("docs/resources/plantuml/sample_service_components.puml")
		}
		create("container") {
			sourceFile = File("docs/resources/plantuml/sample_service_container.puml")
		}
		create("context") {
			sourceFile = File("docs/resources/plantuml/sample_service_context.puml")
		}
	}
}

asciidoctorj {
	modules {
		diagram.use()
		diagram.setVersion("2.2.1")
	}
}