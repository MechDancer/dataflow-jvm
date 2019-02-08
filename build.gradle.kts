import com.novoda.gradle.release.PublishExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("com.novoda:bintray-release:+")
    }
}

plugins {
    kotlin("jvm") version "1.3.21"
    id("org.jetbrains.dokka") version "0.9.17"
}

apply {
    plugin("com.novoda.bintray-release")
}

group = "org.mechdancer"
version = "0.2.0-dev-4"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.1.1")
    implementation("org.mechdancer", "common-extension", "v0.1.0-1")
    testImplementation("junit", "junit", "+")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configure<PublishExtension> {
    userOrg = "mechdancer"
    groupId = "org.mechdancer"
    artifactId = "dataflow-jvm"
    publishVersion = version.toString()
    desc = "from @Microsoft .Net Core: \"System.threading.tasks.dataflow\""
    website = "https://github.com/MechDancer/dataflow-jvm"
    setLicences("WTFPL")
}

task<Jar>("sourceJar") {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

task<Jar>("javadocJar") {
    classifier = "javadoc"
    from("$buildDir/javadoc")
}

tasks.withType<DokkaTask> {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

tasks["javadoc"].dependsOn("dokka")
tasks["jar"].dependsOn("sourceJar")
tasks["jar"].dependsOn("javadocJar")
