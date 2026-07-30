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
public class SecurityFilter  extends OncePerRequestFilter {

    private final TokeService tokeService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = this.recoverToken(request);
        if(token != null){
            var login = tokeService.validateToken(token);

            if(!login.isEmpty()){
                UserDetails user = userRepository.findByEmail(login);

                if(user != null){
                    var authetication = new UsernamePasswordAuthenticationToken(user, null , user.getAuthorities());

                    // Salva as informações do usuário autenticado no contexto da requisição atual
                    SecurityContextHolder.getContext().setAuthentication(authetication);
                }
            }
        }
        filterChain.doFilter(request, response);

    }

    private String recoverToken(HttpServletRequest request){
        var autHeader = request.getHeader("authorization");
        if(autHeader == null) return  null;
        return autHeader.replace("Bearer ", "");
    }
}
