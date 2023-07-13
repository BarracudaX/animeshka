CREATE TABLE IF NOT EXISTS USERS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT USERS_UNIQUE_EMAIL_CONSTRAINT UNIQUE(email),
    CONSTRAINT USERS_UNIQUE_USERNAME_CONSTRAINT UNIQUE(username)
);

CREATE TABLE IF NOT EXISTS USER_ROLES(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_role VARCHAR(255) NOT NULL,
    CONSTRAINT UNIQUE_USER_ROLES UNIQUE(user_id,user_role),
    CONSTRAINT FK_USER_ROLES_USERS FOREIGN KEY(user_id) REFERENCES USERS(id)
);

CREATE TABLE IF NOT EXISTS CONTENT(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content JSON NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    creator_id BIGINT NOT NULL,
    content_status VARCHAR(255) NOT NULL,
    verifier BIGINT DEFAULT NULL,
    `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    content_key VARCHAR(255) NOT NULL,
    rejection_reason TEXT DEFAULT NULL,
    CONSTRAINT UNIQUE_CONTENT_CONTENT_KEY UNIQUE(content_key),
    CONSTRAINT FK_CONTENT_CREATOR_USER FOREIGN KEY(creator_id) REFERENCES USERS(id),
    CONSTRAINT FK_CONTENT_ADMINISTRATOR_USER FOREIGN KEY(verifier) REFERENCES USERS(id)
);


CREATE TABLE IF NOT EXISTS IMAGES(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    image_type VARCHAR(255) NOT NULL,
    image_extension VARCHAR(255) NOT NULL,
    CONSTRAINT FK_IMAGES_CONTENT FOREIGN KEY(content_id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS CONTENT_CHANGES(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    content_id BIGINT NOT NULL,
    property_path VARCHAR(100) NOT NULL,
    source_value VARCHAR(255) NOT NULL,
    operation VARCHAR(15) NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP(),
    creator_id BIGINT NOT NULL,
    moderator_id BIGINT DEFAULT NULL,
    CONSTRAINT FK_CONTENT_CHANGES_CONTENT FOREIGN KEY (content_id) REFERENCES CONTENT(id),
    CONSTRAINT FK_CONTENT_CHANGES_CREATOR_USERS FOREIGN KEY(creator_id) REFERENCES USERS(id),
    CONSTRAINT FK_CONTENT_CHANGES_ACCEPTOR_USERS FOREIGN KEY(moderator_id) REFERENCES USERS(id),
    CONSTRAINT UNIQUE_CONTENT_CHANGE UNIQUE(content_id,property_path,operation,source_value)
);

CREATE TABLE IF NOT EXISTS PEOPLE(
    id BIGINT PRIMARY KEY ,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birthdate DATE DEFAULT NULL,
    family_name VARCHAR(255) DEFAULT NULL,
    given_name VARCHAR(255) DEFAULT NULL,
    description TEXT NOT NULL,
    CONSTRAINT FK_PEOPLE_CONTENT FOREIGN KEY (id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS STUDIOS(
    id BIGINT PRIMARY KEY,
    studio_name VARCHAR(255) NOT NULL,
    japanese_name VARCHAR(255) DEFAULT NULL,
    established DATE NOT NULL,
    CONSTRAINT FK_STUDIOS_CONTENT FOREIGN KEY (id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS ANIME_SEASONS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season VARCHAR(255) NOT NULL,
    year YEAR NOT NULL,
    CONSTRAINT ANIME_SEASONS_UNIQUE_SEASON_PER_YEAR UNIQUE(season,year)
);

CREATE TABLE IF NOT EXISTS CHARACTERS(
    id BIGINT PRIMARY KEY ,
    character_name VARCHAR(255) NOT NULL,
    japanese_name VARCHAR(255) DEFAULT NULL,
    description TEXT NOT NULL,
    CONSTRAINT FK_CHARACTERS_CONTENT FOREIGN KEY(id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS NOVEL_MAGAZINES(
    id BIGINT PRIMARY KEY ,
    magazine_name VARCHAR(255) NOT NULL,
    CONSTRAINT FK_NOVEL_MAGAZINES_CONTENT FOREIGN KEY(id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS NOVELS(
    id BIGINT PRIMARY KEY ,
    title VARCHAR(255) NOT NULL,
    japanese_title VARCHAR(255) NOT NULL,
    novel_type VARCHAR(255) NOT NULL,
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
    demographic VARCHAR(255) NOT NULL,
    explicit_genre VARCHAR(255) DEFAULT NULL,
    CONSTRAINT FK_NOVELS_NOVEL_MAGAZINES FOREIGN KEY(magazine) REFERENCES NOVEL_MAGAZINES(id),
    CONSTRAINT FK_NOVELS_CONTENT FOREIGN KEY(id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS NOVEL_CHARACTERS(
    character_id BIGINT NOT NULL,
    novel_id BIGINT NOT NULL,
    character_role VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_CHARACTERS PRIMARY KEY (character_id,novel_id),
    CONSTRAINT FK_NOVEL_CHARACTERS_CHARACTERS FOREIGN KEY(character_id) REFERENCES CHARACTERS(id),
    CONSTRAINT FK_NOVEL_CHARACTERS_NOVELS FOREIGN KEY (novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE IF NOT EXISTS ANIME(
  id BIGINT PRIMARY KEY,
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
  additional_information TEXT,
  CONSTRAINT ANIME_FK_STUDIOS FOREIGN KEY(studio) REFERENCES STUDIOS(id),
  CONSTRAINT ANIME_FK_LICENSORS FOREIGN KEY(licensor) REFERENCES STUDIOS(id),
  CONSTRAINT FK_ANIME_CONTENT FOREIGN KEY(id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS ANIME_CHARACTERS(
    character_id BIGINT NOT NULL,
    anime_id BIGINT NOT NULL,
    voice_actor_id BIGINT NOT NULL,
    character_role VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_CHARACTERS PRIMARY KEY(character_id,anime_id,voice_actor_id),
    CONSTRAINT FK_ANIME_CHARACTERS_CHARACTERS FOREIGN KEY(character_id) REFERENCES CHARACTERS(id),
    CONSTRAINT FK_ANIME_CHARACTERS_ANIME FOREIGN KEY (anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_ANIME_CHARACTERS_PEOPLE FOREIGN KEY(voice_actor_id) REFERENCES PEOPLE(id)
);

CREATE TABLE IF NOT EXISTS ANIME_EPISODES(
    id BIGINT PRIMARY KEY,
    aired DATE NOT NULL,
    anime_id BIGINT NOT NULL,
    episode_name VARCHAR(255) NOT NULL,
    score DECIMAL DEFAULT NULL,
    synopsis TEXT NOT NULL,
    CONSTRAINT ANIME_EPISODES_FK_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_ANIME_EPISODES FOREIGN KEY(id) REFERENCES CONTENT(id)
);

CREATE TABLE IF NOT EXISTS ANIME_EPISODE_CHARACTER_APPEARANCES(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    episode_id BIGINT NOT NULL,
    character_id BIGINT NOT NULL,
    CONSTRAINT FK_ANIME_EPISODE_CHARACTER_APPEARANCES_ANIME_EPISODES FOREIGN KEY(episode_id) REFERENCES ANIME_EPISODES(id),
    CONSTRAINT FK_ANIME_EPISODE_CHARACTER_APPEARANCES_CHARACTERS FOREIGN KEY(character_id) REFERENCES CHARACTERS(id)
);

CREATE TABLE IF NOT EXISTS ANIME_GENRES(
    anime_id BIGINT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_GENRES PRIMARY KEY(anime_id,genre),
    CONSTRAINT FK_ANIME_GENRES_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id)
);

CREATE TABLE IF NOT EXISTS NOVEL_GENRES(
    novel_id BIGINT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_GENRES PRIMARY KEY(novel_id,genre),
    CONSTRAINT FK_NOVEL_GENRES_MANGA FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE IF NOT EXISTS ANIME_THEMES(
    anime_id BIGINT NOT NULL,
    theme VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_THEMES PRIMARY KEY (anime_id,theme),
    CONSTRAINT FK_ANIME_THEMES_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id)
);

CREATE TABLE IF NOT EXISTS NOVEL_THEMES(
    novel_id BIGINT NOT NULL,
    theme VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_THEMES PRIMARY KEY(novel_id,theme),
    CONSTRAINT FK_NOVEL_THEMES_NOVELS FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE IF NOT EXISTS NOVEL_ANIME_RELATIONS(
    novel_id BIGINT NOT NULL,
    anime_id BIGINT NOT NULL,
    relation VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_ANIME_RELATIONS PRIMARY KEY (novel_id,anime_id),
    CONSTRAINT FK_NOVEL_ANIME_RELATIONS_ANIME FOREIGN KEY(anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_NOVEL_ANIME_RELATIONS_NOVELS FOREIGN KEY(novel_id) REFERENCES NOVELS(id)
);

CREATE TABLE IF NOT EXISTS ANIME_ANIME_RELATIONS(
    anime_id BIGINT NOT NULL,
    related_anime_id BIGINT NOT NULL,
    relation VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ANIME_ANIME_RELATIONS PRIMARY KEY (anime_id,related_anime_id),
    CONSTRAINT FK_ANIME_ANIME_RELATIONS_ANIME FOREIGN KEY (anime_id) REFERENCES ANIME(id),
    CONSTRAINT FK_ANIME_ANIME_RELATIONS_ANIME_2 FOREIGN KEY (related_anime_id) REFERENCES ANIME(id)
);

CREATE TABLE IF NOT EXISTS NOVEL_NOVEL_RELATIONS(
    novel_id BIGINT NOT NULL,
    related_novel_id BIGINT NOT NULL,
    relation VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOVEL_NOVEL_RELATIONS PRIMARY KEY(novel_id,related_novel_id,relation),
    CONSTRAINT FK_NOVEL_NOVEL_RELATIONS_NOVEL FOREIGN KEY (novel_id) REFERENCES NOVELS(id),
    CONSTRAINT FK_NOVEL_NOVEL_RELATIONS_NOVEL_2 FOREIGN KEY(related_novel_id) REFERENCES NOVELS(id)
);
