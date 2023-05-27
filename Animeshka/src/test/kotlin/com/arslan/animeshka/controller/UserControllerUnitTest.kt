package com.arslan.animeshka.controller

import com.arslan.animeshka.entity.User
import com.arslan.animeshka.UserCredentials
import com.arslan.animeshka.UserRegistration
import com.arslan.animeshka.entity.UserRole
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.ParameterizedTypeReference
import org.springframework.security.authentication.BadCredentialsException


@OptIn(ExperimentalCoroutinesApi::class)
class UserControllerUnitTest : AbstractControllerUnitTest(){

    private val userRegistration = UserRegistration("ValidEmail@email.com","ValidPass123!","ValidPass123!","Username","FirstName","LastName")
    private val userCredentials = UserCredentials("CREDENTIALS","PASSWORD")
    private val user = User("First Name","Last Name","Username","Email","Password",UserRole.USER,1)

    @Test
    fun `should return 200(OK) response after successful user registration`() = runTest {
        coEvery { userService.register(userRegistration) } returns user

        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration)
            .exchange()
            .expectStatus().isOk

        coVerifyAll {
            userService.register(userRegistration)
        }
    }

    @MethodSource("invalidEmails")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to register with invalid email`(invalidEmail: String) = runTest{
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(email = invalidEmail))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.email.Email.message").replace("\${validatedValue}",invalidEmail)
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @Test
    fun `should return 400(Bad Request) when trying to register with blank email`() = runTest{
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(email = ""))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.email.NotBlank.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @MethodSource("invalidPasswords")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to register with invalid password`(invalidPassword: String) {
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(password = invalidPassword, repeatedPassword = invalidPassword))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.password.Pattern.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @MethodSource("blankStrings")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to register with blank username`(blankUsername: String) {
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(username = blankUsername))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.username.NotBlank.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @MethodSource("blankStrings")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to register with blank first name`(blankFirstName: String) {
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(firstName = blankFirstName))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.firstName.NotBlank.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @MethodSource("blankStrings")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to register with blank last name`(blankLastName: String) {
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(lastName = blankLastName))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.lastName.NotBlank.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @Test
    fun `should return 400(Bad Request) when trying to register with repeated password not matching`() {
        webClient
            .post()
            .uri("/user/register")
            .bodyValue(userRegistration.copy(repeatedPassword = userRegistration.password.plus("1")))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserRegistration.EqualRegistrationPasswords.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @MethodSource("blankStrings")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to login with blank credentials`(blankCredentials: String) {
        webClient
            .post()
            .uri("/user/login")
            .bodyValue(userCredentials.copy(credentials = blankCredentials))
            .exchange()
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserCredentials.credentials.message")
            }

        coVerifyAll {
            userService wasNot called
        }
    }

    @MethodSource("blankStrings")
    @ParameterizedTest
    fun `should return 400(Bad Request) when trying to login with blank password`(blankPassword: String) {
        webClient
            .post()
            .uri("/user/login")
            .bodyValue(userCredentials.copy(password = blankPassword))
            .exchange()
            .expectBody(object : ParameterizedTypeReference<List<String>>(){}).value { errors ->
                errors shouldHaveSize 1
                errors shouldContain messageSource.getMessage("UserCredentials.password.message")
            }


        coVerifyAll {
            userService wasNot called
        }
    }

    @Test
    fun `should return 401(Unauthorized) when login throws BadCredentials`() {
        coEvery { userService.login(userCredentials) } throws BadCredentialsException("")

        webClient
            .post()
            .uri("/user/login")
            .bodyValue(userCredentials)
            .exchange()
            .expectStatus().isUnauthorized

        coVerifyAll { userService.login(userCredentials) }
    }

    @Test
    fun `should return 200(OK) response with jwt on successful login`() {
        val expectedToken = "JWT_TOKEN"
        coEvery { userService.login(userCredentials) } returns expectedToken

        webClient
            .post()
            .uri("/user/login")
            .bodyValue(userCredentials)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java).value { actualToken -> actualToken shouldBe expectedToken }

        coVerifyAll { userService.login(userCredentials) }
    }

}