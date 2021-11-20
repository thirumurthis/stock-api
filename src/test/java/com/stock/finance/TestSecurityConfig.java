package com.stock.finance;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.servlet.configuration.WebMvcSecurityConfiguration;

import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;

//@Import(WebMvcSecurityConfiguration.class)
@Import(WebSecurityConfiguration.class)
public abstract class TestSecurityConfig {

    /**
     * Mocked bean because it's a dependency of the SecurityConfiguration
     */
    @MockBean
    protected CustomUserDetailsService userDetailsService;

    /**
     * Mocked bean because it's a dependency of the SecurityConfiguration
     */
    @MockBean
    protected JWTManagerService jwtService;

}