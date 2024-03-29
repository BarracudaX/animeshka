package com.arslan.animeshka

import java.time.Month

enum class ImageExtension {
    JPEG, JPG, PNG
}

enum class Role {
    USER, STREAMER, NOVEL_ADMINISTRATOR, ANIME_ADMINISTRATOR
}

enum class Demographic { JOSEI, KIDS, SEINEN, SHOUJO, SHOUNEN }

enum class Season(val start: Month, val end: Month) {

    WINTER(Month.JANUARY, Month.MARCH), SPRING(Month.APRIL, Month.JUNE), SUMMER(Month.JULY, Month.SEPTEMBER), FALL(Month.OCTOBER, Month.DECEMBER);

    companion object {
        fun seasonOf(month: Month): Season = Season.values().first { season -> season.start >= month && month <= season.end }

    }
}

enum class CharacterRole { SUPPORTING, PROTAGONIST, ANTAGONIST }

enum class SeriesRating { G, PG_12, R_15, R_18 }

enum class AnimeStatus { NOT_YET_AIRED, AIRING, FINISHED }

enum class NovelType { NOVEL, LIGHT_NOVEL, MANHWA, MANHUA, MANGA, ONE_SHOT, DOUJINSHI }

enum class NovelStatus { PUBLISHING, FINISHED, DISCONTINUED, PAUSED, NOT_YET_PUBLISHED }

enum class AnimeType { UNKNOWN, TV, OVA, ONA, MOVIE, SPECIAL, MUSIC }

enum class Genre { ACTION, ADVENTURE, AVANT_GARDE, BOYS_LOVE, GIRLS_LOVE, COMEDY, DRAMA, FANTASY, HORROR, MYSTERY, ROMANCE, SCI_FI, SLICE_OF_LIFE, SPORTS, SUPERNATURAL, SUSPENSE }

enum class ExplicitGenre { ECCHI, EROTICA, HENTAI }

enum class Theme {
    ADULT_CAST, ANTHROPOMORPHIC, CHILDCARE, COMBAT_SPORTS, CROSS_DRESSING, DELINQUENTS, DETECTIVE, EDUCATIONAL, GAG_HUMOR, BLOOD, FEMALE_HAREM, HIGH_STAKE_GAMES, HISTORICAL, FEMALE_IDOLS, MALE_IDOLS,
    ISEKAI, IYASHIKEI, LOVE_POLYGON, SEX_SHIFT, MAHOU_SHOUJ, MARTIAL_ART, MECHA, MILITARY, MUSIC, MYTHOLOGY, MAFIA, OTAKU, PARODY, PERFORMING_ART, PETS, PSYCHOLOGICAL, RACING, REINCARNATION, MALE_HAREM, SAMURAI, SCHOOL, SHOWBIZ, SPACE,
    STRATEGY_GAME, SUPER_POWER, SURVIVAL, TEAM_SPORTS, TIME_TRAVEL, VAMPIRE, VIDEO_GAME, ART, WORK
}

enum class Relation {
    SEQUEL,
    PREQUEL,
    ALTERNATIVE_SETTING,
    ALTERNATIVE_VERSION,
    SIDE_STORY, SUMMARY,
    FULL_STORY,
    PARENT_STORY,
    SPIN_OFF,
    ADAPTATION,
    OTHER
}

enum class ContentType { ANIME, NOVEL, CHARACTER, PERSON, STUDIO, MAGAZINE, EPISODE }

enum class ContentStatus { PENDING_VERIFICATION, UNDER_VERIFICATION, VERIFIED, REJECTED, EDITING }

enum class ContentChangeOperation { REPLACE, ADD, REMOVE }

enum class ContentChangeStatus { ACCEPTED, REJECTED, PENDING }

enum class ImageType { POSTER, GALLERY }