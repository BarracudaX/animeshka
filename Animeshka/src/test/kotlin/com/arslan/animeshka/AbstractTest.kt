package com.arslan.animeshka

import org.springframework.test.context.ActiveProfiles

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
         "this\\ still\\\"not\\\\allowed@example.com", // (even if escaped (preceded by a backslash), spaces, quotes, and backslashes must still be contained by quotes)
         "john..doe@example.com", // (double dot before @) with caveat: Gmail lets this through, Email address#Local-part the dots altogether
         "john.doe@example..com"
        )

    }

}