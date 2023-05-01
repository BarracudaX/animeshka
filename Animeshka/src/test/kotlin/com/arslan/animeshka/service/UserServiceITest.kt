package com.arslan.animeshka.service

import com.arslan.animeshka.entity.UserCredentials
import com.arslan.animeshka.entity.UserRegistration
import com.arslan.animeshka.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import javax.validation.ConstraintViolationException

class UserServiceITest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtDecoder: ReactiveJwtDecoder
) : AbstractServiceITest() {


    private val registrationForm = UserRegistration("any_em@email.com","any_pw1!","any_pw1!","any_urn","any_fn","any_ln")

    @Test
    fun `should register new user with password encoded`() = runTransactionalTest{
        userRepository.findAll().toList().shouldBeEmpty()

        userService.register(registrationForm)

        val users = userRepository.findAll().toList()

        users.shouldHaveSize(1)
        assertSoftly(users[0]) {
            firstName shouldBe registrationForm.firstName
            lastName shouldBe registrationForm.lastName
            email shouldBe registrationForm.email
            username shouldBe registrationForm.username
            passwordEncoder.matches(registrationForm.password,password).shouldBeTrue()
        }
    }

    @Test
    fun `should throw DataIntegrityViolationException when registering user with username that already exists`() = runTransactionalTest {
        val userID = userService.register(registrationForm).id!!
        val registrationFormWithDuplicateUsername = registrationForm.copy(email = "dif_email@email.com","diff_pw1!","diff_pw1!",registrationForm.username,"diff_fn","diff_ln")

        shouldThrow<DataIntegrityViolationException> { userService.register(registrationFormWithDuplicateUsername) }.message!!.shouldContain(registrationFormWithDuplicateUsername.username)

        assertSoftly(userRepository.findById(userID)!!) {
            firstName shouldBe registrationForm.firstName
            lastName shouldBe registrationForm.lastName
            email shouldBe registrationForm.email
            username shouldBe registrationForm.username
            passwordEncoder.matches(registrationForm.password,password).shouldBeTrue()
        }
    }

    @Test
    fun `should throw DataIntegrityViolationException when registering user with email that already exists`() = runTransactionalTest {
        val userID = userService.register(registrationForm).id!!
        val registrationFormWithDuplicateEmail = registrationForm.copy(registrationForm.email,"diff_pw","diff_username","diff_fn","diff_ln")

        shouldThrow<DataIntegrityViolationException> { userService.register(registrationFormWithDuplicateEmail) }.message!!.shouldContain(registrationFormWithDuplicateEmail.email)

        assertSoftly(userRepository.findById(userID)!!) {
            firstName shouldBe registrationForm.firstName
            lastName shouldBe registrationForm.lastName
            email shouldBe registrationForm.email
            username shouldBe registrationForm.username
            passwordEncoder.matches(registrationForm.password,password).shouldBeTrue()
        }
    }

    @Test
    fun `should throw BadCredentialsException when trying to login with invalid password`() = runTransactionalTest{
        userService.register(registrationForm)

        shouldThrow<BadCredentialsException> { userService.login(UserCredentials(registrationForm.email,registrationForm.password.plus("1"))) }
    }

    @Test
    fun `BadCredentialsException when trying to login with invalid email or username`() = runTransactionalTest{
        userService.register(registrationForm)

        shouldThrow<BadCredentialsException> { userService.login(UserCredentials("NonExisting@email.com",registrationForm.password)) }
    }

    @Test
    fun `should return jws on successful login with email`() = runTransactionalTest{
        val userID = userService.register(registrationForm).id!!

        val token = userService.login(UserCredentials(registrationForm.email,registrationForm.password))

        assertSoftly(jwtDecoder.decode(token).awaitSingle()) {
            headers.shouldContainKey("alg")
            claims.shouldContain("iss","Animeshka")
            claims.shouldContain("sub",userID.toString())
            claims.shouldContain("aud",listOf("Animeshka"))
            claims.shouldContainKey("exp")
            claims.shouldContainKey("iat")
        }
    }

    @Test
    fun `should return jws on successful login with username`() = runTransactionalTest{
        val userID = userService.register(registrationForm).id!!

        val token = userService.login(UserCredentials(registrationForm.username,registrationForm.password))

        assertSoftly(jwtDecoder.decode(token).awaitSingle()) {
            headers.shouldContainKey("alg")
            claims.shouldContain("iss","Animeshka")
            claims.shouldContain("sub",userID.toString())
            claims.shouldContain("aud",listOf("Animeshka"))
            claims.shouldContainKey("exp")
            claims.shouldContainKey("iat")
        }
    }
}