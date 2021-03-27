package graphhopper

import com.graphhopper.config.Profile
import com.graphhopper.routing.DefaultWeightingFactory
import com.graphhopper.routing.WeightingFactory
import com.graphhopper.routing.util.EncodingManager
import com.graphhopper.routing.weighting.AbstractWeighting
import com.graphhopper.routing.weighting.Weighting
import com.graphhopper.util.EdgeIteratorState
import com.graphhopper.util.PMap

class MyGraphHopper : GraphHopperPostgis() {

    companion object {
        const val ecoParam = "ecoParam"
    }

    override fun createWeightingFactory(): WeightingFactory {

        return object : DefaultWeightingFactory(graphHopperStorage, encodingManager) {

            override fun createWeighting(profile: Profile, requestHints: PMap, disableTurnCosts: Boolean): Weighting {
                val encoder = encodingManager.getEncoder(profile.vehicle)

                return object : AbstractWeighting(encoder) {

                    val pr = encoder.getDecimalEncodedValue(EncodingManager.getKey(encoder, "priority"))
                    /**
                     * Used only for the heuristic estimation in A*
                     *
                     * @return minimal weight for the specified distance in meter. E.g. if you calculate the fastest
                     * way the return value is 'distance/max_velocity'
                     */
                    override fun getMinWeight(distance: Double): Double {
                        return distance
                    }

                    /**
                     * In most cases subclasses should only override this method to change the edge-weight. The turn cost handling
                     * should normally be changed by passing another [TurnCostProvider] implementation to the constructor instead.
                     */
                    override fun calcEdgeWeight(edgeState: EdgeIteratorState, reverse: Boolean): Double {
                        return edgeState.distance * requestHints.getDouble(ecoParam, 1.0) //* edgeState.get(pr)
                    }

                    override fun getName(): String = "asdasd"
                }
            }
        }
    }
}