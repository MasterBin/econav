package ru.nk.econav.graphhopper

import com.graphhopper.reader.ReaderWay
import com.graphhopper.routing.ev.EncodedValue
import com.graphhopper.routing.ev.UnsignedDecimalEncodedValue
import com.graphhopper.routing.util.EncodingManager
import com.graphhopper.routing.util.FootFlagEncoder
import com.graphhopper.routing.util.PriorityCode
import com.graphhopper.storage.IntsRef


class FootFlagEncoder2 : FootFlagEncoder() {

    private lateinit var priority2 : UnsignedDecimalEncodedValue

    override fun createEncodedValues(registerNewEncodedValue: MutableList<EncodedValue>?, prefix: String?, index: Int) {
        super.createEncodedValues(registerNewEncodedValue, prefix, index)

        priority2 = UnsignedDecimalEncodedValue(EncodingManager.getKey(prefix, "priority2"), 31, 0.01, 1.0, speedTwoDirections)

        registerNewEncodedValue!!.add(
            priority2
        )
    }

    override fun handleWayTags(edgeFlags: IntsRef, way: ReaderWay, access: EncodingManager.Access?): IntsRef {
        super.handleWayTags(edgeFlags, way, access)

        val pr = way.getTag("priority2", 0f)

        priority2.setDecimal(false, edgeFlags, pr.toDouble())

        return edgeFlags
    }

    override fun toString(): String = "foot2"
}