# Lightweight Microservice Architecture with Docker

This repository outlines the development of a lightweight microservice architecture using Docker containers. The architecture will consist of independent services, such as User Management, Data Storage, Other Services and an API Gateway, each deployed in its own container.

# Overall Architecture of what we have accomplished is highlighted in the figure below
![image](https://github.com/user-attachments/assets/82e68d88-36bf-4c52-b697-b2a92dc7c57f)

# User Management Service [repository-backend](https://github.com/angeliaaju/codeinithackathonnitc/tree/main/usersmanagementmicroservice/backend)
User Management Microservice which allows to add users, modify users, delete users, view user profiles, refresh auth tokens for login. Currently the login and log out functionality is inside this service which would be moved to the parent container app. A microfrontend architecture is the TO-BE state.


# Containerization

The User Management service is containerized using Docker. This provides several benefits, including:
*  Portability: The containerized application can be easily deployed across different environments (development, testing, production) with consistent behavior.
*  Isolation: Containers provide strong isolation between the application and the host system, minimizing conflicts and improving security.
*  Resource Control: Containers allow for fine-grained control over resource allocation (CPU, memory), optimizing resource utilization and preventing resource exhaustion.
  
# Running the Spring Boot Microservice

To run the Spring Boot microservice within a Docker container, follow these steps:

Build the Docker Image: Create a Dockerfile that defines the build process for the Spring Boot application. This typically involves:

*  Base Image: Selecting a suitable base image (e.g., an official Java image or a lightweight image like openjdk:17).
```
FROM openjdk:17
```
*  Application Artifacts: Copying the compiled application (JAR file) and any necessary dependencies into the image.
```
ADD target/usersmanagementsystem-0.0.1-SNAPSHOT.jar usersmanagementsystem-0.0.1-SNAPSHOT.jar
```
*  Entrypoint: Specifying the command to start the Spring Boot application within the container.
```
ENTRYPOINT ["java","-jar","usersmanagementsystem-0.0.1-SNAPSHOT.jar"]
```
*  Build and Push the Image: Build the Docker image using the docker build command and push it to a container registry (e.g., Docker Hub, Google Container Registry) for easy distribution and deployment.
```

#build the docker image
docker build -t usermgmtsvcimg .


#run the docker image
docker run -it -p8080:8080 usermgmtsvcimg:latest

```

# Scaling the Microservice

To scale the User Management service, simply update the number of replicas in the Kubernetes deployment manifest. Kubernetes will automatically create or destroy container instances to match the desired replica count. This is to be implemented in future

This approach provides a robust and scalable foundation for deploying and managing the User Management microservice within a containerized environment.
Scaling the Microservice


# User Management Demo App [repository-frontend](https://github.com/angeliaaju/codeinithackathonnitc/tree/main/usersmanagementmicroservice/frontend)

User Management Demo app allows to test the User Management service. It is an angular app deployed in docker and containerized.

# Running the User Management demo app

1. ```FROM node:18-alpine AS build```

This line defines the base image for the first build stage.
node:18-alpine specifies that the build will be based on the official Node.js 18 image with the Alpine Linux distribution, which is known for its small size.
AS build names this stage "build," allowing you to reference it later in the Dockerfile.

2. ```WORKDIR /app```

Sets the working directory within the container to /app. This is where subsequent commands will be executed.
3. ```COPY package*.json ./```

Copies the package.json and package-lock.json files from the host machine to the /app directory within the container. These files are necessary for installing project dependencies.

4. ```RUN npm install```

Installs the project dependencies specified in the package.json file using the npm install command.

5. ```COPY . .```

Copies all remaining files and directories from the host machine to the /app directory within the container. This includes the source code for the application.

6. ```RUN npm run build```

Executes the build script defined in the package.json file. This typically involves compiling the application code, bundling assets, and generating production-ready files.

7. ```FROM nginx:latest```

Starts a new build stage based on the official Nginx image. This stage will be used to create the final production image.

8. ```COPY --from=build /app/dist/users-management-angular/server /usr/share/nginx/html```

Copies the server-side files generated in the previous stage (/app/dist/users-management-angular/server) to the default Nginx document root (/usr/share/nginx/html).

9. ```COPY --from=build /app/dist/users-management-angular/browser /usr/share/nginx/html```

Copies the client-side files (e.g., HTML, CSS, JavaScript) generated in the previous stage (/app/dist/users-management-angular/browser) to the Nginx document root.

10. ```EXPOSE 80```

Informs Docker that the container will expose port 80 (the default HTTP port).
11. ```CMD ["nginx", "-g", "daemon off;"]```


Specifies the command to run when the container starts.
nginx starts the Nginx web server.
-g "daemon off;" runs Nginx in the foreground within the container, making it easier to interact with and debug.

# Monitoring using Prometheus and Grafana

The services deployed in the containerized environment are continuously monitored using Prometheus and Grafana. The monitoring workflow is shown below

![image](https://github.com/user-attachments/assets/6b462bc6-9af1-48fc-9937-87fec958e7ca)

# Introduction to Prometheus
*  Prometheus is a popular open-source monitoring and alerting system. It collects, stores, and analyzes metrics from various sources like applications, services, operating systems, and hardware devices. It offers insights into performance and system health.

# Introduction to Grafana
*  Grafana is an open-source tool for data visualization, monitoring, and troubleshooting. It creates dashboards to visualize metrics from sources like Prometheus, Elasticsearch, InfluxDB, and CloudWatch. You can customize the dashboards with graphs, tables, charts, and maps to suit your needs.
  
-  Please follow the steps

1. Add the below maven dependencies into the pom.xml file of your spring boot project.

```
 <dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  <dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
   <groupId>io.micrometer</groupId>
   <artifactId>micrometer-registry-prometheus</artifactId>
   <scope>runtime</scope>
  </dependency>
```
2. Enable all the actuator endpoints by adding the below configuration in the application.properties file of your spring boot project.

```
management.endpoints.web.exposure.include=*
management.endpoints.web.security.none={}
```

3. Setting up Prometheus and Grafana using Docker
   
The Docker Compose file configures a Prometheus container to collect metrics from a Spring Boot application using the prometheus.yml file. It also sets up a Grafana container that uses the datasources.yml file to define Prometheus as the default data source for analyzing the collected metrics.
Ensure the path of the prometheus.yml and datasources.yml files are correct according to the docker-compose.yml file path.

![image](https://github.com/user-attachments/assets/d387e258-90e8-4330-811b-72d86db0ca82)

Create docker-compose.yml

```

version: '3.7'

services:
  prometheus:
    image: prom/prometheus:v2.44.0
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus_config/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:9.5.2
    container_name: grafana
    ports:
      - "3000:3000"
    restart: unless-stopped
    volumes:
      - ./grafana/provisioning/datasources:/etc/grafana/provisioning/datasources
```

Create prometheus.yml

```
scrape_configs:
  - job_name: 'MyAppMetrics'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 3s
    static_configs:
      - targets: ['host.docker.internal:8080']
        labels:
          application: 'User Mgmt Service Spring Boot Application'

```
This scrape config instructs Prometheus to scrape the http://localhost:8080/actuator/prometheus endpoint on the host.docker.internal:8080 address every 3 seconds. The application label is used to identify the application in Prometheus.

Create datasources.yml

```
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
```

The data source you defined instructs Grafana to connect with Prometheus using http://prometheus:9090 and use it as the default data source. You can also add Prometheus data source manually after the Grafana container is up.

The services by running the below command where the docker-compose.yml file is placed:

```
docker-compose up
```

Now, Prometheus is accessible via http://localhost:9090 and Grafana is accessible via http://localhost:3000.
