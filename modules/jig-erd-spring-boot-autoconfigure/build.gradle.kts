plugins {
    id("java-library")
}

dependencies {
    implementation(project(":modules:jig-erd"))

    compileOnly(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.3"))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-web")

    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("com.h2database:h2:2.3.232")
}