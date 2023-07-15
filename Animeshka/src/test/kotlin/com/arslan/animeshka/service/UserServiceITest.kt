package com.arslan.animeshka.service

import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.Role
import com.arslan.animeshka.entity.User
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jwt.SignedJWT
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceITest @Autowired constructor(private val userService: UserService,private val passwordEncoder: PasswordEncoder) : AbstractServiceITest(){

    private val userRegistration = UserRegistration("test@email.com","Pass123!","Pass123!","test_username","test_fn","test_ln")

    @Autowired
    private lateinit var expectedJwtAlgorithm:JWSAlgorithm

    @Test
    fun `should register new user with USER role`() = runTransactionalTest{
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

    @Test
    fun `should throw BadCredentialsException when providing invalid email`() = runTransactionalTest {
        userService.register(userRegistration)

        shouldThrow<BadCredentialsException> { userService.authenticate(TestingAuthenticationToken(userRegistration.email.plus("1"),userRegistration.password)).awaitSingle() }
    }

    @Test
    fun `should throw BadCredentialsException when providing invalid username`() = runTransactionalTest{
        userService.register(userRegistration)

        shouldThrow<BadCredentialsException> { userService.authenticate(TestingAuthenticationToken(userRegistration.username.plus("1"),userRegistration.password)).awaitSingle() }
    }

    @Test
    fun `should throw BadCredentialsException when providing invalid password`() = runTransactionalTest{
        userService.register(userRegistration)

        shouldThrow<BadCredentialsException> { userService.authenticate(TestingAuthenticationToken(userRegistration.username,userRegistration.password.plus("1"))).awaitSingle() }
    }

    @Test
    fun `should return BearerTokenAuthenticationToken on successful authentication with email`() = runTransactionalTest{
        val user = userService.register(userRegistration)

        val authentication = userService.authenticate(TestingAuthenticationToken(userRegistration.email,userRegistration.password)).awaitSingle()

        val bearerTokenAuthentication = authentication.shouldBeInstanceOf<BearerTokenAuthenticationToken>()

        bearerTokenAuthentication.token.validateToken(user)
    }


    @Test
    fun `should return BearerTokenAuthenticationToken on successful authentication with username`() = runTransactionalTest{
        val user = userService.register(userRegistration)

        val authentication = userService.authenticate(TestingAuthenticationToken(userRegistration.username,userRegistration.password)).awaitSingle()

        val bearerTokenAuthentication = authentication.shouldBeInstanceOf<BearerTokenAuthenticationToken>()

        bearerTokenAuthentication.token.validateToken(user)
    }

    private fun String.validateToken(user: User){
        assertSoftly(SignedJWT.parse(this)) {
            header.algorithm shouldBe expectedJwtAlgorithm
            jwtClaimsSet.issuer shouldBe "Animeshka"
            jwtClaimsSet.subject shouldBe user.id.toString()
            jwtClaimsSet.claims["scope"] shouldBe "${Role.USER}"
            jwtClaimsSet.audience shouldContainExactlyInAnyOrder listOf("Animeshka")
        }
    }
}