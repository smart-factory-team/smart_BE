# 전착도장 장비 이상 감지 (PaintingProcessEquipmentDefectDetection) 서비스 E2E 테스트 가이드

## 1. 개요
이 문서는 `painting-process-data-simulator-service`, `painting-process-equipment-defect-detection-model-service`, 그리고 `paintingprocessmonitoring` 서비스를 Docker 환경에서 함께 실행하고, **이벤트 기반으로 데이터가 전송되고 처리되는 전체 과정**을 안내합니다.

---

## 2. 시스템 아키텍처 및 통신 방식

이 테스트 환경은 네 가지 주요 구성 요소로 이루어집니다.

1.  **`infra` (인프라 서비스):**
    -   `smart_BE/infra/docker-compose.yml`에 정의되어 있습니다.
    -   **PostgreSQL**: 모든 마이크로서비스가 사용하는 공용 데이터베이스입니다. `smart-factory-postgres`라는 컨테이너 이름으로 실행됩니다.
    -   **Kafka**: 서비스 간 비동기 메시지 통신을 담당합니다. `kafka`라는 컨테이너 이름으로 실행됩니다.
    -   모든 인프라 서비스는 `smart-factory-network`라는 공용 Docker 네트워크에 연결됩니다.

2.  **`painting-process-data-simulator-service` (데이터 시뮬레이터):**
    -   FastAPI로 구현된 데이터 생성 및 전송 서비스입니다.
    -   컨테이너 내부에서는 **8011 포트**를 사용하며, 외부에서도 동일한 포트로 접근합니다 (`-p 8011:8011`).
    -   `smart-factory-network`에 연결된 `painting-process-equipment-defect-detection-model-service`의 `/predict` 엔드포인트로 데이터를 전송합니다.

3.  **`painting-process-equipment-defect-detection-model-service` (모델 서비스):**
    -   FastAPI로 구현된 AI 모델 서비스입니다.
    -   시뮬레이터로부터 데이터를 수신하여 결함 여부를 판단합니다.
    -   결함이 감지된 경우에만 `painting-defect-detected` Kafka 토픽으로 메시지를 발행합니다.
    -   컨테이너 내부에서는 **8001 포트**를 사용하며, 외부에서도 동일한 포트로 접근합니다 (`-p 8001:8001`).

4.  **`paintingprocessmonitoring` (모니터링 서비스):**
    -   Spring Boot로 구현된 도장 공정 모니터링 서비스입니다.
    -   `painting-defect-detected` Kafka 토픽을 구독하여 모델 서비스로부터 결함 데이터를 수신합니다.
    -   수신된 결함 데이터를 데이터베이스에 저장합니다.
    -   컨테이너 내부에서는 **8080 포트**를 사용하며, 외부에서는 **8087 포트**를 통해 접근할 수 있도록 설정합니다 (`-p 8087:8080`).

### 중요 통신 설정

-   **컨테이너 간 통신:** 모든 서비스는 동일한 `smart-factory-network`에 속해 있으므로, 각 서비스의 컨테이너 이름을 호스트 이름처럼 사용하여 통신합니다.
-   **Kafka `advertised.listeners`:** Kafka가 다른 컨테이너에게 자신의 접속 정보를 알릴 때 사용하는 주소입니다. `docker-compose.yml`에서 이 값을 `PLAINTEXT://kafka:9092`로 설정해야, 다른 컨테이너들이 `kafka`라는 이름으로 정상적으로 접속할 수 있습니다.

---

## 3. 전체 시스템 실행 가이드

### 1단계: 인프라 실행 및 설정

1.  **Kafka 설정 확인:**
    `smart_BE/infra/docker-compose.yml` 파일에서 `KAFKA_ADVERTISED_LISTENERS` 값이 `PLAINTEXT://kafka:9092`로 설정되어 있는지 확인합니다. (기존 설정이므로 변경할 필요 없을 가능성이 높습니다.)

    ```yaml
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    ```

2.  **인프라 서비스 시작:**
    `smart_BE/infra` 디렉토리에서 아래 명령어를 실행하여 Kafka와 PostgreSQL을 시작합니다.

    ```bash
    cd /workspace/smart_BE/infra
    docker-compose up -d
    ```

