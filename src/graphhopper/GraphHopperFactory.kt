package graphhopper

import com.graphhopper.GraphHopper
import com.graphhopper.GraphHopperConfig
import com.graphhopper.config.Profile
import com.graphhopper.reader.osm.GraphHopperOSM
import com.graphhopper.routing.util.EncodingManager
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.util.*

@OptIn(KtorExperimentalAPI::class)
object GraphHopperFactory {

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())

    private val host = appConfig.property("postgisDB.host").getString()
    private val port = appConfig.property("postgisDB.port").getString()
    private val dbName = appConfig.property("postgisDB.dbName").getString()
    private val dbSchema = appConfig.property("postgisDB.dbSchema").getString()
    private val dbUser = appConfig.property("postgisDB.dbUser").getString()
    private val dbPassword = appConfig.property("postgisDB.dbPassword").getString()

    fun create(): GraphHopper =
        GraphHopperPostgis().forServer().apply {

            init(GraphHopperConfig().apply {
                putObject("db.host", host)
                putObject("db.port", port)
                putObject("db.schema", dbSchema)
                putObject("db.database", dbName)
                putObject("db.user", dbUser)
                putObject("db.passwd", dbPassword)
                putObject("db.tags_to_copy", "priority")

                putObject("datareader.file", "ways")
                putObject("graph.location", "./graphFolder")
                putObject("graph.flag_encoders", "car,foot")
            })

            profiles = listOf(
                Profile("foot")
                    .setVehicle("foot")
                    .setWeighting("shortest")
                    .setTurnCosts(false)
            )

//            dataReaderFile = "/Users/nk/Desktop/mo-spe-highways2.osm.pbf"
//            graphHopperLocation = "./graphFolder"
            encodingManager = EncodingManager.create("car,foot")

            //        profiles = listOf(
//            Profile("car")
//                .setVehicle("car")
//                .setWeighting("fastest")
//                .setTurnCosts(false)
//        )
            routerConfig.apply {
                isCHDisablingAllowed = true
            }

            setMemoryMapped()
            importOrLoad()
        }


}