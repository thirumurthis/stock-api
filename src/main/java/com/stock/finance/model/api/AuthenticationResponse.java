package com.stock.finance.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthenticationResponse {

	private final String status;
	private final String jwtToken;
	private final String apiKey;
}
