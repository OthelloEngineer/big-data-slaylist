plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.example.Main")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation("org.testng:testng:7.1.0")
    implementation("org.testng:testng:7.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.apache.kafka:kafka-streams:3.9.0")
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("io.confluent:kafka-streams-protobuf-serde:7.8.0")
    testImplementation("org.apache.hadoop:hadoop-hdfs:3.4.1")
    implementation("org.apache.hadoop:hadoop-common:3.4.1")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}