#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.8.6")
@file:DependsOn("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.0")
@file:DependsOn("io.github.microutils:kotlin-logging:1.12.0")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.29")

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

//val file = File("/Users/nk/Desktop/тест1.csv")
//val out = File("/Users/nk/Desktop/тест2.csv")

val reader = csvReader {
    charset = "UTF-8"
    delimiter = ';'
    escapeChar = '\\'
    skipEmptyLine = true
}

val csvWriter = csvWriter {
}

reader.open("/Users/nk/Desktop/тест2.csv") {
    csvWriter.open(File("/Users/nk/Desktop/test_id.csv")) {
        readAllAsSequence().forEachIndexed { index, it ->
            this.writeRow(
                if (index == 0)
                    listOf("Question", "Answer")
                else
                    listOf(it[0], "a$index")
            )
        }
    }
}
