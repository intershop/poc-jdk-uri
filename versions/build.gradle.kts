plugins {

    id("java-platform")
}

group = "com.intershop.icm"

// Define dependency versions for dependency groups
val logbackVersion = "1.2.13"
val guiceVersion = "7.0.0"
val opentracingVersion = "0.33.0"
val prometheusSimpleclientVersion = "0.16.0"
val slf4jVersion = "1.7.36"

dependencies {
    constraints {
        api("org.slf4j:slf4j-api:${slf4jVersion}")
        api("org.slf4j:log4j-over-slf4j:${slf4jVersion}")

        api("ch.qos.logback:logback-classic:${logbackVersion}")
        api("ch.qos.logback:logback-core:${logbackVersion}")
    }
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}
