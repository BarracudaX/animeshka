package com.arslan.animeshka

import io.r2dbc.spi.Row

inline fun <reified T> Row.getParam(name: String) : T = get(name,T::class.java)!!