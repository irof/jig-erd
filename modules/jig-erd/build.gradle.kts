plugins {
    id("java-library")
    id("org.dddjava.jig-gradle-plugin") version "2025.3.1"
}

dependencies {
    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation("com.h2database:h2:2.3.232")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")
    testImplementation("org.mockito:mockito-core:5.17.0")
}

tasks.test {
    useJUnitPlatform()
}

configure<org.dddjava.jig.gradle.JigConfig> {
    modelPattern = "jig\\.erd\\.domain\\..+"
    documentTypes =
        listOf("DomainSummary", "TermTable", "PackageRelationDiagram", "BusinessRuleRelationDiagram")
}
tasks.named("jigReports") {
    dependsOn("classes")
}
