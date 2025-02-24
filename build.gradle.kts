plugins {
    id("org.dddjava.jig-gradle-plugin") version "2025.2.3" apply (false)
}

subprojects {
    if (name == "core") {
        apply(plugin = "org.dddjava.jig-gradle-plugin")

        configure<org.dddjava.jig.gradle.JigConfig> {
            modelPattern = "jig\\.erd\\.domain\\..+"
            documentTypes =
                listOf("DomainSummary", "TermTable", "PackageRelationDiagram", "BusinessRuleRelationDiagram")
        }

        tasks.named("jigReports") {
            dependsOn("classes")
        }
    }
}
