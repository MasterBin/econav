package graphhopper

import com.graphhopper.config.Profile
import com.graphhopper.routing.DefaultWeightingFactory
import com.graphhopper.routing.WeightingFactory
import com.graphhopper.routing.util.EncodingManager
import com.graphhopper.routing.util.PriorityCode
import com.graphhopper.routing.weighting.FastestWeighting
import com.graphhopper.routing.weighting.TurnCostProvider
import com.graphhopper.routing.weighting.Weighting
import com.graphhopper.util.EdgeIteratorState
import com.graphhopper.util.PMap
import kotlin.math.pow

class MyGraphHopper : GraphHopperPostgis() {

    companion object {
        const val ecoParam = "ecoParam"
    }

    override fun createWeightingFactory(): WeightingFactory {

        return object : DefaultWeightingFactory(graphHopperStorage, encodingManager) {

            override fun createWeighting(profile: Profile, requestHints: PMap, disableTurnCosts: Boolean): Weighting {
                val encoder = encodingManager.getEncoder(profile.vehicle)

                return object : FastestWeighting(encoder, requestHints, TurnCostProvider.NO_TURN_COST_PROVIDER) {

                    val pr = encoder.getDecimalEncodedValue(EncodingManager.getKey(encoder, "priority2"))
                    val priorityEnc = encoder.getDecimalEncodedValue(EncodingManager.getKey(encoder, "priority"))
                    val maxPriority: Double = PriorityCode.getFactor(PriorityCode.BEST.value)
                    val minFactor = 1.0 / (0.5 + maxPriority)

                    override fun getMinWeight(distance: Double): Double {
                        return minFactor * super.getMinWeight(distance);
                    }

                    /**
                     * In most cases subclasses should only override this method to change the edge-weight. The turn cost handling
                     * should normally be changed by passing another [TurnCostProvider] implementation to the constructor instead.
                     */
                    override fun calcEdgeWeight(edgeState: EdgeIteratorState, reverse: Boolean): Double {
                        val weight = super.calcEdgeWeight(edgeState, reverse)

                        if (weight.isInfinite())
                            return Double.POSITIVE_INFINITY

                        val ecoPriority = edgeState.get(this.pr)
                        val priority = edgeState.get(this.priorityEnc)

                        val eco = requestHints.getDouble(ecoParam, 1.0)

                        if (ecoPriority == 404.0)
                            return weight / ((50/25 * eco) + priority + 0.5)//weight / (0.5 + priority)

                        if (ecoPriority < 0.0 || ecoPriority > 100.0)
                            error("MyGraphhopper: unsupported edge eco-priority value")

//                        when(priority) {
//                            0.0 -> {}
//                            0.0 -> {}
//                            0.0 -> {}
//                            0.0 -> {}
//
//                        }


                        return when (eco) {
//                            0.0 -> weight / (0.5 + priority)
//                            else -> weight / (0.2 + 1/priority + ((ecoPriority) * eco / 10))
//                            else -> weight * (1/(priority + 0.5)) * (1/(ecoPriority/10 * eco + 0.2))
                            else -> weight / ((ecoPriority/25 * eco) + priority + 0.5)
                        }
                    }

                    override fun getName(): String = "shortest"
                }
            }
        }
    }
}