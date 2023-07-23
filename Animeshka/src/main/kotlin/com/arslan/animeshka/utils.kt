package com.arslan.animeshka

import com.arslan.animeshka.repository.PERSON_PREFIX_KEY


inline fun <reified T> io.r2dbc.spi.Readable.getParam(name: String) : T = get(name,T::class.java)!!

val PersonContent.key : String
    get() = "$PERSON_PREFIX_KEY${firstName}_${lastName}_${givenName}_${familyName}"