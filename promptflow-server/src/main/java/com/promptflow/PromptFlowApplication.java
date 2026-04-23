package com.promptflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PromptFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptFlowApplication.class, args);
        System.out.println("PromptFlow启动成功...");
    }

}
