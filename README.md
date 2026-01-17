# PostgreSql-demo

PostgreSQL 18과 Spring Boot 4.0을 사용하는 풀스택 프로젝트입니다.

## 프로젝트 구조

```
PostgreSql-demo/
├── backend/          # Spring Boot 백엔드
│   ├── src/
│   ├── build.gradle.kts
│   └── README.md
└── frontend/         # Vue 3 + Vite 프론트엔드
    ├── src/
    └── package.json
```

## 기술 스택

### Backend
- **Java**: 24 (JDK 24)
- **Framework**: Spring Boot 4.0
- **Build**: Gradle (Kotlin DSL)
- **Database**: PostgreSQL 18
- **Persistence**: Spring Data JPA
- **Utility**: Lombok

### Frontend
- **Framework**: Vue 3
- **Build Tool**: Vite
- **Language**: TypeScript
- **State Management**: Pinia
- **Router**: Vue Router

## 실행 방법

### Backend 실행

```bash
cd backend
./gradlew bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:5173`에서 실행됩니다.

## API 엔드포인트

- **POST** `/api/users` - 사용자 생성
- **GET** `/api/users` - 모든 사용자 조회
- **GET** `/api/users/{id}` - ID로 사용자 조회
- **PUT** `/api/users/{id}` - 사용자 정보 수정
- **DELETE** `/api/users/{id}` - 사용자 삭제

## 개발 가이드

### DB 권한 정책

애플리케이션 DB 계정은 **DML만 허용**됩니다.

- ✅ SELECT / INSERT / UPDATE / DELETE
- ❌ CREATE / ALTER / DROP 등 DDL

테이블 생성(DDL)은 별도 계정 또는 DBA가 수행하고, 애플리케이션은 CRUD만 수행합니다.

### 프록시 설정

프론트엔드 개발 서버는 `/api`로 시작하는 요청을 백엔드(`http://localhost:8080`)로 프록시합니다.
