# Kubernetes CronJob Demo using Spring Boot

This project is a Proof of Concept (POC) demonstrating how to run a single Spring Boot application as multiple distinct Kubernetes CronJobs.

## Architecture

Instead of building separate applications/Docker images for different jobs, this project builds exactly **1 Docker image**. The Kubernetes manifests define 3 different CronJobs that reuse this single image:
- `cronjob-10s`: Delays for 10 seconds, then triggers job 1
- `cronjob-30s`: Delays for 30 seconds, then triggers job 2
- `cronjob-50s`: Delays for 50 seconds, then triggers job 3

This is achieved by passing arguments to the Spring Boot application (e.g., `job-10s`) which are then processed dynamically by a **Dynamic Job Registry** system.

The application is configured as a non-web application (`spring.main.web-application-type=none`), meaning it will boot up, execute the requested logic, and immediately shut down. Kubernetes handles the scheduling and cleanup.

### Dynamic Job Registry

Jobs are loaded dynamically using Spring's bean discovery mechanism:
1. **`JobPerformable` interface**: Every job class must implement this interface and override `perform(String... args)`.
2. **`@RegistryKey` annotation**: Annotated on the job class with the matching Kubernetes parameter key (e.g., `@RegistryKey(key = "job-10s")`).
3. **`JobRegistry` component**: Automatically scans all `JobPerformable` beans, parses their `@RegistryKey` annotation on initialization, and indexes them in a map.
4. **`TaskRunner` component**: A `CommandLineRunner` that acts as the entrypoint. It receives the job key as the first command-line argument, looks up the corresponding job from the `JobRegistry`, and runs it. Any subsequent command-line arguments are passed along to the job.


## Prerequisites

- **Java 25**
- **Maven**
- **Docker**
- **MicroK8s** (or another Kubernetes distribution)

## Local Development & Build

1. Build the Spring Boot application using Maven:
   ```bash
   mvn clean package
   ```

2. Build the Docker image:
   ```bash
   docker build -t k8scron-demo:latest .
   ```

## Deployment to MicroK8s

1. Save the Docker image and import it into MicroK8s containerd:
   ```bash
   docker save k8scron-demo:latest | microk8s ctr image import -
   ```
   *(Note: Ensure your user is in the `microk8s` group, or use `sudo` if necessary)*

2. Apply the Kubernetes CronJob manifests:
   ```bash
   microk8s kubectl apply -f k8s/cronjobs.yaml
   ```

## Monitoring

The CronJobs are scheduled to run every minute (`* * * * *`).

Watch the pods spin up:
```bash
microk8s kubectl get pods -w
```

Check the logs of a completed pod to see the Spring Boot output:
```bash
microk8s kubectl logs <pod-name>
```

Manually trigger a job immediately (bypassing the cron schedule):
```bash
microk8s kubectl create job --from=cronjob/cronjob-10s manual-test-job
```

## Cleanup

To stop the jobs from triggering every minute, delete the CronJobs from your cluster:
```bash
microk8s kubectl delete -f k8s/cronjobs.yaml
```

To clean up the leftover "Completed" pods:
```bash
microk8s kubectl delete pods --field-selector=status.phase=Succeeded
```
