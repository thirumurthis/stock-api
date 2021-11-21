package com.stock.finance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;


//@SpringBootTest
class StockBackendApplicationTests {

	@Test
	public void streamCheckTest() {
		String roles = "ROLE_ADMIN";
		
		List<GrantedAuthority> authList = Arrays.asList(roles.split(",")).stream().map(item -> new SimpleGrantedAuthority(item.toString())).collect(Collectors.toList());
		
		Assert.isTrue(authList.get(0).getAuthority().toString() == "ROLE_ADMIN","Only One role admin to be present");
				
	}
}
