/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.3.3/userguide/building_java_projects.html
 */

import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

version = "1.3.1"

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
    }
    test {
        java {
            setSrcDirs(listOf("test"))
        }
    }
}

tasks.test {
    useJUnit()
    testLogging {
        events(
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED,
        )
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
    workingDir(binaryResultsDirectory)
}

tasks.javadoc {
    val overview = "build/docs/overview.html"
    doFirst {
        // Convert markdown tutorial to HTML using pandoc
        val pb = ProcessBuilder(
            "pandoc",
            "doc/fcanvas-tutorial-en.md",
            "-f",
            "markdown-implicit_figures",
            "-s",
            "-M", "document-css=false",
            "--no-highlight",
            "-o", overview)
        ;
        pb.start().waitFor();
    }
    // variable is required because options is a property with a getter
    val o = options
    if (o is StandardJavadocDocletOptions) {
        // enables @pre tag
        o.tags("pre:a:Preconditions: ")
        // includes tutorial in overview page
        o.overview(overview)
        o.header("<img src=\"{@docRoot}/resources/Schlammspringer.png\" alt=\"Mudskipper\"/>")
    }
    doLast{
        // copy mudskipper image to correct folder
        copy {
            from("logo/Schlammspringer.png")
            into("build/docs/javadoc/resources")
        }
        copy {
            from("logo/fcanvas-final.svg")
            into("build/docs/javadoc/resources")
        }
    }
}

// add source files to jar so that students can browse javadoc in IDE
tasks.jar {
    from(sourceSets["main"].getAllSource())
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit test framework.
    testImplementation("junit:junit:4.13.2")
}
