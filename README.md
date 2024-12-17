# 재입고 알림 관리 시스템

상품이 재입고되었을 때, 알림을 설정한 사용자에게 알림을 전송하고 이력을 관리하는 백엔드 애플리케이션입니다.

---

## 비즈니스 요구사항

1. **재입고 회차 관리**  
   - 상품이 재입고될 때 재입고 회차를 1 증가시킵니다.

2. **재입고 알림 전송**  
   - 알림 설정 유저에게 순서대로 알림을 전송합니다.  
   - 알림 이력은 데이터베이스에 저장됩니다.

3. **알림 전송 중단 조건**  
   - 재고가 모두 소진되면 알림 전송을 중단합니다.  
   - 알림 상태는 다음과 같습니다:  
     - `IN_PROGRESS`: 알림 전송 중  
     - `CANCELED_BY_SOLD_OUT`: 재고 소진으로 중단  
     - `CANCELED_BY_ERROR`: 예외 발생으로 중단  
     - `COMPLETED`: 알림 전송 완료  

4. **알림 재전송 (수동)**  
   - 알림 전송이 실패한 경우, 마지막 성공 이후 사용자부터 재전송합니다.

5. **속도 제한**  
   - 초당 최대 500개의 알림만 전송될 수 있도록 속도 제한이 적용됩니다.

---

## API 명세

### 1. 재입고 알림 전송

- **Method**: `POST`  
- **URL**: `/products/{productId}/notifications/re-stock`  
- **설명**: 상품 재입고 시 알림을 설정한 사용자에게 알림을 전송합니다.

### 2. 재입고 알림 재전송 (수동)

- **Method**: `POST`  
- **URL**: `/admin/products/{productId}/notifications/re-stock`  
- **설명**: 알림 전송이 실패한 경우 마지막 성공 이후 사용자부터 재전송합니다.


---

## 테이블 설계

### Product (상품 테이블)
| 컬럼명          | 타입        | 설명          |
|-----------------|-------------|---------------|
| `id`           | BIGINT (PK) | 상품 아이디   |
| `restockCount` | INT         | 재입고 회차   |
| `stock`        | INT         | 재고 수량     |

### ProductNotificationHistory (알림 상태 기록)
| 컬럼명                | 타입        | 설명                      |
|-----------------------|-------------|---------------------------|
| `id`                 | BIGINT (PK) | 기록 ID                   |
| `productId`          | BIGINT (FK) | 상품 아이디               |
| `restockCount`       | INT         | 재입고 회차               |
| `status`             | VARCHAR     | 알림 상태                 |
| `lastNotifiedUserId` | BIGINT      | 마지막 알림 전송 유저 ID  |

### ProductUserNotification (알림 설정 유저)
| 컬럼명       | 타입        | 설명                   |
|--------------|-------------|------------------------|
| `id`        | BIGINT (PK) | 아이디                 |
| `productId` | BIGINT (FK) | 상품 아이디            |
| `userId`    | BIGINT      | 유저 아이디            |
| `isActive`  | BOOLEAN     | 알림 활성화 여부       |
| `createdAt` | DATETIME    | 생성 날짜              |
| `updatedAt` | DATETIME    | 수정 날짜              |

### ProductUserNotificationHistory (알림 이력)
| 컬럼명       | 타입        | 설명                   |
|--------------|-------------|------------------------|
| `id`        | BIGINT (PK) | 기록 ID                |
| `productId` | BIGINT (FK) | 상품 아이디            |
| `userId`    | BIGINT      | 유저 아이디            |
| `restockCount` | INT      | 재입고 회차            |
| `notifiedAt` | DATETIME   | 알림 전송 시간         |

---

## 실행 방법

### 프로젝트 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew clean build

# Docker 컨테이너 실행
docker-compose up --build
```

## 디렉토리 구조

```plaintext
src/
├── main/
│   ├── java/com/example/project/
│   │   ├── controller/         # API 컨트롤러
│   │   ├── entity/             # 엔티티 클래스
│   │   ├── repository/         # 데이터 접근 레이어
│   │   ├── service/            # 비즈니스 로직
│   │   └── ProjectApplication.java  # 메인 애플리케이션 클래스
│   └── resources/
│       ├── application.yml     # 환경 설정 파일
│       └── schema.sql          # DB 초기화 스크립트 (Optional)
└── test/
    ├── java/com/example/project/
    │   ├── controller/         # 컨트롤러 테스트
    │   ├── service/            # 서비스 테스트
    │   └── repository/         # 리포지토리 테스트
    └── resources/        
```

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.4.0  
- **Database**: MySQL 8.0  
- **Build Tool**: Gradle  
- **Rate Limiting**: Resilience4j  
- **Containerization**: Docker, Docker Compose  


