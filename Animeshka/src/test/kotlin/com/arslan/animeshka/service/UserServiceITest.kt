package com.arslan.animeshka.service

import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.Role
import com.arslan.animeshka.repository.UserRoleRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.password.PasswordEncoder

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceITest @Autowired constructor(private val userService: UserService,private val passwordEncoder: PasswordEncoder) : AbstractServiceITest(){

    private val userRegistration = UserRegistration("test@email.com","Pass123!","Pass123!","test_username","test_fn","test_ln")

    @Test
    fun `should register new user`() = runTransactionalTest{
        userRepository.findByUsername(userRegistration.username).shouldBeNull()
        userRepository.findByEmail(userRegistration.email).shouldBeNull()

        val userID = userService.register(userRegistration).id!!

        assertSoftly(userRepository.findByEmail(userRegistration.email)!!) {
            firstName shouldBe userRegistration.firstName
            lastName shouldBe userRegistration.lastName
            email shouldBe userRegistration.email
            username shouldBe userRegistration.username
            passwordEncoder.matches(userRegistration.password,password).shouldBeTrue()
        }
        assertSoftly(userRoleRepository.findAllByUserID(userID).toList().map { it.userRole }) {
            shouldHaveSize(1)
            shouldContain(Role.USER)
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