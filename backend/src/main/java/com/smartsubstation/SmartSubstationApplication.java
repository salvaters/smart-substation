package com.smartsubstation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能变电站巡检系统 - 启动类
 *
 * @author Smart Substation Team
 * @since 2024-01-31
 */
@SpringBootApplication
@MapperScan("com.smartsubstation.mapper")
public class SmartSubstationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartSubstationApplication.class, args);
        System.out.println("""

                ========================================
                   智能变电站巡检系统启动成功!
                   API地址: http://localhost:8080/api
                   Druid监控: http://localhost:8080/api/druid
                ========================================
                """);
    }
}
