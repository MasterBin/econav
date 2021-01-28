#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.8.6")

import java.io.File

val file = File("/Users/nk/Desktop/allCountries.txt")
val shapes = File("")

val table = listOf(
    "geonameid",
    "name",
    "asciiname",
    "alternatenames",
    "latitude",
    "longitude",
    "feature class",
    "feature code",
    "country code",
    "cc2",
    "admin1 code",
    "admin2 code",
    "admin3 code",
    "admin4 code",
    "population",
    "elevation",
    "dem",
    "timezone",
    "modification date",
)

/*
geonameid         : integer id of record in geonames database
name              : name of geographical point (utf8) varchar(200)
asciiname         : name of geographical point in plain ascii characters, varchar(200)
alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
latitude          : latitude in decimal degrees (wgs84)
longitude         : longitude in decimal degrees (wgs84)
feature class     : see http://www.geonames.org/export/codes.html, char(1)
feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
country code      : ISO-3166 2-letter country code, 2 characters
cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
admin3 code       : code for third level administrative division, varchar(20)
admin4 code       : code for fourth level administrative division, varchar(20)
population        : bigint (8 byte int)
elevation         : in meters, integer
dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
timezone          : the iana timezone id (see file timeZone.txt) varchar(40)
modification date : date of last modification in yyyy-MM-dd format
*/

var vCount = 0
var tCount = 0
var lCount = 0

var vCountRus = 0
var tCountRus = 0
var lCountRus = 0

var rusAll = 0

var collisionCount = 0

file.reader().useLines {
    it.forEachIndexed { index, s ->
        if (index == 5) {
            return@useLines
        }
//        print(index)
//        print("\u001b[H\u001b[2J")
        println(s)

//        val values = s.split("\t")

//        s.split("\t").zip(table).forEach { ss ->
//            println("${ss.second}=${ss.first.ifEmpty { "###" }}")
//        }

//        var isRus = false
//        if (values[8] == "RU") {
//            isRus = true
//            rusAll++
//
//            if (values[15].isNotEmpty() || values[16].isNotEmpty())
//                println("dem=" + values[15] + " ;elevetion=" + values[16])
//        }
//
//        when(values[6]){
//            "T"-> { tCount++; if(isRus) tCountRus++ }
//            "V"-> { vCount++; if(isRus) vCountRus++}
//            "L"-> { lCount++; if(isRus) lCountRus++}
//        }


    }
}

//println("""
//    Results:
//
//    vCount=${vCount}
//    tCount=${tCount}
//    lCount=${lCount}
//
//    vCountRus=${vCountRus}
//    tCountRus=${tCountRus}
//    lCountRus=${lCountRus}
//
//    rusAll=${rusAll}
//""".trimIndent())

/*
Results:

vCount=48709
tCount=1599094
lCount=417426

vCountRus=451
tCountRus=26267
lCountRus=12530

rusAll=361455
*/