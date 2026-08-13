plugins {
    kotlin("jvm") version libs.versions.kotlin
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version libs.versions.plugin.publish
}

group = "education.cccp"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val functionalTest by sourceSets.creating {
    java.srcDir("src/functionalTest/kotlin")
    resources.srcDir("src/functionalTest/resources")
}

sourceSets.test {
    java.srcDir("src/test/scenarios")
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.kotlin.gradle.plugin)

    testImplementation(platform("education.cccp:workspace-bom:0.0.18"))
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.junit.platform.engine)
    testImplementation(libs.cucumber.java8)
    testImplementation(libs.junit.platform.suite)

    add(functionalTest.implementationConfigurationName, gradleTestKit())
    add(functionalTest.implementationConfigurationName, libs.kotlin.test.junit5)
    add(functionalTest.runtimeOnlyConfigurationName, platform("education.cccp:workspace-bom:0.0.18"))
    add(functionalTest.runtimeOnlyConfigurationName, libs.junit.platform.launcher)
}

val functionalTestTask = tasks.register<Test>("functionalTest") {
    testClassesDirs = functionalTest.output.classesDirs
    classpath = configurations[functionalTest.runtimeClasspathConfigurationName] + functionalTest.output
    useJUnitPlatform()
}

gradlePlugin.testSourceSets.add(functionalTest)
tasks.check { dependsOn(functionalTestTask) }

val cucumberTest = tasks.register<Test>("cucumberTest") {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = configurations.testRuntimeClasspath.get() +
        sourceSets.test.get().output +
        sourceSets.main.get().output +
        files(tasks.jar.get().archiveFile)
    useJUnitPlatform { excludeEngines("junit-jupiter") }
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    shouldRunAfter("test")
    // No .feature files yet — will be added in BUREAU-2+
    failOnNoDiscoveredTests = false
}

tasks.named<Test>("test") {
    filter { excludeTestsMatching("*.scenarios.*") }
}

tasks.check { dependsOn(cucumberTest) }

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

gradlePlugin {
    website.set("https://github.com/cccp-education/bureau-gradle")
    vcsUrl.set("https://github.com/cccp-education/bureau-gradle.git")
    plugins {
        register("bureau") {
            id = "education.cccp.bureau"
            displayName = "Bureau Gradle Plugin"
            description = "Learner entry point CLI/Docker for education.ccp ecosystem — create office, create items via OSS tools, consume purchased training"
            implementationClass = "bureau.BureauPlugin"
            tags = listOf("cccp", "bureau", "office", "learner", "education")
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("Bureau Gradle Plugin")
                description.set("Learner entry point CLI/Docker for education.ccp ecosystem")
                url.set("https://github.com/cccp-education/bureau-gradle")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("cccp-education")
                        name.set("CCCP Education")
                        email.set("cccp.edu@gmail.com")
                    }
                }
                scm {
                    connection.set("https://github.com/cccp-education/bureau-gradle.git")
                    developerConnection.set("https://github.com/cccp-education/bureau-gradle.git")
                    url.set("https://github.com/cccp-education/bureau-gradle")
                }
            }
        }
    }
    repositories {
        mavenCentral()
    }
}

signing {
    if (System.getenv("CI") != "true" && !version.toString().endsWith("-SNAPSHOT")) {
        sign(publishing.publications)
    }
    useGpgCmd()
}