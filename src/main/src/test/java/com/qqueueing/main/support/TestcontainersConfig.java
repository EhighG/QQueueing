package com.qqueueing.main.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * 테스트용 MongoDB와 Redis를 컨테이너로 띄운다. 컨테이너를 빈으로 두므로 스프링 컨텍스트와 수명을 같이 하고,
 * 같은 컨텍스트를 쓰는 테스트 클래스끼리 컨테이너를 함께 쓴다. 이미지는 src/compose.yml과 같은 버전을 쓴다.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:5.0"));
    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:8.10.2-alpine")).withExposedPorts(6379);
    }
}
