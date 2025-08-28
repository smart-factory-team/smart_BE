# 스마트팩토리 백엔드 시스템

자동차 제조 공정의 스마트팩토리 모니터링 및 관리를 위한 마이크로서비스 기반 백엔드 시스템입니다.

## 시스템 아키텍처

- **마이크로서비스 아키텍처**: Spring Boot 기반의 독립적인 서비스들
- **이벤트 드리븐**: Apache Kafka를 통한 비동기 메시징 시스템
- **API Gateway**: Spring Cloud Gateway를 통한 단일 진입점
- **데이터베이스**: PostgreSQL 기반 다중 데이터베이스 구성
- **컨테이너화**: Docker & Kubernetes 기반 배포

## 서비스 구성

### 인증 및 보안 서비스
- **usermanagement**: 사용자 관리 및 인증
- **approvalmanagement**: 사용자 승인 관리 및 권한 제어
- **gateway**: API 게이트웨이 및 라우팅

### 핵심 AI 서비스
- **chatbot**: AI 기반 챗봇 및 이슈 관리 시스템

### 스마트 모니터링 서비스
- **weldingprocessmonitoring**: 용접 공정 실시간 모니터링 및 결함 감지
- **paintingprocessmonitoring**: 도장 공정 표면 결함 감지
- **paintingequipmentmonitoring**: 도장 장비 상태 모니터링
- **assemblyprocessmonitoring**: 조립 공정 결함 감지 및 모니터링
- **pressfaultdetection**: 프레스 장비 고장 예측 및 감지

### 통합 관리 서비스
- **report**: 리포트 생성 및 게시판 관리

## 기술 스택

- **Framework**: Spring Boot 2.x
- **Message Queue**: Apache Kafka
- **Database**: PostgreSQL
- **Container**: Docker
- **Orchestration**: Kubernetes
- **AI/ML**: 외부 예측 모델 연동 (RESTful API)
- **Real-time Communication**: WebSocket

## 실행 환경 구성

### 1. Kafka 서버 실행
```bash
cd infra
docker-compose up -d
```

### 2. 마이크로서비스 실행
각 서비스별 개별 실행:
```bash
# 예: 사용자 관리 서비스
cd usermanagement
mvn spring-boot:run

# 다른 서비스들도 동일하게 실행
cd ../approvalmanagement && mvn spring-boot:run
cd ../chatbot && mvn spring-boot:run
# ... (각 서비스별 실행)
```

### 3. API Gateway 실행
```bash
cd gateway
mvn spring-boot:run
```

## 주요 기능

- **실시간 공정 모니터링**: WebSocket을 통한 실시간 데이터 스트리밍
- **AI 기반 예측 분석**: 머신러닝 모델을 활용한 결함 예측 및 감지
- **이벤트 드리븐 아키텍처**: Kafka 기반 비동기 이벤트 처리
- **사용자 승인 워크플로**: 다단계 사용자 승인 프로세스
- **통합 리포팅**: 각 공정별 리포트 생성 및 관리

## API 테스트

### 사용자 관리
```bash
# 사용자 등록
http :8088/userRegisterations name="홍길동" email="hong@example.com" password="password123" department="제조부"

# 사용자 조회
http :8088/users/1
```

### 모니터링 서비스
```bash
# 용접 공정 로그 조회
http :8088/weldingMachineDefectDetectionLogs

# 도장 공정 결함 로그
http :8088/paintingSurfaceDefectDetectionLogs

# 프레스 결함 예측
http :8088/pressDefectDetectionLogs
```

## 모니터링

### Kafka 메시지 확인
```bash
cd infra
docker-compose exec kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic [토픽명]
```

## 개발 환경 구성

### 필수 도구
- JDK 11+
- Maven 3.6+
- Docker & Docker Compose
- kubectl (Kubernetes 배포 시)

### 권장 도구
- HTTPie (API 테스트용)
```bash
pip install httpie
```

## 시스템 모니터링 대시보드

각 공정별 실시간 모니터링을 위한 WebSocket 연결:
- 용접 모니터링: `ws://localhost:8088/welding/monitoring`
- 도장 모니터링: `ws://localhost:8088/painting/monitoring`
- 조립 모니터링: `ws://localhost:8088/assembly/monitoring`

## 모델 서버 연동

외부 AI 예측 모델과의 연동을 위한 RESTful API 클라이언트가 각 모니터링 서비스에 포함되어 있습니다.

---

**Model**: www.msaez.io/#/117638449/storming/408c7f86f186a69c91693fe51946703a
