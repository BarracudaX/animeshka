package com.arslan.animeshka.service

import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.UserRole
import com.arslan.animeshka.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.password.PasswordEncoder

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceITest @Autowired constructor(private val userService: UserService,private val userRepository: UserRepository,private val passwordEncoder: PasswordEncoder) : AbstractServiceITest(){

    private val userRegistration = UserRegistration("test@email.com","Pass123!","Pass123!","test_username","test_fn","test_ln")

    @Test
    fun `should register new user`() = runTransactionalTest{
        userRepository.findByUsername(userRegistration.username).shouldBeNull()
        userRepository.findByEmail(userRegistration.email).shouldBeNull()

        userService.register(userRegistration)

        assertSoftly(userRepository.findByEmail(userRegistration.email)!!) {
            firstName shouldBe userRegistration.firstName
            lastName shouldBe userRegistration.lastName
            email shouldBe userRegistration.email
            username shouldBe userRegistration.username
            role shouldBe UserRole.USER
            passwordEncoder.matches(userRegistration.password,password).shouldBeTrue()
        }
    }

    @Test
    fun `should throw DuplicateKeyException when registering user with email that is already used`() = runTransactionalTest {
        userService.register(userRegistration)

        shouldThrow<DuplicateKeyException> { userService.register(userRegistration.copy(username = "diff_username")) }
    }

    @Test
    fun `should throw DuplicateKeyException when registering user with username that is already used`() = runTransactionalTest{
        userService.register(userRegistration)

        shouldThrow<DuplicateKeyException> { userService.register(userRegistration.copy(email = "diff_email@email.com")) }
    }
}