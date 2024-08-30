package com.ExpenseTracker.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.ExpenseTracker.Services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private HandlerExceptionResolver handlerExceptionResolver;

	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserDetailsService userDetailsService;

	/*
	 * this method checks if the token is valid or not. if it not valid, reject the
	 * token. if it is valid, extract the username, find the user in the database
	 * using the username. Then, set the user in the Authentication context it can
	 * be access in the application layer
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		// check if the header is not null or doesnt start with "Bearer"
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			// remove "Bearer ". Bearer is 6 characters and plus whitespace makes it 7
			String jwt = authHeader.substring(7);

			// extract the username from the token
			String userEmail = jwtService.extractUsername(jwt);

			// get the authenticated user
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// if userEmail not null, load the user using the email
			if (userEmail != null && authentication == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

				/*
				 * if the token is valid, create an object of
				 * UsernamePasswordAuthenticationToken that three arguments: userDetails and
				 * authorities. null is the place of credentials but i am not using it
				 */

				/*
				 * UsernamePasswordAuthenticationToken is an implementation of the
				 * Authentication interface and used when a user wants to authenticate using a
				 * username and password
				 */
				if (jwtService.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
			filterChain.doFilter(request, response);
		}

		catch (Exception exception) {
			handlerExceptionResolver.resolveException(request, response, null, exception);
		}
	}
}
