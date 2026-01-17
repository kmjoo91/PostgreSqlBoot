# PostgreSql-demo

PostgreSQL 18과 Spring Boot 4.0을 사용하는 프로젝트입니다.

## 기술 스택

- **Java**: 24 (JDK 24)
- **Framework**: Spring Boot 4.0
- **Build**: Gradle (Kotlin DSL)
- **Database**: PostgreSQL 18
- **Persistence**: Spring Data JPA
- **Utility**: Lombok

## DB 권한 정책

애플리케이션 DB 계정(`demo_user`)은 **DML만 허용**됩니다.

### 허용 작업
- ✅ SELECT / INSERT / UPDATE / DELETE
- ✅ 시퀀스 사용 시 USAGE, SELECT (ID 생성용)

### 금지 작업
- ❌ CREATE / ALTER / DROP 등 DDL

### 운영 방식
- 테이블/시퀀스/인덱스 생성(DDL)은 별도 계정 또는 DBA가 수행
- 애플리케이션(`demo_user`)은 CRUD만 수행
- `application.yml`에서 `spring.jpa.hibernate.ddl-auto: none`으로 설정되어 DDL이 비활성화됩니다.

## 실행

```bash
./gradlew bootRun
```

## 빌드

```bash
./gradlew build
```

