package com.arslan.animeshka.service

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.UserRoleRepository
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.reduce
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class JwtServiceImpl(private val jwsAlgorithm: JWSAlgorithm,private val jwsSigner: JWSSigner,@Value("\${jwt.token.duration}") private val tokenDuration: Duration,private val userRoleRepository: UserRoleRepository) : JwtService {

    private val header = JWSHeader.Builder(jwsAlgorithm).build()

    override suspend fun createToken(user: User): String {
        val claims = JWTClaimsSet
            .Builder()
            .issuer("Animeshka")
            .subject(user.id!!.toString())
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plus(tokenDuration)))
            .claim("scope",userRoleRepository.findAllByUserID(user.id).fold(""){current,role ->
                if(current === ""){
                    role.userRole.name
                }else{
                    current.plus(" ").plus(role.userRole.name)
                }
            })
            .audience("Animeshka")
            .build()

        return SignedJWT(header,claims).apply { sign(jwsSigner) }.serialize()
    }

}