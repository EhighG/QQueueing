package com.example.tes24.config;

import com.example.tes24.controller.MainController;
import com.example.tes24.controller.MemberController;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public MemberController mockMemberController() {
        return Mockito.mock(MemberController.class);
    }

    @Bean
    @Primary
    public MainController mockMainController() {
        return Mockito.mock(MainController.class);
    }
}
