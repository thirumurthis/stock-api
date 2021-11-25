package com.stock.finance;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockStoreRepository;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.service.StockStoreService;
import com.stock.finance.user.model.CustomUserDetails;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testGet() throws Exception {
   /*
	  this.mockMvc.perform(MockMvcRequestBuilders.get("/stock-app/about").param("name", "Joe"))
                  .andExpect(MockMvcResultMatchers.status().isOk())
                  .andExpect(MockMvcResultMatchers.model().attribute("msg", "Hi there, Joe."))
                  .andExpect(MockMvcResultMatchers.view().name("hello-page"))
                  .andDo(MockMvcResultHandlers.print());
   */
	  this.mockMvc.perform(MockMvcRequestBuilders.get("/stock-app/about"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      //.andExpect(MockMvcResultMatchers.model().attribute("msg", "Hi there, Joe."))
      //.andExpect(MockMvcResultMatchers.view().name("hello-page"))
      .andDo(MockMvcResultHandlers.print());

  }
  
  
	@MockBean
	StockStoreRepository stockRepo;
	
	@MockBean
	StockStoreService stockService;
	
    @MockBean
	private CustomUserDetailsService userDetailsService;
    
	ObjectMapper objectMapper = new ObjectMapper();
	
	@MockBean 
	JWTManagerService jwtMangerService;
	
  @Test
  public void testPost() {
	  
	  
		String token = jwtMangerService.generateTokenWithApi("user", "TESTAPIKEY");
		
		//Using Builder pattern approach
		//StockInfo stock = StockInfo.builder().symbol("INTC").active(true).stockCount(10).avgStockPrice(100.00f).userId(1).build();
		StockInfo stock = new StockInfo(100,"INTC",10.0f,100.0f,"1",true);
		MockHttpServletRequestBuilder request;
		try {
			request = post("/stock/v1/add")
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
					.accept(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(stock));
			Mockito.when(stockService.storeStockInfo(stock)).thenReturn(stock);
			when(userDetailsService.loadUserByUsername("user")).thenReturn(new CustomUserDetails("user", "", true, "TESTAPIKEY", "ROLE_USER"));
			mockMvc.perform(request)
			   .andExpect(MockMvcResultMatchers.status().isOk())
			      .andDo(MockMvcResultHandlers.print());
		    /*   .andExpect(status().isOk())
		       .andExpect(jsonPath("$", notNullValue()))
		       .andExpect(jsonPath("$.status",Matchers.is("Successfully added stock")))
		       .andExpect(jsonPath("$.stockInfo[0].symbol",Matchers.is("INTC")));*/
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
}
