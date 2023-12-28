package com.example.demo.authentication;

import com.example.demo.jwt_service.JwtService;
import com.example.demo.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequestDto.username(), loginRequestDto.password());
            Authentication authentication = authenticationManager.authenticate(token);

            String username = authentication.getName();
            String role = authentication.getAuthorities().toArray()[0].toString();

            String bearerToken = jwtService.generateToken(new User(username, "", role));
            return new ResponseEntity<>(bearerToken, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
