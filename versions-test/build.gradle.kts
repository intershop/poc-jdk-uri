plugins {
    `java-platform`
}

// Define dependency versions for dependency groups
val junitVersion = "5.10.2"
val junitPlatformVersion = "1.10.2"

dependencies {
    constraints {
        api("org.junit.jupiter:junit-jupiter:${junitVersion}")
        api("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
        api("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
        api("org.junit.jupiter:junit-jupiter-params:${junitVersion}")

        api("org.junit.platform:junit-platform-commons:${junitPlatformVersion}")
        api("org.junit.platform:junit-platform-runner:${junitPlatformVersion}")
    }
}
