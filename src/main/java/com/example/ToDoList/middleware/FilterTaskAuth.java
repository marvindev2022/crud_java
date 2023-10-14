package com.example.ToDoList.middleware;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.ToDoList.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().contains("/tasks")) {
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                response.sendError(401, "Token not found");
                return;
            }
            var authEncoded = token.substring("Basic".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(authEncoded);
            String decodedString = new String(decodedBytes);
            String[] credentials = decodedString.split(":");
            if (credentials.length != 2) {
                response.sendError(401, "Invalid token");
                return;
            }
            String username = credentials[0];
            String password = credentials[1];
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401, "User not found");
                return;
            }
            BCrypt.Verifyer verifyer = BCrypt.verifyer();
            BCrypt.Result result = verifyer.verify(password.toCharArray(), user.getPassword());
            if (!result.verified) {
                response.sendError(401, "Invalid password");
                return;
            }
            request.setAttribute("userId", user.getId());

        }
        filterChain.doFilter(request, response);
    }

}