# 도장 공정 설비 결함 탐지 모니터링 서비스

## 1. 개요

본 서비스는 도장 공정 설비에서 발생하는 센서 데이터를 수신하여, AI 모델을 통해 설비의 이상(결함) 유무를 실시간으로 탐지하고 그 결과를 데이터베이스에 기록하는 역할을 합니다.

전체 시스템은 아래와 같은 이벤트 기반 아키텍처로 구성되어 있습니다.

## 2. 데이터 흐름

1.  **데이터 생성 (Simulator):** 시뮬레이터가 30초마다 센서 데이터를 생성하여 Spring Boot API를 호출합니다.
2.  **이벤트 발행 (Topic A):** Spring Boot는 API 요청을 받아 Kafka 토픽 A (`equipment-data-topic`)로 `EquipmentDataReceived` 이벤트를 발행합니다.
3.  **모델 호출:** Spring Boot의 `EquipmentDataProcessor`가 토픽 A의 이벤트를 구독하여, Python으로 작성된 AI 모델 서비스의 API (`/predict/`)를 호출합니다.
4.  **이상 탐지 (AI Model):** AI 모델은 전달받은 데이터의 이상 여부를 판단합니다.
    *   **정상:** HTTP 204 (No Content)를 응답합니다.
    *   **이상:** `issue` 필드에 결함 원인이 담긴 JSON 데이터를 응답합니다.
5.  **결과 발행 (Topic B):** Spring Boot는 모델로부터 이상 데이터를 응답받은 경우에만, 해당 결과를 Kafka 토픽 B (`model-result-topic`)로 발행합니다. 정상 데이터의 경우 이 단계는 생략됩니다.
6.  **DB 저장:** Spring Boot의 `EquipmentDataProcessor`가 토픽 B의 최종 결과를 구독하여, PostgreSQL 데이터베이스의 `painting_process_equipment_defect_detection_log_table` 테이블에 저장합니다.

## 3. 로컬 환경 테스트 실행 방법

#### 사전 준비
*   Docker가 설치되어 있어야 합니다.

#### Step 0: 공용 인프라 실행 (DB & Kafka)
*   `smart_BE/infra` 폴더로 이동하여 아래 명령어를 실행합니다.
    ```bash
    docker-compose up -d
    ```

#### Step 1: AI 모델 서비스 실행
*   **새 터미널**을 엽니다.
*   `smart_FAST/services/painting-process-equipment-defect-detection-model-service` 폴더로 이동합니다.
*   아래 명령어로 모델 서비스를 **8001번 포트**로 실행합니다.
    ```bash
    uvicorn app.main:app --reload --port 8001
    ```

#### Step 2: 시뮬레이터 서비스 실행
*   **또 다른 새 터미널**을 엽니다.
*   `smart_FAST/services/painting-process-data-simulator-service` 폴더로 이동합니다.
*   아래 명령어로 시뮬레이터 서비스를 **8011번 포트**로 실행합니다.
    ```bash
    uvicorn app.main:app --reload --port 8011
    ```

#### Step 3: 모니터링 서비스 실행 (Spring Boot)
*   **또 다른 새 터미널**을 엽니다.
*   `smart_BE/paintingprocessmonitoring` 폴더로 이동합니다.
*   아래 명령어를 순서대로 입력합니다.
    ```bash
    mvn clean package
    java -jar target/paintingprocessmonitoring-0.0.1-SNAPSHOT.jar
    ```

#### Step 4: 시뮬레이션 시작
*   **마지막 새 터미널**을 엽니다.
*   아래 `curl` 명령어를 입력하여 시뮬레이션을 시작합니다.
    ```bash
    curl -X POST http://localhost:8011/simulator/start
    ```

## 4. 도커 환경 테스트 실행 방법

아래 절차에 따라 각 서비스를 독립된 Docker 컨테이너로 실행하여 전체 데이터 흐름을 테스트합니다.

#### 사전 준비
*   Docker가 설치되어 있어야 합니다.
*   (필요시) `smart_BE/infra/docker-compose.yml` 파일의 `KAFKA_ADVERTISED_LISTENERS` 값을 `PLAINTEXT://kafka:9092`로 수정합니다.

#### Step 1: 공용 인프라 실행 (DB & Kafka)
*   프로젝트 최상위 폴더에서 아래 명령어를 실행합니다.
    ```bash
    docker-compose -f ./smart_BE/infra/docker-compose.yml up -d
    ```

#### Step 2: 서비스별 Docker 이미지 빌드
*   각 서비스의 폴더로 이동하여 `docker build` 명령을 실행합니다.
    ```bash
    # 1. 모니터링 서비스 (Spring Boot)
    # 경로: /workspace/smart_BE/paintingprocessmonitoring
    docker build -t painting-monitoring-service .

    # 2. AI 모델 서비스 (Python)
    # 경로: /workspace/smart_FAST/services/painting-process-equipment-defect-detection-model-service
    docker build -t painting-model-service .

    # 3. 시뮬레이터 서비스 (Python)
    # 경로: /workspace/smart_FAST/services/painting-process-data-simulator-service
    docker build -t painting-simulator-service .
    ```

#### Step 3: 서비스 컨테이너 실행
*   **반드시 아래 순서대로** 컨테이너를 실행해야 합니다.
    ```bash
    # 1. AI 모델 서비스 컨테이너 실행
    docker run --name model-service --network smart-factory-network painting-model-service

    # 2. 모니터링 서비스 컨테이너 실행
    docker run --name painting-process-monitoring-service --network smart-factory-network -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker painting-monitoring-service

    # 3. 시뮬레이터 서비스 컨테이너 실행
    docker run --name simulator-service --network smart-factory-network -p 8011:8011 --env-file ./smart_FAST/services/painting-process-data-simulator-service/.env -e BACKEND_SERVICE_URL=http://painting-process-monitoring-service:8080/equipment-data painting-simulator-service
    ```

#### Step 4: 시뮬레이션 시작
*   모든 컨테이너가 실행되면, 아래 `curl` 명령어로 시뮬레이션을 시작합니다.
    ```bash
    curl -X POST http://localhost:8011/simulator/start
    ```

## 5. 데이터 확인 방법

#### 방법 1: pgAdmin (웹 UI)
1.  브라우저에서 `http://localhost:5050` 으로 접속합니다.
2.  로그인: `admin@smartfactory.com` / `admin123`
3.  서버 등록 후, `smartfactory` DB -> `Schemas` -> `public` -> `Tables` 에서 `painting_process_equipment_defect_detection_log_table` 테이블의 데이터를 확인합니다.

#### 방법 2: psql (터미널)
1.  터미널에서 `docker exec -it smart-factory-postgres psql -U postgres -d smartfactory` 명령어로 DB에 접속합니다.
2.  `SELECT * FROM painting_process_equipment_defect_detection_log_table ORDER BY id DESC LIMIT 10;` 쿼리를 실행하여 최신 데이터를 확인합니다.
3.  `SELECT * FROM paintingprocessequipmentdefectdetectionlog_table ORDER BY id DESC LIMIT 10;` 로컬은 여기 담길수도..
4. `ALTER SEQUENCE paintingprocessequipmentdefectdetectionlog_table_id_seq RESTART WITH 1;` DB 인덱스 1부터 초기화
5. `DELETE FROM paintingprocessequipmentdefectdetectionlog_table;` 테이블 내 데이터 삭제