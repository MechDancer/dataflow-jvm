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
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.dokka") version "0.9.17"
}

apply {
    plugin("com.novoda.bintray-release")
}

group = "org.mechdancer"
version = "0.2.0-dev-6"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "+")
    implementation("org.mechdancer", "common-extension", "+")
    // 单元测试
    testImplementation("junit", "junit", "+")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions { jvmTarget = "1.8" }
}
tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

// 源码导出任务
with("sourcesJar") {
    tasks["jar"].dependsOn(this)
    tasks.register<Jar>(this) {
        archiveClassifier.set("sources")
        group = "build"

        from(sourceSets["main"].allSource)
    }
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

task<Jar>("javadocJar") {
    classifier = "javadoc"
    from("$buildDir/javadoc")
}

tasks.withType<DokkaTask> {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

tasks["javadoc"].dependsOn("dokka")
tasks["jar"].dependsOn("javadocJar")
