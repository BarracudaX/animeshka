package com.arslan.animeshka.service

import com.arslan.animeshka.UserRole
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.repository.UserRepository
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Transactional
class UserServiceImpl(private val userRepository: UserRepository,private val passwordEncoder: PasswordEncoder,private val jwtService: JwtService) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override suspend fun register(userRegistration: UserRegistration): User {
        val user = with(userRegistration){ User(firstName,lastName,username,email,passwordEncoder.encode(password),
            UserRole.USER) }
        return userRepository.save(user)
    }

    override fun authenticate(authentication: Authentication): Mono<Authentication> = mono{
        val emailOrUsername = authentication.principal?.toString() ?: throw AuthenticationCredentialsNotFoundException("Email/Username is not provided.")
        val password = authentication.credentials?.toString() ?: throw AuthenticationCredentialsNotFoundException("Password is not provided.")

        val user = when(val user = userRepository.findByEmail(emailOrUsername)){
            null -> userRepository.findByUsername(emailOrUsername)
            else -> user
        }

        if(user == null){
            logger.info("Failed authentication request. Email/Username : ${emailOrUsername}. Cause : user not found.")
            throw BadCredentialsException("User with email/username $emailOrUsername not found.")
        }

        if(!passwordEncoder.matches(password,user.password)) {
            logger.info("Failed authentication request. Email/Username : ${emailOrUsername}. Cause : invalid credentials.")
            throw BadCredentialsException("Invalid Credentials.")
        }

        BearerTokenAuthenticationToken(jwtService.createToken(user))
    }


}