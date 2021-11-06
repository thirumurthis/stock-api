package com.stock.finance;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.controller.StockAPIController;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockStoreRepository;
import com.stock.finance.service.StockStoreService;

import net.bytebuddy.asm.Advice.OffsetMapping.ForThisReference;

//@SpringBootTest

@WebMvcTest(StockAPIController.class)
public class TestStockAPIRestController {

	@Autowired
	MockMvc mockMvc;
	
	@Test
	@WithMockUser  // we saw this on the method level security test case
	public void allowAll() throws Exception{
		mockMvc.perform(get("/stock/v1/about"))
		       .andExpect(status().isOk());
	 
	}
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	StockStoreRepository stockRepo;
	
	@MockBean
	StockStoreService stockService;
	
	@Test
	@WithMockUser  // we saw this on the method level security test case
	public void postFails() throws Exception{
		
		//Using Builder pattern approach
		//StockInfo stock = StockInfo.builder().symbol("INTC").active(true).stockCount(10).avgStockPrice(100.00f).userId(1).build();
		StockInfo stock = new StockInfo(100,"INTC",10.0f,100.0f,1,true);
		MockHttpServletRequestBuilder request = post("/stock/v1/add")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(stock));
		
		Mockito.when(stockService.storeStockInfo(stock)).thenReturn(stock);
		mockMvc.perform(request)
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$", notNullValue()))
		       .andExpect(jsonPath("$.status",Matchers.is("Successfully added stock")))
		       .andExpect(jsonPath("$.stockInfo[0].symbol",Matchers.is("INTC")));
	}
}
