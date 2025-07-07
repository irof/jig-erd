plugins {
    id("java-library")
}

dependencies {
    implementation(project(":modules:jig-erd"))

    // pomに出力しないようにcompileOnlyにしておく
    compileOnly(platform("org.springframework.boot:spring-boot-dependencies:3.5.3"))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-web")

    // compileOnlyだと見てくれないのでこちらにもplatformがいる
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.3"))
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("com.h2database:h2")
}