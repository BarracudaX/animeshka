package com.arslan.animeshka.entity

import com.arslan.animeshka.constraints.EqualRegistrationPasswords
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
data class StudioEntry(
    val studioName: String,

    val japaneseName: String,

    val established: LocalDate,

    val id: Long? = null
)

@Serializable
data class AnimeSeasonDTO(val season: Season,val year: Int)

@Serializable
data class AnimeCharacter(val characterID: Long,val voiceActorID: Long)

@Serializable
data class WorkRelation(val workID: Long,val relatedWorkID: Long,val relation: Relation)

@Serializable
data class AnimeEntry(
    val title: String,

    val japaneseTitle: String,

    val status: AnimeStatus,

    val rating: SeriesRating,

    val studio: Long,

    val demographic: Demographic,

    val licensor: Long,

    val season: AnimeSeasonDTO,

    @Transient
    val seasonID: Long = -1,

    val animeType: AnimeType,

    val themes: Set<Theme> = emptySet(),

    val genres: Set<Genre> = emptySet(),

    val novelRelations: Set<WorkRelation> = emptySet(),

    val animeRelations: Set<WorkRelation> = emptySet(),

    val characters: Set<AnimeCharacter> = emptySet(),


    val explicitGenre: ExplicitGenre? = null,

    val airingTime: LocalTime? = null,

    val airingDay: DayOfWeek? = null,

    val duration: Int? = null,

    val episodeCounts: Int? = null,

    val airedAt: LocalDate? = null,

    val finishedAt: LocalDate? = null,

    val novelAdaptations: List<Long> = emptyList()
)