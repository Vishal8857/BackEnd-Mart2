package com.product.Security;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.product.CommanClasses.CustomUserDetailsService;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService service;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();

		// Skip Login/Register APIs
		if (path.equals("/user/login") || path.equals("/user/register") || path.equals("api/category/allCategory")) {

			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");

		String token = null;
		String username = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			token = authHeader.substring(7);

			System.out.println("TOKEN : " + token);

			try {

				username = jwtUtil.extractUsername(token);

				System.out.println("USERNAME FROM TOKEN : " + username);

			} catch (Exception e) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			    response.setContentType("application/json");
			    response.getWriter().write("""
			        {
			            "message":"Invalid or Expired JWT Token"
			        }
			        """);

			    return;
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = service.loadUserByUsername(username);

			// Extract Role From Token
			String role = jwtUtil.extractRole(token);

			System.out.println("ROLE : " + role);

			if (jwtUtil.validateToken(token, userDetails.getUsername())) {

				System.out.println("TOKEN VALID");

				// SET ROLE
				List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, authorities);

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);

				System.out.println("AUTHENTICATION SUCCESS");
			}
		}

		filterChain.doFilter(request, response);
	}
}