-- 1) 카테고리 데이터 삽입
INSERT INTO category (category_id, category_type, created_at, updated_at, is_deleted)
VALUES (1, 'MOVIE', NOW(), NOW(), false),
       (2, 'DRAMA', NOW(), NOW(), false),
       (3, 'ANIMATION', NOW(), NOW(), false),
       (4, 'VARIETY', NOW(), NOW(), false);

-- 2) 장르 데이터 삽입 (genre_id는 PK이므로 고유하게 부여)
INSERT INTO genre (genre_id, genre_type, created_at, updated_at, category_id, is_deleted)
VALUES

    -- ▶ MOVIE 분류에 속하는 영화 장르
    (1, 'ACTION', NOW(), NOW(), 1, false),
    (2, 'FANTASY', NOW(), NOW(), 1, false),
    (3, 'SF', NOW(), NOW(), 1, false),
    (4, 'THRILLER', NOW(), NOW(), 1, false),
    (5, 'MYSTERY', NOW(), NOW(), 1, false),
    (6, 'ADVENTURE', NOW(), NOW(), 1, false),
    (7, 'MUSICAL', NOW(), NOW(), 1, false),
    (8, 'COMEDY', NOW(), NOW(), 1, false),
    (9, 'WESTERN', NOW(), NOW(), 1, false),
    (10, 'ROMANCE', NOW(), NOW(), 1, false),
    (11, 'DRAMA', NOW(), NOW(), 1, false),
    (12, 'HORROR', NOW(), NOW(), 1, false),
    (13, 'DOCUMENTARY', NOW(), NOW(), 1, false),
    (14, 'CRIME', NOW(), NOW(), 1, false),
    (15, 'MARTIAL_ARTS', NOW(), NOW(), 1, false),
    (16, 'HISTORICAL_DRAMA', NOW(), NOW(), 1, false),
    (17, 'ADULT', NOW(), NOW(), 1, false),
    (18, 'KIDS', NOW(), NOW(), 1, false),

    -- ▶ DRAMA 분류에 속하는 드라마 장르
    (19, 'DRAMA', NOW(), NOW(), 2, false),
    (20, 'ROMANCE', NOW(), NOW(), 2, false),
    (21, 'MYSTERY', NOW(), NOW(), 2, false),
    (22, 'THRILLER', NOW(), NOW(), 2, false),
    (23, 'HISTORICAL_DRAMA', NOW(), NOW(), 2, false),

    -- ▶ ANIMATION 분류에 속하는 애니메이션 장르
    (24, 'ANIMATION', NOW(), NOW(), 3, false),
    (25, 'KIDS', NOW(), NOW(), 3, false),

    -- ▶ VARIETY 분류에 속하는 버라이어티 장르
    (26, 'VARIETY', NOW(), NOW(), 4, false),
    (27, 'TALK_SHOW', NOW(), NOW(), 4, false),
    (28, 'SURVIVAL', NOW(), NOW(), 4, false),
    (29, 'REALITY', NOW(), NOW(), 4, false),
    (30, 'STAND_UP_COMEDY', NOW(), NOW(), 4, false);

-- 3) 플랫폼 데이터 삽입
INSERT INTO platform (platform_id, platform_type, created_at, updated_at, is_deleted)
VALUES (1, 'NETFLIX', NOW(), NOW(), false),
       (2, 'TVING', NOW(), NOW(), false),
       (3, 'COUPANG_PLAY', NOW(), NOW(), false),
       (4, 'WAVVE', NOW(), NOW(), false),
       (5, 'DISNEY_PLUS', NOW(), NOW(), false),
       (6, 'WATCHA', NOW(), NOW(), false),
       (7, 'APPLE_TV', NOW(), NOW(), false);


-- 4) 출연진 데이터 삽입
INSERT INTO cast (cast_id, cast_name, cast_image_url, is_deleted, created_at, updated_at)
VALUES (1, '하정우', 'https://example.com/images/ha-jung-woo.jpg', false, NOW(), NOW()),
       (2, '전도연', 'https://example.com/images/jeon-do-yeon.jpg', false, NOW(), NOW()),
       (3, '이병헌', 'https://example.com/images/lee-byung-hun.jpg', false, NOW(), NOW()),
       (4, '김태리', 'https://example.com/images/kim-tae-ri.jpg', false, NOW(), NOW()),
       (5, '송강호', 'https://example.com/images/song-kang-ho.jpg', false, NOW(), NOW());


-- 5) 감독 데이터 삽입
INSERT INTO director (director_id, director_name, director_image_url, is_deleted, created_at,
                      updated_at)
VALUES (1, '봉준호', 'https://example.com/images/bong-joon-ho.jpg', false, NOW(), NOW()),
       (2, '박찬욱', 'https://example.com/images/park-chan-wook.jpg', false, NOW(), NOW()),
       (3, '김지운', 'https://example.com/images/kim-jee-woon.jpg', false, NOW(), NOW()),
       (4, '임상수', 'https://example.com/images/im-sang-soo.jpg', false, NOW(), NOW()),
       (5, '류승완', 'https://example.com/images/ryoo-seung-wan.jpg', false, NOW(), NOW());

