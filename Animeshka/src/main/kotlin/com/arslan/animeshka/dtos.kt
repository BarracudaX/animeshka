package com.arslan.animeshka

import com.arslan.animeshka.constraints.EqualRegistrationPasswords
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Serializable
@EqualRegistrationPasswords(message = "{UserRegistration.EqualRegistrationPasswords.message}")
data class UserRegistration(

    @field:NotBlank(message = "{UserRegistration.email.NotBlank.message}")
    @field:Email(message = "{UserRegistration.email.Email.message}")
    val email : String,

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
data class AnimeCharacter(val characterID: Long,val voiceActorID: Long,val role: CharacterRole)

@Serializable
data class WorkRelation(val relatedWorkID: Long,val relation: Relation)

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

    val posterPath: String = "",

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

    val posterPath: String,

    val id: Long,

    val background: String = "",

    val airedAt: LocalDate? = null,

    val finishedAt: LocalDate? = null,
)

@Serializable
data class PersonEntry(
    val firstName: String,

    val lastName: String,

    val familyName: String,

    val givenName: String,

    val description: String = "",

    val birthDate: LocalDate,
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

    val posterPath: String,

    val finished: LocalDate? = null,

    val id: Long
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

    val characterRole: CharacterRole,

    val posterPath: String = "",

    val id: Long? = null
)