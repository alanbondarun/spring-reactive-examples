subprojects {
    val ktlint by configurations.creating

    dependencies {
        ktlint("com.pinterest:ktlint:0.43.0")
    }

    val ktlintOutputDir = "${project.buildDir}/reports/ktlint/"
    val ktlintInputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

    val ktlintCheck by tasks.creating(JavaExec::class) {
        description = "Check Kotlin code style."
        classpath = ktlint

        mainClass.set("com.pinterest.ktlint.Main")
        args = listOf("src/**/*.kt")

        inputs.files(ktlintInputFiles)
        outputs.dir(ktlintOutputDir)
    }

    val ktlintFormat by tasks.creating(JavaExec::class) {
        description = "Fix Kotlin code style deviations."
        classpath = ktlint

        mainClass.set("com.pinterest.ktlint.Main")
        args = listOf("-F", "src/**/*.kt")

        inputs.files(ktlintInputFiles)
        outputs.dir(ktlintOutputDir)
    }
}
