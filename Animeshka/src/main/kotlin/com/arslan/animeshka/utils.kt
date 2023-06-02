package com.arslan.animeshka

import com.arslan.animeshka.entity.Novel
import io.r2dbc.spi.Row
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.http.codec.multipart.FilePart
import java.nio.file.Path
import kotlin.io.path.Path

inline fun <reified T> Row.getParam(name: String) : T = get(name,T::class.java)!!

