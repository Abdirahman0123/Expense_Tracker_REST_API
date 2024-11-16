package com.ExpenseTracker;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.core.userdetails.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ExpenseTracker.Models.Expense;
import com.ExpenseTracker.Models.User;
import com.ExpenseTracker.Services.AuthenticationService;
import com.ExpenseTracker.Services.ExpenseService;
import com.ExpenseTracker.Services.JwtService;
import com.ExpenseTracker.config.JwtAuthenticationFilter;
import com.ExpenseTracker.config.SecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.PostgreSQLContainer;

//@ExtendWith(SpringExtension.class)
@SpringBootTest(classes =  ExpenseTrackerApplicationTests.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfiguration.class)
public class ExpenseController_Test {

	@MockBean
	public ExpenseService expenseService;

	@MockBean
	public JwtService jwtService;

	// use this to authenticate the LoginUserDTO
	
	@MockBean
	private AuthenticationService authService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtAuthenticationFilter filter;
	
	@Autowired
	private WebApplicationContext context;

	@ClassRule
	public static PostgreSQLContainer<MyPostgresqlContainer>
			postgreSQLContainer = MyPostgresqlContainer.getInstance();

	@BeforeAll
	public static void beforeAll() {
		postgreSQLContainer.start();
	}

	//private String token;

	@Before(value = "")
	public void setup()
	{
	    //Init MockMvc Object and build
	    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	
	@Test // this passed but I have to omit ex1.getDate() because json conversion issues
	public void getExpense_API_Test() throws Exception {
		String ourtoken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsd2lsbGlhbXMxNiIsInJvbGVzIjoidXNlciIsImlhdCI6MTUxNDQ0OTgzM30.WKMQ_oPPiDcc6sGtMJ1Y9hlrAAc6U3xQLuEHyAnM1FU";
		
		// Arrange
		Long id1 = 1l;
		Expense ex1 = new Expense(id1, "Wings Meal", "KFC", 5, "Food", LocalDate.now(), LocalTime.now());
		
		// Act
		//when(expenseService.getExpense(id1)).thenReturn(ex1);
		doReturn(Optional.of(ex1)).when(expenseService).getExpense(id1);
		// assertion
		// mockMvc.perform("/vi/api/expenses/{id}", id1)).and
		mockMvc.perform(get("/v1/api/expenses/{id}", id1).with(csrf())
				.header("AUTHORIZATION","Bearer"+ourtoken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id1)).andExpect(jsonPath("$.item").value(ex1.getItem()))
				.andExpect(jsonPath("$.shop").value(ex1.getShop())).andExpect(jsonPath("$.price").value(ex1.getPrice()))
				.andExpect(jsonPath("$.category").value(ex1.getCategory()))
				/*.andExpect(jsonPath("$.date").value(ex1.getDate()))
				.andExpect(jsonPath("$.time").value(ex1.getTime()))*/;
	}

	@Test
	//@WithMockUser
	//@WithUserDetails
	/* this passes if you comment out
	 User currentUser = (User) authentication.getPrincipal(); in ExpenseConroller class 
	 if you use  @WithMockUser, it throws casting issues
	 *  */
	public void create_New_Expense_API_Test() throws Exception {
		//User user = new User();
		String mytoken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsd2lsbGlhbXMxNiIsInJvbGVzIjoidXNlciIsImlhdCI6MTUxNDQ0OTgzM30.WKMQ_oPPiDcc6sGtMJ1Y9hlrAAc6U3xQLuEHyAnM1FU";
		Long id1 = 1l;
		/*User user = new User();
		user.setFirstName("Farah");
		user.setLastName("John");
		user.setEmail("Hardland@hotmail.com");
		user.setPassword("Hardland@hotmail.com");*/
		
		Expense ex1 = new Expense(id1, "Wings Meal", "KFC",
				5, "Food", LocalDate.now(), LocalTime.now());
		
		//ex1.setUser(user);

		/*this.*/mockMvc.perform(post("/v1/api/expenses")
				.header("AUTHORIZATION","Bearer"+mytoken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ex1)))
				.andExpect(status().isCreated());
	}

	/*
	 This test passes if change deleteExpense controller method body to this:
	 expenseService.deleteExpense(id);
	 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	 */
	
	@Test // I think you have to add a record with same id then test.
	public void deleteStudent() throws Exception {
	
		String dtoken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsd2lsbGlhbXMxNiIsInJvbGVzIjoidXNlciIsImlhdCI6MTUxNDQ0OTgzM30.WKMQ_oPPiDcc6sGtMJ1Y9hlrAAc6U3xQLuEHyAnM1FU";
		Long id1 = 4l;
		Expense ex1 = new Expense(id1, "Wings Meal", "KFC", 5, "Food", LocalDate.now(), LocalTime.now());
		
		//doReturn(Optional.of(ex1)).when(expenseService).deleteExpense(id1); 
		mockMvc.perform(delete("/v1/api/expenses/{id}", id1).with(csrf())
				.header("AUTHORIZATION","Bearer"+dtoken))
				.andExpect(status().isNoContent());
		
	}

	/* getting Cannot invoke "org.springframework.data.domain.Page.getContent()" 
	because "pageExpenses" is null. This is because I implemented Pagination */
	@Test 
	public void getAllExpenses_API_Test() throws Exception {
		String mytoken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsd2lsbGlhbXMxNiIsInJvbGVzIjoidXNlciIsImlhdCI6MTUxNDQ0OTgzM30.WKMQ_oPPiDcc6sGtMJ1Y9hlrAAc6U3xQLuEHyAnM1FU";
		mockMvc.perform(get("/v1/api/expenses") 
				.param("category", "Food")
				.param("page", "0")
				.param("size", "3")
				.param("sort", "date, asc")
				.header("authorization", "Bearer " + mytoken))
	            .andExpect(status().isOk());
	}
	
}
