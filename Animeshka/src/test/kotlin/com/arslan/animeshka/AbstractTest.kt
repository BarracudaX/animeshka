package com.arslan.animeshka

import org.springframework.context.MessageSource
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
abstract class AbstractTest{

    companion object{
        /**
         * Taken from hibernate email validator test.
         */
        @JvmStatic
        fun invalidEmails() = arrayOf(
         "emmanuel.hibernate.org",
         "emma nuel@hibernate.org",
         "emma(nuel@hibernate.org",
         "emmanuel@",
         "emma\nnuel@hibernate.org",
         "emma@nuel@hibernate.org",
         "emma@nuel@.hibernate.org",
         "Just a string",
         "string",
         "me@",
         "@example.com",
         "me.@example.com",
         ".me@example.com",
         "me@example..com",
         "me\\@example.com",
         "Abc.example.com", // (no @ character)
         "A@b@c@example.com", // (only one @ is allowed outside quotation marks)
         "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", // (none of the special characters in this local-part are allowed outside quotation marks)
         "just\"not\"right@example.com", // (quoted strings must be dot separated or the only element making up the local-part)
         "this is\"not\\allowed@example.com", // (spaces, quotes, and backslashes may only exist when within quoted strings and preceded by a backslash)
         "john..doe@example.com", // (double dot before @) with caveat: Gmail lets this through, Email address#Local-part the dots altogether
         "john.doe@example..com"
        )

        @JvmStatic
        fun invalidPasswords() = arrayOf("","7Chars!","NoDigits!","NoSpec1Char","12345678!")

        @JvmStatic
        fun blankStrings() = arrayOf("","   ")
    }

    protected fun MessageSource.getMessage(code: String) : String = getMessage(code, emptyArray(), Locale.getDefault())
}