### 2단계: `paintingprocessmonitoring` 서비스 설정 및 실행

1.  **Maven 패키징:**
    `paintingprocessmonitoring` 디렉토리로 이동하여 Maven으로 프로젝트를 패키징합니다.

    ```bash
    cd /workspace/smart_BE/paintingprocessmonitoring
    mvn package -B -DskipTests
    ```

2.  **Docker 이미지 빌드:**
    패키징이 완료된 후, 서비스의 Docker 이미지를 빌드합니다.

    ```bash
    docker build -t paintingprocessmonitoring .
    ```

3.  **Docker 컨테이너 실행:**
    빌드된 이미지를 사용하여 Docker 컨테이너를 실행합니다.

    ```bash
    docker run --name paintingprocessmonitoring --network smart-factory-network -p 8087:8080 -e spring.profiles.active=docker paintingprocessmonitoring
    ```

### 3단계: `painting-process-equipment-defect-detection-model-service` 설정 및 실행

1.  **Docker 이미지 빌드:**
    `smart_FAST/services/painting-process-equipment-defect-detection-model-service` 디렉토리로 이동하여 Docker 이미지를 빌드합니다.

    ```bash
    cd /workspace/smart_FAST/services/painting-process-equipment-defect-detection-model-service
    docker build -t painting-process-equipment-defect-detection-model-service .
    ```

2.  **Docker 컨테이너 실행:**
    빌드된 이미지를 사용하여 모델 서비스 컨테이너를 실행합니다. Kafka 접속을 위해 `KAFKA_BOOTSTRAP_SERVERS` 환경 변수를 설정합니다.

    ```bash
    docker run --name painting-process-equipment-defect-detection-model-service --network smart-factory-network -p 8001:8001 -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 painting-process-equipment-defect-detection-model-service
    ```

### 4단계: `painting-process-data-simulator-service` 서비스 설정 및 실행

1.  **`.env` 파일 설정:**
    시뮬레이터가 모델 서비스와 통신할 수 있도록 `smart_FAST/services/painting-process-data-simulator-service` 디렉토리의 `.env` 파일을 수정합니다. `BACKEND_SERVICE_URL`을 모델 서비스의 `/predict` 엔드포인트로 설정합니다.

    ```env
    # /workspace/smart_FAST/services/painting-process-data-simulator-service/.env
    # ... (기존 설정 유지) ...
    BACKEND_SERVICE_URL="http://painting-process-equipment-defect-detection-model-service:8001/predict/"
    # ... (기존 설정 유지) ...
    ```

2.  **Docker 이미지 빌드:**
    시뮬레이터 서비스 디렉토리로 이동하여 Docker 이미지를 빌드합니다.

    ```bash
    cd /workspace/smart_FAST/services/painting-process-data-simulator-service
    docker build -t painting-process-data-simulator-service .
    ```

3.  **Docker 컨테이너 실행:**
    빌드된 이미지를 사용하여 시뮬레이터 컨테이너를 실행합니다.

    ```bash
    docker run --name simulator --network smart-factory-network -p 8011:8011 --env-file .env painting-process-data-simulator-service
    ```

### 5단계: 시뮬레이션 시작 및 전체 흐름 확인

1.  **모든 컨테이너가 실행 중인지 확인:**
    ```bash
    docker ps
    ```
    `paintingprocessmonitoring`, `painting-process-equipment-defect-detection-model-service`, `simulator`, `kafka`, `smart-factory-postgres` 컨테이너가 `Up` 상태여야 합니다.

2.  **시뮬레이션 시작:**
    아래 `curl` 명령어를 실행하여 데이터 전송 시뮬레이션을 시작합니다.

    ```bash
    curl -X POST http://localhost:8011/simulator/start
    ```

---

## 4. 로컬 환경 실행 (참고)
Docker를 사용하지 않고 로컬에서 직접 Spring Boot 애플리케이션을 실행하려면 아래 명령어를 사용합니다.

```bash
mvn spring-boot:run
```