package com.arslan.animeshka

import com.arslan.animeshka.entity.Novel
import io.r2dbc.spi.Row
import kotlinx.datetime.toKotlinLocalDate

inline fun <reified T> Row.getParam(name: String) : T = get(name,T::class.java)!!

fun Novel.toDTO() : NovelDTO = NovelDTO(synopsis,published.toKotlinLocalDate(),title,novelStatus,novelType,explicitGenre,magazine,japaneseTitle,demographic,novelRank,score,background,finished?.toKotlinLocalDate(),chapters,volumes,id)