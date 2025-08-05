# 

## Model
www.msaez.io/#/117638449/storming/408c7f86f186a69c91693fe51946703a

## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd infra
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- usermanagement
- report
- approvalmanagement
- pressfaultdetection
- chatbot
- assemblyprocessmonitoring
- weldingprocessmonitoring


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- usermanagement
```
 http :8088/userRegisterations id="id"name="name"email="email"password="password"department="department"createdAt="createdAt"
 http :8088/users id="id"email="email"password="password"name="name"department="department"isApproved="isApproved"createdAt="createdAt"updatedAt="updatedAt"
```
- report
```
 http :8088/posts id="id"userId="userId"title="title"content="content"createdAt="createdAt"updatedAt="updatedAt"issue="issue"isSolved="isSolved"
 http :8088/comments id="id"postId="postId"userId="userId"parentId="parentId"content="content"createdAt="createdAt"updatedAt="updatedAt"isDeleted="isDeleted"
 http :8088/reports id="id"postId="postId"reportUrl="reportUrl"
```
- approvalmanagement
```
 http :8088/userApprovals id="id"name="name"email="email"password="password"department="department"createdAt="createdAt"
```
- pressfaultdetection
```
 http :8088/pressDefectDetectionLogs id="id"machineId="machineId"timeStamp="timeStamp"machineName="machineName"itemNo="itemNo"pressTime="pressTime"pressure1="pressure1"pressure2="pressure2"pressure3="pressure3"defectCluster="defectCluster"defectType="defectType"issue="issue"isSolved="isSolved"
 http :8088/pressFaultDetectionLogs id="id"machineId="machineId"timeStamp="timeStamp"ai0Vibration="ai0Vibration"ai1Vibration="ai1Vibration"ai2Current="ai2Current"issue="issue"isSolved="isSolved"
```
- chatbot
```
 http :8088/agentSessions chatbotSessionId="chatbotSessionId"issue="issue"userId="userId"startedAt="startedAt"endedAt="endedAt"isReported="isReported"isTerminated="isTerminated"
 http :8088/issues issue="issue"modelLogId="modelLogId"
```
- assemblyprocessmonitoring
```
 http :8088/vehicleAssemblyProcessDefectDetectionLogs id="id"machineId="machineId"timeStamp="timeStamp"part="part"work="work"category="category"imageUrl="imageUrl"imageName="imageName"imageWidth="imageWidth"imageHeight="imageHeight"issue="issue"isSolved="isSolved"
```
- weldingprocessmonitoring
```
 http :8088/weldingMachineDefectDetectionLogs id="id"machineId="machineId"timeStamp="timeStamp"sensorValue0Ms="sensorValue0ms"sensorValue312Ms="sensorValue3.12ms"sensorValue625Ms="sensorValue6.25ms"sensorValue938Ms="sensorValue9.38ms"sensorValue125Ms="sensorValue12.5ms"sensorValue1562Ms="sensorValue15.62ms"sensorValue1875Ms="sensorValue18.75ms"sensorValue2188Ms="sensorValue21.88ms"sensorValue25Ms="sensorValue25ms"sensorValue2812Ms="sensorValue28.12ms"sensorValue3125Ms="sensorValue31.25ms"sensorValue3438Ms="sensorValue34.38ms"sensorValue375Ms="sensorValue37.5ms"sensorValue4062Ms="sensorValue40.62ms"issue="issue"isSolved="isSolved"
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```
