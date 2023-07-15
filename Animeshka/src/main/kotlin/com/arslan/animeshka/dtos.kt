package com.arslan.animeshka

import com.arslan.animeshka.constraints.EqualRegistrationPasswords
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
@EqualRegistrationPasswords(message = "{UserRegistration.EqualRegistrationPasswords.message}")
data class UserRegistration(

        @field:NotBlank(message = "{UserRegistration.email.NotBlank.message}")
        @field:Email(message = "{UserRegistration.email.Email.message}")
        val email: String,

        @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$", message = "{UserRegistration.password.Pattern.message}")
        val password: String,

        val repeatedPassword: String,

        @field:NotBlank(message = "{UserRegistration.username.NotBlank.message}")
        val username: String,

        @field:NotBlank(message = "{UserRegistration.firstName.NotBlank.message}")
        val firstName: String,

        @field:NotBlank(message = "{UserRegistration.lastName.NotBlank.message}")
        val lastName: String
)

@Serializable
data class UserCredentials(
        @field:NotBlank(message = "{UserCredentials.credentials.message}")
        val credentials: String,

        @field:NotBlank(message = "{UserCredentials.password.message}")
        val password: String
)

@Serializable
data class StudioContent(
        val studioName: String,

        val japaneseName: String,

        val established: LocalDate,

        val id: Long? = null
)

@Serializable
data class AnimeCharacter(val characterID: Long, val voiceActorID: Long, val role: CharacterRole)

@Serializable
data class WorkRelation(val relatedWorkID: Long, val relation: Relation)

@Serializable
data class AnimeContent(
        val title: String,

        val japaneseTitle: String,

        val status: AnimeStatus,

        val rating: SeriesRating,

        val studio: Long,

        val demographic: Demographic,

        val licensor: Long,

        val synopsis: String,

        val animeType: AnimeType,

        val background: String = "",

        val additionalInfo: String = "",

        val themes: Set<Theme> = emptySet(),

        val genres: Set<Genre> = emptySet(),

        val novelRelations: Set<WorkRelation> = emptySet(),

        val animeRelations: Set<WorkRelation> = emptySet(),

        val characters: Set<AnimeCharacter> = emptySet(),

        val explicitGenre: ExplicitGenre? = null,

        val airingTime: LocalTime? = null,

        val airingDay: DayOfWeek? = null,

        val duration: Int? = null,

        val airedAt: LocalDate? = null,

        val finishedAt: LocalDate? = null,

        val id: Long? = null
)

@Serializable
data class AnimeDTO(
        val title: String,

        val japaneseTitle: String,

        val status: AnimeStatus,

        val rating: SeriesRating,

        val studio: Long,

        val demographic: Demographic,

        val licensor: Long,

        val synopsis: String,

        val animeType: AnimeType,

        val background: String = "",

        val additionalInfo: String = "",

        val themes: Set<Theme> = emptySet(),

        val genres: Set<Genre> = emptySet(),

        val novelRelations: Set<WorkRelation> = emptySet(),

        val animeRelations: Set<WorkRelation> = emptySet(),

        val characters: Set<AnimeCharacter> = emptySet(),

        val explicitGenre: ExplicitGenre? = null,

        val airingTime: LocalTime? = null,

        val airingDay: DayOfWeek? = null,

        val duration: Int? = null,

        val airedAt: LocalDate? = null,

        val finishedAt: LocalDate? = null,

        val id: Long
)

@Serializable
data class BasicAnimeDTO(
        val title: String,

        val japaneseTitle: String,

        val status: AnimeStatus,

        val demographic: Demographic,

        val synopsis: String,

        val animeType: AnimeType,

        val id: Long,

        val background: String = "",

        val airedAt: LocalDate? = null,

        val finishedAt: LocalDate? = null,

        val images: Set<Long> = emptySet(),

        val posterID: Long? = null,
)

@Serializable
data class BasicStudioDTO(
        val studioName: String,

        val japaneseName: String?,

        val id: Long,

        val posterID: Long? = null,

        val images: Set<Long> = emptySet(),
)

@Serializable
data class PersonContent(
        val firstName: String,

        val lastName: String,

        val familyName: String? = null,

        val givenName: String? = null,

        val description: String = "",

        val birthDate: LocalDate? = null,

        val id: Long? = null
)

@Serializable
data class AnimeEpisodeEntry(

        val episodeName: String,

        val animeId: Long,

        val aired: LocalDate,

        val synopsis: String
)

@Serializable
data class BasicNovelDTO(
        val title: String,

        val japaneseTitle: String,

        val synopsis: String,

        val published: LocalDate,

        val novelStatus: NovelStatus,

        val novelType: NovelType,

        val demographic: Demographic,

        val background: String,

        val finished: LocalDate? = null,

        val id: Long,

        val images: Set<Long> = emptySet(),

        val posterID: Long? = null
)

@Serializable
data class BasicCharacterDTO(
        val characterName: String,

        val japaneseName: String?,

        val description: String,

        val id: Long,

        val images: Set<Long> = emptySet(),

        val posterID: Long? = null
)

@Serializable
data class BasicPersonDTO(
        val firstName: String,

        val lastName: String,

        val familyName: String? = null,

        val givenName: String? = null,

        val description: String,

        val birthDate: LocalDate?,

        val id: Long,

        val posterID: Long? = null
)

@Serializable
data class NovelContent(
        val title: String,

        val japaneseTitle: String,

        val synopsis: String,

        val published: LocalDate,

        val novelStatus: NovelStatus,

        val novelType: NovelType,

        val demographic: Demographic,

        val background: String,

        val posterPath: String = "",

        val characters: Set<Long> = emptySet(),

        val animeRelations: Set<WorkRelation> = emptySet(),

        val novelRelations: Set<WorkRelation> = emptySet(),

        val themes: Set<Theme> = emptySet(),

        val genres: Set<Genre> = emptySet(),

        val explicitGenre: ExplicitGenre? = null,

        val magazine: Long? = null,

        val finished: LocalDate? = null,

        val chapters: Int? = null,

        val volumes: Int? = null,

        val id: Long? = null
)

@Serializable
data class CharacterContent(
        val characterName: String,

        val japaneseName: String? = null,

        val description: String = "",

        val id: Long? = null
)

@Serializable
data class PagedBasicAnimeDTO(val content: List<BasicAnimeDTO>, val hasNext: Boolean, val hasPrevious: Boolean)

@Serializable
data class PagedBasicStudioDTO(val content: List<BasicStudioDTO>,val hasNext: Boolean,val hasPrevious: Boolean)

@Serializable
data class PagedBasicNovelDTO(val content: List<BasicNovelDTO>, val hasNext: Boolean, val hasPrevious: Boolean)

@Serializable
data class PagedBasicCharacterDTO(val content: List<BasicCharacterDTO>, val hasNext: Boolean, val hasPrevious: Boolean)

@Serializable
data class PagedBasicPersonDTO(val content: List<BasicPersonDTO>,val hasNext: Boolean,val hasPrevious: Boolean)