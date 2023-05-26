package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class JwtServiceImpl(private val jwsAlgorithm: JWSAlgorithm,private val jwsSigner: JWSSigner) : JwtService {

    private val header = JWSHeader.Builder(jwsAlgorithm).build()

    private val tokenDuration = Duration.ofHours(24)

    override fun createToken(user: User): String {
        val claims = JWTClaimsSet
            .Builder()
            .issuer("Animeshka")
            .subject(user.id!!.toString())
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plus(tokenDuration)))
            .claim("scope",user.role.name)
            .audience("Animeshka")
            .build()

        return SignedJWT(header,claims).apply { sign(jwsSigner) }.serialize()
    }

}