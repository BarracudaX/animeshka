package com.arslan.animeshka


inline fun <reified T> io.r2dbc.spi.Readable.getParam(name: String) : T = get(name,T::class.java)!!
