package ru.nk.econav.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import javax.crypto.SecretKey

/**
 * Created by n.samoylov on 18.11.2020
 */
class JwtFactory(secretKey: String) {
    private val algorithm = Algorithm.HMAC256(secretKey)
    val expiration = 300
    lateinit var date : Date
    val verifier : JWTVerifier = JWT.require(algorithm).build()

    fun sign(userName : String) : String {
        date = Calendar.getInstance().apply {
            this.time = date
            this.roll(Calendar.MINUTE, 5)
        }.time

        return JWT.create()
            .withIssuer(userName)
            .withExpiresAt(date)
            .withClaim("key", userName)
            .withClaim("uniqueId", generateUniqueId())
            .sign(algorithm)
    }
}