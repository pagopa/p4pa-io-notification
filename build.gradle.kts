import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import com.github.jk1.license.render.*
import com.github.jk1.license.filter.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "7.2.3.7755"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.openapi.generator") version "7.21.0"
    id("com.gorylenko.gradle-git-properties") version "2.5.7"
    id("com.github.jk1.dependency-license-report") version "3.1.2"
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
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

licenseReport {
    renderers = arrayOf(XmlReportRenderer("third-party-libs.xml", "Back-End Libraries"))
    outputDir = "$projectDir/dependency-licenses"
    filters = arrayOf(SpdxLicenseBundleNormalizer())
}
tasks.classes {
    finalizedBy(tasks.generateLicenseReport)
}

repositories {
    mavenCentral()
}

val springDocOpenApiVersion = "3.0.2"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.10"
val wiremockVersion = "3.13.2"
val micrometerVersion = "1.6.4"
val bouncycastleVersion = "1.83"
val httpClientVersion = "5.6"
val httpCoreVersion = "5.4.2"
val fileUploadVersion = "1.6.0"
val commonsLang3Version = "3.20.0"
val caffeineVersion = "3.2.3"

// fix cve
val jackson3CoreVersion = "3.1.1"

val springCloudDepsVersion = "2025.1.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudDepsVersion")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion") {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("org.codehaus.janino:janino:$janinoVersion")
    implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5:$httpCoreVersion")
    implementation("commons-fileupload:commons-fileupload:$fileUploadVersion")

    //	Testing
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.projectlombok:lombok")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")

    // CVE fix
    implementation("tools.jackson.core:jackson-core:$jackson3CoreVersion")

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
        testLogging.events = setOf(TestLogEvent.FAILED)
        testLogging.exceptionFormat = TestExceptionFormat.FULL
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

tasks.compileJava {
    dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
    group = "AutomaticallyGeneratedCode"
    description = "grouping all together automatically generate code tasks"

    dependsOn(
        "openApiGenerate",
        "openApiGenerateORGANIZATION"
    )
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("$projectDir/build/generated/src/main/java")
    }
}

springBoot {
    buildInfo()
    mainClass.value("it.gov.pagopa.payhub.ionotification.IONotificationApplication")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/openapi/p4pa-io-notification.openapi.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.payhub.ionotification.controller.generated")
    modelPackage.set("it.gov.pagopa.payhub.ionotification.dto.generated")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "requestMappingMode" to "api_interface",
            "useSpringBoot3" to "true",
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useBeanValidation" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "additionalModelTypeAnnotations" to "@lombok.Builder"
        )
    )
}


tasks.register<GenerateTask>("openApiGenerateORGANIZATION") {
    group = "AutomaticallyGeneratedCode"
    description = "openapi"

    generatorName.set("java")
    remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-organization/refs/heads/develop/openapi/generated.openapi.json")
    outputDir.set("$projectDir/build/generated")
    invokerPackage.set("it.gov.pagopa.pu.organization.generated")
    apiPackage.set("it.gov.pagopa.pu.organization.client.generated")
    modelPackage.set("it.gov.pagopa.pu.organization.dto.generated")
    configOptions.set(
        mapOf(
            "swaggerAnnotations" to "false",
            "openApiNullable" to "false",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "useSpringBoot3" to "true",
            "useJakartaEe" to "true",
            "useOneOfInterfaces" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "generateSupportingFiles" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "enumPropertyNaming" to "original",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
    library.set("resttemplate")
}