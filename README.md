# Image upload service with autoscaling

This Spring Boot service has one business endpoint. It accepts one or more images,
reads each image into a byte array, and stores those arrays in the current process.

## Run locally

Java 17 or newer is required.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot'
mvn spring-boot:run
```

Upload multiple images with the same `files` field:

```powershell
curl.exe -X POST http://localhost:8080/api/images `
  -F "files=@C:\path\first.png" `
  -F "files=@C:\path\second.jpg"
```

The response contains an ID and metadata for each stored byte array, plus the bytes
used/capacity of the responding instance. If the complete batch does not fit, the
request is rejected atomically with HTTP `507 Insufficient Storage`.

## Build and deploy

```powershell
mvn clean package
docker build -t your-registry/image-upload:latest .
docker push your-registry/image-upload:latest
kubectl apply -f k8s.yml
```

The Kubernetes HPA scales from 1 to 10 pods when average memory utilization exceeds
65%. A Metrics Server must be installed in the cluster.

## Production warning

In-memory byte arrays disappear whenever a pod restarts or scales down, and a later
request cannot know which pod contains an image. For a production system, stream
uploads to shared object storage such as Amazon S3, Azure Blob Storage, Google Cloud
Storage, or MinIO. Keep the API instances stateless, then autoscale on CPU, request
rate, or queue depth. The current implementation intentionally demonstrates the
requested per-instance byte-array behavior only.
