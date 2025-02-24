plugins {
    id("java-library")
}

java.sourceCompatibility = JavaVersion.VERSION_21

extra["artifactId"] = "jig-erd"

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
