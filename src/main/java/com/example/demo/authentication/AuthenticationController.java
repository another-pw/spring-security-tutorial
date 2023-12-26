package com.example.demo.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/auth")
public class AuthenticationController {

    private final SecurityContextRepository securityContextRepository;

    private final AuthenticationManager authenticationManager;

    private final SecurityContextLogoutHandler securityContextLogoutHandler;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            SecurityContextLogoutHandler securityContextLogoutHandler
    ) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.securityContextLogoutHandler = securityContextLogoutHandler;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequestDto.username(), loginRequestDto.password());
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.getSession().setMaxInactiveInterval(30 * 60);
        securityContextRepository.saveContext(context, request, response);
        return new ResponseEntity<>("login successfully!", HttpStatus.OK);
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        securityContextLogoutHandler.logout(request, response, authentication);
        return new ResponseEntity<>("logout!", HttpStatus.OK);
    }
}
