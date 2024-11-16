package com.ExpenseTracker;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ExpenseTracker.Models.LoginUserDto;
import com.ExpenseTracker.Models.RegisterUserDto;
import com.ExpenseTracker.Models.User;
import com.ExpenseTracker.Repositories.UserRepository;
import com.ExpenseTracker.Services.AuthenticationService;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationService_Test {

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private AuthenticationService authService;

	@Autowired
	public PasswordEncoder passwordEncoder;
	@Mock
	public RegisterUserDto registerUserDto;
	@Mock
	public LoginUserDto loginUserDto;

	@ClassRule
	public static PostgreSQLContainer<MyPostgresqlContainer>
			postgreSQLContainer = MyPostgresqlContainer.getInstance();

	@BeforeAll
	public static void beforeAll() {
		postgreSQLContainer.start();
	}

	// I am getting cant invoke PasswordEncoder.ecode because passwordEncoder is
	// null;
	// I searched a for solutions but couldnt find anything
	@Test
	public void add_New_User() throws Exception {
		RegisterUserDto register1 = new RegisterUserDto();
		User user = new User();
		register1.setFirstName("Tim");
		register1.setLastName("Wayne");
		register1.setEmail("tw@hotmail.com");
		register1.setPassword("tw@hotmail.com");

		user.setFirstName(register1.getFirstName());
		user.setLastName(register1.getLastName());
		user.setEmail(register1.getEmail());
		user.setPassword(passwordEncoder.encode(register1.getPassword()));

		when(userRepo.save(user)).thenReturn(user);

		authService.signup(register1);

		// verify save method of userRepo is called
		verify(userRepo, times(1)).save(user);

	}

	/*
	 i am getting Cannot invoke .AuthenticationManager.authenticate(org.springframework.security.core.Authentication)" 
	 because "this.AuthenticationManager" is null
	 If i comment out AuthenticationManager.authenticate, it works
	 */
	@Test
	public void authenticate() throws Exception {
		LoginUserDto login1 = new LoginUserDto();
		Optional<User> user2 = Optional.ofNullable(new User());
		login1.setEmail("jp@hotmail.com");
		login1.setPassword("jp@hotmail.com");
		
		when(userRepo.findByEmail(login1.getEmail())).thenReturn(user2);
		
		User user3 = authService.authenticate(login1);
		
		Assertions.assertThat(user3).isNotNull();
	}

}

/*java.lang.NullPointerException: Cannot invoke "org.springframework.security.authentication.AuthenticationManager.authenticate(org.springframework.security.core.Authentication)" because "this.AuthenticationManager" is null
at com.ExpenseTracker.Services.AuthenticationService.authenticate(AuthenticationService.java:41)
at com.ExpenseTracker.AuthenticationService_Test.authenticate(AuthenticationService_Test.java:76)
at java.base/java.lang.reflect.Method.invoke(Method.java:568)
at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)*/


