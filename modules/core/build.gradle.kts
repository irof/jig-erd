plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

java.sourceCompatibility = JavaVersion.VERSION_21

group = "com.github.irof"
version = System.getenv("VERSION") ?: "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation("com.h2database:h2:2.3.232")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.15.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    withSourcesJar()
}

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
            artifactId = "jig-erd"
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