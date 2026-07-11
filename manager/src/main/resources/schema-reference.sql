-- ============================================================
-- RoutineManager MySQL 스키마 생성 스크립트
-- application.properties의 네이밍 전략(PhysicalNamingStrategyStandardImpl +
-- ImplicitNamingStrategyLegacyJpaImpl) 기준으로 Hibernate가 실제 생성하는
-- 테이블/컬럼명과 동일하게 맞춤 (엔티티 필드명 대소문자 그대로 유지됨)
--
-- 참고: application.properties의 spring.jpa.hibernate.ddl-auto=update가
-- 켜져 있으면 앱 실행 시 Hibernate가 알아서 테이블을 생성/보완하기 때문에
-- 이 스크립트를 꼭 수동 실행할 필요는 없음. 제출용/문서화용으로 필요하면 사용.
-- ============================================================

CREATE DATABASE IF NOT EXISTS routine_manager
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE routine_manager;

-- 회원
CREATE TABLE Member (
    id       VARCHAR(255) NOT NULL,
    name     VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 운동 종목 마스터 (부위별 운동 검색용)
CREATE TABLE WorksOut (
    id      BIGINT NOT NULL AUTO_INCREMENT,
    part    VARCHAR(255),
    workout VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 루틴 그룹 (예: "올라잇 어깨")
CREATE TABLE RoutineGroup (
    id        BIGINT NOT NULL AUTO_INCREMENT,
    title     VARCHAR(255),
    member_id VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT FK_RoutineGroup_Member
        FOREIGN KEY (member_id) REFERENCES Member (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 루틴 그룹에 속한 개별 운동(세트/반복/무게 포함)
CREATE TABLE Routine (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    workout           VARCHAR(255),
    sets              INT NOT NULL,
    reps              INT NOT NULL,
    weight            DOUBLE NOT NULL,
    part              VARCHAR(255),
    member_id         VARCHAR(255),
    worksout_id       BIGINT,
    routine_group_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_Routine_Member
        FOREIGN KEY (member_id) REFERENCES Member (id),
    CONSTRAINT FK_Routine_WorksOut
        FOREIGN KEY (worksout_id) REFERENCES WorksOut (id),
    CONSTRAINT FK_Routine_RoutineGroup
        FOREIGN KEY (routine_group_id) REFERENCES RoutineGroup (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 날짜별로 배정된 루틴 그룹 (캘린더)
CREATE TABLE Calender (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    date             DATE,
    member_id        VARCHAR(255),
    routineGroup_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_Calender_Member
        FOREIGN KEY (member_id) REFERENCES Member (id),
    CONSTRAINT FK_Calender_RoutineGroup
        FOREIGN KEY (routineGroup_id) REFERENCES RoutineGroup (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 실제로 그날 수행한 세트 기록 (calender/detail 화면의 "기록 추가")
CREATE TABLE WorkOutRecord (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    sets         INT NOT NULL,
    reps         INT NOT NULL,
    weight       INT NOT NULL,
    workOut      VARCHAR(255),
    calender_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_WorkOutRecord_Calender
        FOREIGN KEY (calender_id) REFERENCES Calender (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;