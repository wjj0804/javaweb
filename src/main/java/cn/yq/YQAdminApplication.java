package cn.yq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("cn.yq.system.mapper")
public class YQAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(YQAdminApplication.class, args);
    }
}

