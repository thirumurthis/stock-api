package com.stock.finance;

//import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;

import com.stock.finance.controller.StockAPIController;
import com.stock.finance.filter.JwtRequestFilter;
import com.stock.finance.model.StockInfo;
import com.stock.finance.service.ComputeStockMetricsService;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.service.StockStoreService;
import com.stock.finance.user.model.CustomUserDetails;

@WebMvcTest(controllers = StockAPIController.class)
//@ExtendWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class TestWithJWTUpdate {

	
	@MockBean
	private StockStoreService stockService;
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	JWTManagerService jwtService;
	
	@MockBean
	CustomUserDetailsService userDetailsService;
	
	@Autowired
	TestRestTemplate restTemplate;
	
	@MockBean
	JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	WebApplicationContext context;
	
	@MockBean
	ComputeStockMetricsService computeStockMetricsService;
	//*
	@Test
	 void should_return_mocked_list_stock() throws Exception {

		mockMvc = MockMvcBuilders.webAppContextSetup(context)
        		//MockMvcBuilders.standaloneSetup(StockAPIController.class)
        		.apply(springSecurity())
        		.addFilters(jwtRequestFilter)
				.build();
		
	        StockInfo stock = new StockInfo(1,"MFST",10.0f,10,"1",true);
	        List<StockInfo> stockList = new ArrayList<>();
	        stockList.add(stock);
	        // when
	        when(stockService.getStocksDetail("MSFT")).thenReturn(stockList);

	        // then
	        MvcResult response = mockMvc.perform(
	        		 get("/stock/v1/get")
	                .contentType(MediaType.APPLICATION_JSON)
	                .header("Authorization", "Bearer "+jwtService.generateTokenWithApi("user","TESTAPIKEY")))
	                .andExpect(status().isOk())
	               // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	                .andReturn();

	        String responseBodyJson = response.getResponse().getContentAsString();
	        System.out.println("This is resposne ==== "+responseBodyJson);
//	        User responseUser = jsonMapper.readValue(responseBodyJson, User.class);

//	        assertThat(responseUser.getId(), is(equalTo(expectedUser.getId())));
//	        assertThat(responseUser.getEmail(), is(equalTo(expectedUser.getEmail())));
//	        assertThat(responseUser.getPassword(), is(nullValue()));

	        verify(stockService, times(1)).getStocksDetail("user");
	      //  verifyNoMoreInteractions(userService);
	    }
      //*/
	
	@Disabled
    @Test
    
    public void getRatings() {
        //when(tourRatingServiceMock.lookupAll()).thenReturn(Arrays.asList(tourRatingMock, tourRatingMock, tourRatingMock));
    	
    	StockInfo stock = new StockInfo(100,"INTC",10.0f,100.0f,"1",true);
    	List<StockInfo> sList = new ArrayList<>();
    	sList.add(stock);
    	when(userDetailsService.loadUserByUsername("user")).thenReturn(new CustomUserDetails("user", "", true, "TESTAPIKEY", "ROLE_USER"));
    	try {
			when(stockService.getStocksDetail("user")).thenReturn(sList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    	String apiKey = "TESTAPIKEY";
		HttpHeaders headers = new HttpHeaders();
		//headers.setContentType(MediaType.APPLICATION_JSON);
		//String bodyContent = "{\"userName\":\"user\",\"apiKey\":\""+apiKey+"\"}";
		headers.add("Authorization", "Bearer "+jwtService.generateTokenWithApi("user", apiKey));

		//HttpEntity<String> requestEntity = new HttpEntity<>(bodyContent, headers);
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/stock/v1/get", HttpMethod.GET,requestEntity,String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        System.out.println(response.getBody());
       // assertThat(response.getBody().size(), is(3));
    }

	
	
}
