plugins {
    id("java-library")
    id("org.dddjava.jig-gradle-plugin") version "2025.2.3"
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

configure<org.dddjava.jig.gradle.JigConfig> {
    modelPattern = "jig\\.erd\\.domain\\..+"
    documentTypes =
        listOf("DomainSummary", "TermTable", "PackageRelationDiagram", "BusinessRuleRelationDiagram")
}
tasks.named("jigReports") {
    dependsOn("classes")
}
