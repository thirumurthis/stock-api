package com.stock.finance.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthenticationResponse {

	private final String jwtToken;
}
