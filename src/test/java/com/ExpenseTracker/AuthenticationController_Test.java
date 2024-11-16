package com.ExpenseTracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ExpenseTracker.Models.LoginUserDto;
import com.ExpenseTracker.Models.RegisterUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationController_Test {

	@ClassRule
	public static PostgreSQLContainer<MyPostgresqlContainer> postgreSQLContainer =
			MyPostgresqlContainer.getInstance();

	// start the container before running tests
	@BeforeAll
	public static void beforeAll() {
		postgreSQLContainer.start();
	}
	@Mock
	public RegisterUserDto registerUserDto;
	@Mock
	public LoginUserDto loginUserDto;
	
	@Autowired 
	public MockMvc mockMvc;
	
	@Autowired
	public ObjectMapper objectMapper;
	
	// register with correct credentials
	@Test
	public void register_Method_Test() throws  Exception {
		RegisterUserDto user1 = new RegisterUserDto();
		
		user1.setFirstName("Tim");
		user1.setLastName("Wayne");
		user1.setEmail("tw@hotmail.com");
		user1.setPassword("tw@hotmail.com");
		
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user1)))
				.andExpect(status().isCreated())
				.andDo(print());
	}
	
	// trying register with existing email
	@Test
	public void register_With_Existing_Email_Test() throws  Exception {
		RegisterUserDto user5 = new RegisterUserDto();
		
		user5.setFirstName("John");
		user5.setLastName("Parker");
		user5.setEmail("jp@hotmail.com");
		user5.setPassword("jp@hotmail.com");
		
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user5)))
				.andExpect(status().isConflict())
				.andDo(print());
	}
	
	// try to register without providing details
	@Test
	public void register_Method__With_Empty_Values_Test() throws  Exception {
		RegisterUserDto user2 = new RegisterUserDto();
		
		user2.setFirstName("");
		user2.setLastName("");
		user2.setEmail("");
		user2.setPassword("");
		
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user2)))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}
	
	// login with correct credentials
	@Test
	public void login_Method_Test() throws  Exception {
		LoginUserDto login1 = new LoginUserDto();
		
		login1.setEmail("jp@hotmail.com");
		login1.setPassword("jp@hotmail.com");
		
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(login1)))
				.andExpect(status().isOk())
				.andDo(print());
	}
	
	// try login without providing credentials
	@Test
	public void login_With_Empty_ValuesTest() throws  Exception {
		LoginUserDto login2 = new LoginUserDto();
		
		login2.setEmail("");
		login2.setPassword("");
		
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(login2)))
				.andExpect(status().isUnauthorized())
				.andDo(print());
	}
}
