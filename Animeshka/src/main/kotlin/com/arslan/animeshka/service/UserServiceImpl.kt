package com.arslan.animeshka.service

import com.arslan.animeshka.entity.UserRole
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.entity.UserCredentials
import com.arslan.animeshka.entity.UserRegistration
import com.arslan.animeshka.repository.UserRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional
class UserServiceImpl(private val userRepository: UserRepository,private val passwordEncoder: PasswordEncoder,private val jwtService: JwtService) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override suspend fun register(userRegistration: UserRegistration): User {
        val user = with(userRegistration){ User(firstName,lastName,username,email,passwordEncoder.encode(password),UserRole.USER) }
        return userRepository.save(user)
    }

    override suspend fun login(userCredentials: UserCredentials): String {
        val user = userRepository.findByEmail(userCredentials.email)
            ?:
            kotlin.run {
                logger.info("Failed authentication request. Email : ${userCredentials.email}. Cause : user not found.")
                throw BadCredentialsException("User with email ${userCredentials.email} not found.")
            }

        if(!passwordEncoder.matches(userCredentials.password,user.password)) {
            logger.info("Failed authentication request. Email : ${userCredentials.email}. Cause : invalid credentials.")
            throw BadCredentialsException("Invalid Credentials.")
        }

        logger.info("Successful authentication. Email : ${userCredentials.email}")
        return jwtService.createToken(user)
    }

}