package it.alessiomatricardi.easytask.backend.config;

import it.alessiomatricardi.easytask.backend.errors.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        // check if the user inserted a JWT token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get the JWT token
        jwtToken = authHeader.substring(7);

        try {
            // get the user email
            userEmail = jwtService.extractUsername(jwtToken);

            // user is not authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    // create the authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // update the security context holder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch(Exception e) {
            // token is expired or malformed
            // send 401 (UNAUTHORIZED) to the client
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            boolean isTokenExpired = e instanceof ExpiredJwtException;
            ErrorResponse errorResponse = new ErrorResponse(status, e.getMessage(), isTokenExpired);
            ObjectMapper objectMapper = new ObjectMapper();
            response.setStatus(status.value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
