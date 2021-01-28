package ru.nk.econav.security

import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by n.samoylov on 18.11.2020
 */
fun generateUniqueId() : String {
    var uniqueId = 0L

    do {
        val uid = UUID.randomUUID()
        val buffer = ByteBuffer.wrap(ByteArray(16))

        buffer.putLong(uid.leastSignificantBits)
        buffer.putLong(uid.mostSignificantBits)

        val bi = BigInteger(buffer.array())

        uniqueId = bi.toLong()
    }while (uniqueId < 0)

    return uniqueId.toString()
}