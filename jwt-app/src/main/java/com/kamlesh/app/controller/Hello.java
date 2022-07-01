package com.kamlesh.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kamlesh.app.model.AuthRequest;
import com.kamlesh.app.model.AuthResponse;
import com.kamlesh.app.service.MyUserDetailsService;
import com.kamlesh.app.util.JWT_util;

@RestController
public class Hello {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private MyUserDetailsService userDetailsService;
	@Autowired
	private JWT_util jwt_Util;
	

	@RequestMapping("/test")
	public String test(){
		return "Hello World!!";	
	}
	
	//generate token and passed in response to client
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticaionToken(@RequestBody AuthRequest authRequest) throws Exception{
		try {
		authenticationManager
		.authenticate(new UsernamePasswordAuthenticationToken
				(authRequest.getUsername(), authRequest.getPassword()));
		}
		catch(BadCredentialsException e){
			throw new Exception("Incorrect username or passord", e);
		}
		
		//load username from userDetailsService
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
		
		//pass userDetails username to jwt_util for generate JWT
		final String token = jwt_Util.generateToken(userDetails);
		
		return ResponseEntity.ok(new AuthResponse(token));//send generated token to client
	}
}
