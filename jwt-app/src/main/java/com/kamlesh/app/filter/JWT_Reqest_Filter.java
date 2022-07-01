package com.kamlesh.app.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.startup.WebAnnotationSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kamlesh.app.service.MyUserDetailsService;
import com.kamlesh.app.util.JWT_util;

/*
This filter is used for manipulate the request which coming with JWT token header inside each request
use to check valid JWT token REQIRED -> userDetailsService and jwt_Util
If find valid Token then stored same token into securityContextHolder.getContext().setAuthentication(), so browser remember the username
*/
@Component
public class JWT_Reqest_Filter extends OncePerRequestFilter{
	
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
	private JWT_util jwt_Util;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");
		System.out.println("authorizationHeader: "+authorizationHeader);
		
		String username = null;
		String new_token = null;
		
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
			new_token = authorizationHeader.substring(7);
			username = jwt_Util.extractUsername(new_token);
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			if(jwt_Util.validateToken(new_token, userDetails)){
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		//next filter is called to store the token inside thread
		//handover control to next filter
		filterChain.doFilter(request, response);
		
	}

}
