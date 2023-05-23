CREATE TABLE USERS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT USERS_UNIQUE_EMAIL_CONSTRAINT UNIQUE(email),
    CONSTRAINT USERS_UNIQUE_USERNAME_CONSTRAINT UNIQUE(username)
);

CREATE TABLE PEOPLE(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birthdate DATE DEFAULT NULL,
    family_name VARCHAR(255) NOT NULL,
    given_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE STUDIOS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    studio_name VARCHAR(255) NOT NULL,
    japanese_name VARCHAR(255) NOT NULL,
    established DATE NOT NULL,
    is_verified BOOLEAN NOT NULL  DEFAULT FALSE
);

CREATE TABLE ANIME_SEASONS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season VARCHAR(255) NOT NULL,
    year YEAR NOT NULL,
    CONSTRAINT ANIME_SEASONS_UNIQUE_SEASON_PER_YEAR UNIQUE(season,year)
);

CREATE TABLE CHARACTERS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    character_name VARCHAR(255) NOT NULL,
    japanese_name VARCHAR(255) DEFAULT NULL,
    description TEXT NOT NULL,
    character_role VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL  DEFAULT FALSE
);

CREATE TABLE NOVEL_MAGAZINES(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    magazine_name VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL  DEFAULT FALSE
);

CREATE TABLE NOVELS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    title VARCHAR(255) NOT NULL,
    japanese_title VARCHAR(255) DEFAULT NULL,
    type VARCHAR(255) NOT NULL,
    novel_status VARCHAR(255) NOT NULL,
    volumes INT DEFAULT NULL,
    chapters INT DEFAULT NULL,
    published DATE NOT NULL,
    finished DATE DEFAULT NULL,
    synopsis TEXT NOT NULL,
    background TEXT NOT NULL,
    score DECIMAL DEFAULT NULL,
    novel_rank INT DEFAULT NULL,
    magazine BIGINT DEFAULT NULL,
    demographic VARCHAR(255) DEFAULT NULL,
    explicit_genre VARCHAR(255) DEFAULT NULL,
    is_verified BOOLEAN NOT NULL  DEFAULT FALSE,
    CONSTRAINT FK_NOVELS_NOVEL_MAGAZINES FOREIGN KEY(magazine) REFERENCES NOVEL_MAGAZINES(id)
);

