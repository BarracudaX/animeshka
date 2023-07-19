package com.arslan.animeshka.service

import com.arslan.animeshka.ContentType
import com.arslan.animeshka.entity.Content
import com.arslan.animeshka.entity.Magazine
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MagazineServiceITest @Autowired constructor(private val magazineService: MagazineService): AbstractServiceITest() {

    @Test
    fun `should create magazine entry`() = runTransactionalTest{
        val creatorID = createPlainUser().id!!
        val magazine = Magazine("test",contentRepository.save(Content(creatorID,ContentType.MAGAZINE,"{}","")).id!!)
        magazineRepository.findByMagazineName(magazine.magazineName).shouldBeNull()

        magazineService.insertMagazineEntry(magazine)

        assertSoftly(magazineRepository.findByMagazineName(magazine.magazineName)!!) {
            magazineName shouldBe magazine.magazineName
            id shouldBe magazine.id
        }
    }

}