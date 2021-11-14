package com.stock.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.controller.StockAPIController;
import com.stock.finance.filter.JwtRequestFilter;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockStoreRepository;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.service.StockStoreService;
import com.stock.finance.user.model.CustomUserDetails;
import com.stock.finance.user.repo.UserRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.mockito.BDDMockito.*;

//@SpringBootTest
//@DataJpaTest
@WebMvcTest(StockAPIController.class)
//@SpringBootTest(
//	    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	  //  classes = StockAPIController.class)
/*
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class })
    */
public class TestStockAPIRestController {

	//@Autowired
	MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;

    @MockBean
    private UserRepository userRepo;

    @InjectMocks
    private JwtRequestFilter jwtAuthenticationFilter = new JwtRequestFilter();

    @Autowired
	private JWTManagerService jwtMangerService; // = new JWTManagerService();
    
   // @MockBean
   // JwtRequestFilter jwtRequestFilter;
    
	//@Autowired
    @MockBean
	private CustomUserDetailsService userDetailsService;// = new CustomUserDetailsService();
    
    //@BeforeEach
    public void setUp() throws ServletException, IOException
    {
    	//security context when set is set within the session
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
        		.apply(springSecurity())
        		.addFilters(jwtAuthenticationFilter)
        		.build();
        
        //mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
       // MockitoAnnotations.initMocks(this);
    }
	

	//@Autowired
	//JwtRequestFilter filter;
	
	@Disabled
	@Test
	//@WithMockUser  // we saw this on the method level security test case
	public void allowAll() throws Exception{

		mockMvc.perform(get("/stock-app/about"))
		       .andExpect(status().isOk());
	 
	}
	
	
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@MockBean
	StockStoreRepository stockRepo;
	
	@MockBean
	StockStoreService stockService;
		
	
	@Disabled
	@Test
	@WithMockUser  // we saw this on the method level security test case
	public void postAddStockTest() throws Exception{
		
		String token = jwtMangerService.generateTokenWithApi("user", "TESTAPIKEY");
		
		//Using Builder pattern approach
		//StockInfo stock = StockInfo.builder().symbol("INTC").active(true).stockCount(10).avgStockPrice(100.00f).userId(1).build();
		StockInfo stock = new StockInfo(100,"INTC",10.0f,100.0f,"1",true);
		MockHttpServletRequestBuilder request = post("/stock/v1/add")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
				.accept(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(stock));
		
		when(userDetailsService.loadUserByUsername("user")).thenReturn(new CustomUserDetails("user", "", true, "TESTAPIKEY", "ROLE_USER"));
		
		Mockito.when(stockService.storeStockInfo(stock)).thenReturn(stock);
		mockMvc.perform(request)
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$", notNullValue()))
		       .andExpect(jsonPath("$.status",Matchers.is("Successfully added stock")))
		       .andExpect(jsonPath("$.stockInfo[0].symbol",Matchers.is("INTC")));
	}
	
	@Disabled
	@Test
	//@WithMockUser(username="user",password="user")
	//@WithUserDetails
	public void testJWTFilter() throws Exception {
	/*    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	        "user",
	        "user",
	        Collections.singletonList(new SimpleGrantedAuthority("user"))
	    );*/
		//*
	    String jwt = jwtMangerService.generateTokenWithApi("user", "TESTAPIKEY");
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
	    request.setRequestURI("/stock-app/token");
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    MockFilterChain filterChain = new MockFilterChain();
	    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	    assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user");
	    assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString()).isEqualTo(jwt);
	    //*/
		
		String token = jwtMangerService.generateTokenWithApi("user", "TESTAPIKEY");
		//System.out.println("TOKEN ++++++++++++> "+token);
		//MockFilterChain filterChain = new MockFilterChain();
        //MockHttpServletRequest request = new MockHttpServletRequest();
        //request.addHeader(HttpHeaders.AUTHORIZATION,"Authorized");
        //MockHttpServletResponse response = new MockHttpServletResponse();
        //jwtAuthenticationFilter.doFilter(request, response, filterChain);
		String bodyRequest = "{\"userName\":\"user\",\"apiKey\":\"TESTAPIKEY\"}";
       // Mockito.when(jwtMangerService.validateToken(token,new CustomUserDetails("user", "", true, "TESTAPIKEY", "ROLE_USER"),"TESTAPIKEY")).thenReturn(true);
        //Mockito.when(jwtMangerService.extractUserName("")).thenReturn("user");
		//SecurityContextHolder.getContext().getAuthentication()
		Mockito.when(userDetailsService.loadUserByUsername("user")).thenReturn(new CustomUserDetails("user", "", true, "TESTAPIKEY", "ROLE_USER"));
		//Mockito.when(jwtAuthenticationFilter.doFilter(request, response, filterChain))
		SecurityContext seContext = SecurityContextHolder.createEmptyContext();
        MvcResult result =mockMvc.perform(post("/stock-app/token")
        		.sessionAttr(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, seContext)
				.header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
				.content(bodyRequest)				
        		.accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        
        String response1 = result.getResponse().getContentAsString();
        System.out.println("RESPONSE +++ "+response1);
	}
}