CREATE TABLE NOVEL_CHARACTERS(
    character_id BIGINT NOT NULL,
    novel_id BIGINT NOT NULL,
    CONSTRAINT PK_NOVEL_CHARACTERS PRIMARY KEY (character_id,novel_id),
    CONSTRAINT FK_NOVEL_CHARACTERS_CHARACTERS FOREIGN KEY(character_id) REFERENCES CHARACTERS(id),
    CONSTRAINT FK_NOVEL_CHARACTERS_NOVELS FOREIGN KEY (novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE ANIME(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  anime_status VARCHAR(255) NOT NULL,
  studio BIGINT NOT NULL,
  duration INT DEFAULT NULL,
  episodes_count INT DEFAULT NULL,
  licensor BIGINT DEFAULT NULL,
  season BIGINT DEFAULT NULL,
  title VARCHAR(255) NOT NULL,
  japanese_title VARCHAR(255) NOT NULL,
  published DATE DEFAULT NULL,
  finished DATE DEFAULT NULL,
  synopsis TEXT NOT NULL,
  background TEXT NOT NULL,
  anime_type VARCHAR(255) NOT NULL DEFAULT 'UNKNOWN',
  score DECIMAL DEFAULT NULL,
  anime_rank INT DEFAULT NULL,
  demographic VARCHAR(255) DEFAULT NULL,
  airing_day VARCHAR(255) DEFAULT NULL,
  airing_time TIME DEFAULT NULL,
  rating VARCHAR(255) DEFAULT NULL,
  explicit_genre VARCHAR(255) DEFAULT NULL,
  is_verified BOOLEAN NOT NULL  DEFAULT FALSE,
  additional_information TEXT,
  CONSTRAINT ANIME_FK_STUDIOS FOREIGN KEY(studio) REFERENCES STUDIOS(id),
  CONSTRAINT ANIME_FK_LICENSORS FOREIGN KEY(licensor) REFERENCES STUDIOS(id)
);

CREATE TABLE ANIME_CHARACTERS(
    character_id BIGINT NOT NULL,
    anime_id BIGINT NOT NULL,
    voice_actor_id BIGINT NOT NULL,
    CONSTRAINT PK_ANIME_CHARACTERS PRIMARY KEY(character_id,anime_id,voice_actor_id),
    CONSTRAINT FK_ANIME_CHARACTERS_CHARACTERS FOREIGN KEY(character_id) REFERENCES CHARACTERS(id),
    CONSTRAINT FK_ANIME_CHARACTERS_ANIME FOREIGN KEY (anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_ANIME_CHARACTERS_PEOPLE FOREIGN KEY(voice_actor_id) REFERENCES PEOPLE(id)
);


CREATE TABLE ADAPTATIONS(
    anime_id BIGINT NOT NULL,
    novel_id BIGINT NOT NULL,
    CONSTRAINT PK_ADAPTATIONS PRIMARY KEY(anime_id,novel_id),
    CONSTRAINT FK_ADAPTATIONS_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_ADAPTATIONS_NOVELS FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE ANIME_EPISODES(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aired DATE NOT NULL,
    anime_id BIGINT NOT NULL,
    episode_name VARCHAR(255) NOT NULL,
    score DECIMAL DEFAULT NULL,
    synopsis TEXT NOT NULL,
    is_verified BOOLEAN NOT NULL  DEFAULT FALSE,
    CONSTRAINT ANIME_EPISODES_FK_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id)
);

CREATE TABLE ANIME_EPISODE_CHARACTER_APPEARANCES(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    episode_id BIGINT NOT NULL,
    character_id BIGINT NOT NULL,
    CONSTRAINT FK_ANIME_EPISODE_CHARACTER_APPEARANCES_ANIME_EPISODES FOREIGN KEY(episode_id) REFERENCES ANIME_EPISODES(id),
    CONSTRAINT FK_ANIME_EPISODE_CHARACTER_APPEARANCES_CHARACTERS FOREIGN KEY(character_id) REFERENCES CHARACTERS(id)
);

CREATE TABLE ANIME_GENRES(
    anime_id BIGINT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_GENRES PRIMARY KEY(anime_id,genre),
    CONSTRAINT FK_ANIME_GENRES_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id)
);

CREATE TABLE NOVEL_GENRES(
    novel_id BIGINT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_GENRES PRIMARY KEY(novel_id,genre),
    CONSTRAINT FK_NOVEL_GENRES_MANGA FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE ANIME_THEMES(
    anime_id BIGINT NOT NULL,
    theme VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_THEMES PRIMARY KEY (anime_id,theme),
    CONSTRAINT FK_ANIME_THEMES_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id)
);

CREATE TABLE NOVEL_THEMES(
    novel_id BIGINT NOT NULL,
    theme VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_THEMES PRIMARY KEY(novel_id,theme),
    CONSTRAINT FK_NOVEL_THEMES_NOVELS FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE NOVEL_ANIME_RELATIONS(
    novel_id BIGINT NOT NULL,
    anime_id BIGINT NOT NULL,
    relation VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_ANIME_RELATIONS PRIMARY KEY (novel_id,anime_id),
    CONSTRAINT FK_NOVEL_ANIME_RELATIONS_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_NOVEL_ANIME_RELATIONS_NOVELS FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE ANIME_ANIME_RELATIONS(
    anime_id BIGINT NOT NULL,
    related_anime_id BIGINT NOT NULL,
    relation VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_ANIME_RELATIONS PRIMARY KEY (anime_id,related_anime_id),
    CONSTRAINT FK_ANIME_ANIME_RELATIONS_ANIME FOREIGN KEY (anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_ANIME_ANIME_RELATIONS_ANIME_2 FOREIGN KEY (related_anime_id) REFERENCES ANIME(id)
);

CREATE TABLE NOVEL_NOVEL_RELATIONS(
    novel_id BIGINT NOT NULL,
    related_novel_id BIGINT NOT NULL,
    CONSTRAINT PK_NOVEL_NOVEL_RELATIONS PRIMARY KEY(novel_id,related_novel_id),
    CONSTRAINT FK_NOVEL_NOVEL_RELATIONS_NOVEL FOREIGN KEY (novel_id) REFERENCES NOVELS(id),
    CONSTRAINT FK_NOVEL_NOVEL_RELATIONS_NOVEL_2 FOREIGN KEY(related_novel_id) REFERENCES NOVELS(id)
);