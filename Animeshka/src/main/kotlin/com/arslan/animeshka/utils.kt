package com.arslan.animeshka

import com.arslan.animeshka.entity.Novel
import io.r2dbc.spi.Row
import kotlinx.datetime.toKotlinLocalDate

inline fun <reified T> Row.getParam(name: String) : T = get(name,T::class.java)!!
