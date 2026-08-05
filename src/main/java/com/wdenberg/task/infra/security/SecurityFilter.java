package com.wdenberg.task.infra.security;

import com.wdenberg.task.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {


    private final TokenService tokenService;
    private final UserRepository userRepository;



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String token = recoverToken(request);


        if (token != null) {

            authenticateUser(token);
        }


        filterChain.doFilter(request, response);
    }



    private void authenticateUser(String token) {

        try {

            String email = tokenService.validateToken(token);


            if (email == null) {
                return;
            }


            UserDetails user =
                    userRepository.findByEmail(email);


            if (user == null) {
                return;
            }


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );


            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);


        } catch (Exception exception) {

            SecurityContextHolder.clearContext();
        }
    }



    private String recoverToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");


        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            return null;
        }


        return authorization.substring(7);
    }
}