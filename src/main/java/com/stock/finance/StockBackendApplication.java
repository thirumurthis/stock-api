package com.stock.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
// use /swagger-ui/index.html
// Below configuration will update the title in the swagger ui 
@OpenAPIDefinition(info = @Info(title = "Stock API", version = "1.0", description = "Simple Stock information API"))

//open API uses the term security scheme for authentication and authorization scheme
// HTTP Authentication : Basic , Bearer, other HTTP scheme
// API keys in header - cookie authentication
// oAuth2
// OpenID connect Discovery
@SecurityScheme(name = "stockapp-openapi", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
//For openAPI3 use url http://localhost:<port>/v3/api-docs
public class StockBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockBackendApplication.class, args);
	}

}
