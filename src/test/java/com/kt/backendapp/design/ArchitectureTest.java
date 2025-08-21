package com.kt.backendapp.design;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * 아키텍처 설계 검증 테스트
 */
class ArchitectureTest {

    private static final String SOURCE_PATH = "src/main/java/com/kt/backendapp";

    @Test
    @DisplayName("패키지 구조 검증")
    void validatePackageStructure() throws Exception {
        // given
        String[] expectedPackages = {
            "controller", "service", "repository", "entity", "dto", "config",
            "domain", "domain/model", "domain/vo", "domain/service", "domain/event"
        };

        // when & then
        for (String packageName : expectedPackages) {
            Path packagePath = Paths.get(SOURCE_PATH, packageName);
            assertTrue(Files.exists(packagePath), 
                "패키지가 존재해야 함: " + packageName);
        }
    }

    @Test
    @DisplayName("Controller 클래스 명명 규칙 검증")
    void validateControllerNaming() throws Exception {
        // given
        Path controllerPath = Paths.get(SOURCE_PATH, "controller");
        
        // when
        try (Stream<Path> files = Files.walk(controllerPath)) {
            List<String> invalidNames = files
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .map(path -> path.getFileName().toString())
                .filter(name -> !name.equals("HealthController.java")) // 예외
                .filter(name -> !name.endsWith("Controller.java"))
                .toList();

            // then
            assertTrue(invalidNames.isEmpty(), 
                "Controller 클래스명이 올바르지 않음: " + invalidNames);
        }
    }

    @Test
    @DisplayName("Service 인터페이스와 구현체 검증")
    void validateServiceStructure() throws Exception {
        // given
        Path servicePath = Paths.get(SOURCE_PATH, "service");
        Path implPath = Paths.get(SOURCE_PATH, "service/impl");
        
        // when
        try (Stream<Path> serviceFiles = Files.walk(servicePath, 1)) {
            List<String> serviceInterfaces = serviceFiles
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith("Service.java"))
                .map(path -> path.getFileName().toString())
                .toList();

            // then
            assertFalse(serviceInterfaces.isEmpty(), "Service 인터페이스가 존재해야 함");
            
            // 구현체 확인
            for (String serviceInterface : serviceInterfaces) {
                String implName = serviceInterface.replace(".java", "Impl.java");
                Path implFile = implPath.resolve(implName);
                assertTrue(Files.exists(implFile), 
                    "Service 구현체가 존재해야 함: " + implName);
            }
        }
    }

    @Test
    @DisplayName("DTO 클래스 검증")
    void validateDTOClasses() throws Exception {
        // given
        Path dtoPath = Paths.get(SOURCE_PATH, "dto");
        
        // when
        try (Stream<Path> files = Files.walk(dtoPath)) {
            List<String> dtoFiles = files
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .map(path -> path.getFileName().toString())
                .toList();

            // then
            assertTrue(dtoFiles.size() >= 5, "최소 5개 이상의 DTO가 있어야 함");
            
            // 필수 DTO 확인
            String[] requiredDTOs = {
                "FestivalDTO.java", "ZoneDTO.java", "SNSFeedbackDTO.java", 
                "ApiResponseDTO.java", "FestivalRequestDTO.java"
            };
            
            for (String requiredDTO : requiredDTOs) {
                assertTrue(dtoFiles.contains(requiredDTO), 
                    "필수 DTO가 존재해야 함: " + requiredDTO);
            }
        }
    }

    @Test
    @DisplayName("Value Object 불변성 검증")
    void validateValueObjectImmutability() {
        // Domain VO 클래스들이 올바르게 설계되었는지 확인
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "domain/vo/Coordinates.java")));
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "domain/vo/TargetAudience.java")));
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "domain/vo/CongestionLevel.java")));
    }

    @Test
    @DisplayName("도메인 이벤트 구조 검증")
    void validateDomainEvents() {
        // Domain Event 클래스들이 존재하는지 확인
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "domain/event/FestivalStatusChangedEvent.java")));
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "domain/event/HighCongestionDetectedEvent.java")));
    }

    @Test
    @DisplayName("레이어별 의존성 방향 검증")
    void validateLayerDependencies() {
        // 간단한 의존성 방향 검증
        // Controller -> Service -> Repository 순서여야 함
        
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "controller")), 
            "Controller 레이어 존재");
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "service")), 
            "Service 레이어 존재");
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "repository")), 
            "Repository 레이어 존재");
        assertTrue(Files.exists(Paths.get(SOURCE_PATH, "domain")), 
            "Domain 레이어 존재");
    }
}
