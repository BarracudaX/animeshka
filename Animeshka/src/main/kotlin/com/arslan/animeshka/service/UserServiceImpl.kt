package com.arslan.animeshka.service

import com.arslan.animeshka.UserRole
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(private val userRepository: UserRepository,private val passwordEncoder: PasswordEncoder,private val jwtService: JwtService) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override suspend fun register(userRegistration: UserRegistration): User {
        val user = with(userRegistration){ User(firstName,lastName,username,email,passwordEncoder.encode(password),
            UserRole.USER) }
        return userRepository.save(user)
    }

    override suspend fun login(userCredentials: UserCredentials): String {
        val user = when(val user = userRepository.findByEmail(userCredentials.credentials)){
            null -> userRepository.findByUsername(userCredentials.credentials)
            else -> user
        }

        if(user == null){
            logger.info("Failed authentication request. Email : ${userCredentials.credentials}. Cause : user not found.")
            throw BadCredentialsException("User with email ${userCredentials.credentials} not found.")
        }

        if(!passwordEncoder.matches(userCredentials.password,user.password)) {
            logger.info("Failed authentication request. Email : ${userCredentials.credentials}. Cause : invalid credentials.")
            throw BadCredentialsException("Invalid Credentials.")
        }

        logger.info("Successful authentication. Email : ${userCredentials.credentials}")
        return jwtService.createToken(user)
    }

}