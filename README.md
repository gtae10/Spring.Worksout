# Spring.Worksout

Spring Boot 기반의 운동 루틴 관리 웹 애플리케이션입니다.  
사용자는 회원가입과 로그인을 통해 본인만의 운동 루틴을 관리할 수 있으며, 루틴 그룹을 생성하고 각 그룹에 운동 정보를 등록하는 방식으로 운동 계획을 정리할 수 있습니다.

## 프로젝트 소개

`Spring.Worksout`은 개인 운동 루틴을 체계적으로 관리하기 위한 웹 프로젝트입니다.

운동을 부위별 또는 목적별로 그룹화하고, 각 그룹 안에 운동 종목을 추가하여 사용자가 자신의 운동 계획을 쉽게 확인하고 관리할 수 있도록 하는 것을 목표로 합니다.

## 주요 기능

- 회원가입
- 로그인
- 로그인한 사용자 정보 기반 페이지 접근
- 운동 루틴 그룹 생성
- 운동 루틴 그룹 목록 조회
- 루틴 그룹별 운동 추가
- 운동 루틴 목록 조회
- 운동 루틴 삭제
- H2 데이터베이스를 이용한 데이터 저장

## 기술 스택

### Backend

- Java 17
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation

### Frontend

- Thymeleaf
- HTML
- CSS

### Database

- H2 Database

### Build Tool

- Gradle

## 프로젝트 구조

```text
Spring.Worksout
└── manager
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com.exercise.manager
    │   │   └── resources
    │   │       ├── templates
    │   │       └── application.properties
    │   └── test
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew
    └── gradlew.bat
