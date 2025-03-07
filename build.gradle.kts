plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
	id("org.sonarqube") version "6.0.1.5171"
	id("com.github.ben-manes.versions") version "0.51.0"
	id ("org.openapi.generator") version "7.10.0"
}

group = "it.gov.pagopa.payhub"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}


repositories {
	mavenCentral()
}


dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

val springDocOpenApiVersion = "2.8.5"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.6"
val wiremockVersion = "3.12.0"
val hibernateValidatorVersion = "8.0.2.Final"
val micrometerVersion = "1.4.3"
val commonsIoVersion = "2.18.0"
val bouncycastleVersion = "1.80"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion")
	implementation("org.codehaus.janino:janino:$janinoVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
	implementation ("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
	implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")

	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation ("org.projectlombok:lombok")
	testImplementation ("org.wiremock:wiremock-standalone:$wiremockVersion")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
	mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
	test {
		jvmArgs("-javaagent:${mockitoAgent.asPath}")
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
	}
}

val projectInfo = mapOf(
		"artifactId" to project.name,
		"version" to project.version
)

tasks {
	val processResources by getting(ProcessResources::class) {
		filesMatching("**/application.yml") {
			expand(projectInfo)
		}
	}
}

configurations {
	compileClasspath {
		resolutionStrategy.activateDependencyLocking()
	}
}

tasks.compileJava {
	dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
	group = "AutomaticallyGeneratedCode"
	description = "grouping all together automatically generate code tasks"

	dependsOn(
		"openApiGenerate"
	)
}

configure<SourceSetContainer> {
	named("main") {
		java.srcDir("$projectDir/build/generated/src/main/java")
	}
}

springBoot {
	mainClass.value("it.gov.pagopa.payhub.ionotification.IONotificationApplication")
}

openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$rootDir/openapi/p4pa-io-notification.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	apiPackage.set("it.gov.pagopa.payhub.ionotification.controller.generated")
	modelPackage.set("it.gov.pagopa.payhub.ionotification.dto.generated")
	configOptions.set(mapOf(
		"dateLibrary" to "java8",
		"requestMappingMode" to "api_interface",
		"useSpringBoot3" to "true",
		"interfaceOnly" to "true",
		"useTags" to "true",
		"useBeanValidation" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.Builder"
	))
}