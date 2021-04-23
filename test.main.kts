#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.8.6")
@file:DependsOn("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.0")
@file:DependsOn("io.github.microutils:kotlin-logging:1.12.0")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.29")

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

val file = File("/Users/nk/Desktop/1.txt")

