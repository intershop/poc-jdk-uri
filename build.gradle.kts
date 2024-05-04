plugins {
    // IDE plugin
    idea
    // Gradle base plugin
    base
}

// project configuration
description = "JDK Example Encode URI"
group = "com.intershop.jdk"
version = "1.0.0-LOCAL"

val buildDir: File = project.layout.buildDirectory.asFile.get()

repositories {
    mavenCentral()
    mavenLocal()
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories.addAll(rootProject.repositories)

    plugins.withType<JavaPlugin> {

        extensions.getByType(JavaPluginExtension::class.java).apply {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        tasks {
            withType<JavaCompile> {
                options.setFork(true)
                options.setEncoding("UTF-8")
            }
            withType<Javadoc> {
                if (options is StandardJavadocDocletOptions) {
                    val opt = options as StandardJavadocDocletOptions
                    // without the -quiet option, the build fails
                    // opt.addStringOption("Xdoclint:none", "-quiet")
                    opt.links("https://docs.oracle.com/en/java/javase/21/docs/api/")
                    opt.setEncoding("UTF-8")
                }
            }
            withType<Test> {
                useJUnitPlatform()
            }
        }

        dependencies {
            val implementation by configurations
            implementation(platform(project(":versions")))
            val testImplementation by configurations
            testImplementation(platform(project(":versions-test")))
        }
    }
}
