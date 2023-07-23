package com.arslan.animeshka.service

import com.arslan.animeshka.*
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.User
import com.arslan.animeshka.repository.*
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.toKotlinLocalDate
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class  AnimeServiceITest @Autowired constructor (private val studioService: StudioService, ) : AbstractServiceITest(){

    private val animeContent = AnimeContent(
            "test_anime_title","jp_test_anime_title",AnimeStatus.AIRING,SeriesRating.PG_12,-1,Demographic.SEINEN,-1,
            "test_synopsis",AnimeType.ONA,"test_bg","test_ai",setOf(Theme.ADULT_CAST,Theme.BLOOD),setOf(Genre.ACTION,Genre.COMEDY),
            airedAt = LocalDate.now().minusDays(3).toKotlinLocalDate(), duration = 25, explicitGenre = ExplicitGenre.EROTICA, finishedAt = LocalDate.now().toKotlinLocalDate()
    )

}