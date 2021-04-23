package graphhopper

import com.graphhopper.GraphHopper
import com.graphhopper.GraphHopperConfig
import com.graphhopper.config.Profile
import com.graphhopper.reader.osm.GraphHopperOSM
import com.graphhopper.routing.util.CarFlagEncoder
import com.graphhopper.routing.util.EncodingManager
import com.graphhopper.routing.util.FootFlagEncoder
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.util.*
import ru.nk.econav.graphhopper.FootFlagEncoder2

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
        MyGraphHopper().forServer().apply {

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
                    .setVehicle("foot2")
                    .setWeighting("shortest")
                    .setTurnCosts(false)
            )
//            dataReaderFile = "/Users/nk/Desktop/mo-spe-highways2.osm.pbf"
//            graphHopperLocation = "./graphFolder"
            encodingManager = EncodingManager.create(FootFlagEncoder2())

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