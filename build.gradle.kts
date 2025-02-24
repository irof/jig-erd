plugins {
    id("org.dddjava.jig-gradle-plugin") version "2025.2.3" apply (false)
    id("maven-publish")
    id("signing")
}

group = "com.github.irof"
version = System.getenv("VERSION") ?: "0.0.0-SNAPSHOT"

subprojects {
    if (name == "core") {
        plugins.apply("java")
        plugins.apply("maven-publish")
        plugins.apply("signing")
        plugins.apply("org.dddjava.jig-gradle-plugin")

        configure<org.dddjava.jig.gradle.JigConfig> {
            modelPattern = "jig\\.erd\\.domain\\..+"
            documentTypes =
                listOf("DomainSummary", "TermTable", "PackageRelationDiagram", "BusinessRuleRelationDiagram")
        }
        tasks.named("jigReports") {
            dependsOn("classes")
        }
        plugins.withType<JavaPlugin> {
            the<JavaPluginExtension>().toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
        configure<JavaPluginExtension> {
            withJavadocJar()
            withSourcesJar()
        }
    }

    afterEvaluate {
        plugins.withType<MavenPublishPlugin> {
            publishing {
                repositories {
                    maven {
                        name = "ossrh"
                        val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                        credentials(PasswordCredentials::class)
                    }
                }
                publications {
                    create<MavenPublication>("mavenJava") {
                        artifactId = extra["artifactId"] as String

                        from(components["java"])
                        pom {
                            name.set("JIG ERD")
                            description.set("Entity-Relationship Diagram of JIG")
                            url.set("https://github.com/irof/jig-erd")
                            licenses {
                                license {
                                    name.set("The Apache License, Version 2.0")
                                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }
                            developers {
                                developer {
                                    name.set("irof")
                                    email.set("irof.ocean@gmail.com")
                                }
                            }
                            scm {
                                connection.set("scm:git:git://github.com/irof/jig-erd.git")
                                developerConnection.set("scm:git:git://github.com/irof/jig-erd.git")
                                url.set("https://github.com/irof/jig-erd")
                            }
                        }
                    }
                }
            }

            signing {
                sign(publishing.publications["mavenJava"])
            }
        }
    }
}
