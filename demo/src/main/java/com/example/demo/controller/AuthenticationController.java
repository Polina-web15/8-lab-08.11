package com.example.demo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChangePasswordRequestDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.StudentLoggedDto;
import com.example.demo.service.AuthenticationService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
           @CookieValue(name="access-token", required=false) String access, 
           @CookieValue(name="refresh-token", required=false) String refresh,
           @RequestBody LoginRequestDto request){        
        
        return authenticationService.login(request, access, refresh);
        
        
    }
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
           @CookieValue(name="refresh-token", required=false) String refresh){ 
        logger.debug("Запрос на обновление токена (refresh)");
        try {
            ResponseEntity<LoginResponseDto> response = authenticationService.refresh(refresh);
            logger.info("Токен успешно обновлён");
            return response;
        } catch (Exception e) {
            logger.warn("Ошибка при обновлении токена: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements({})//?
    public ResponseEntity<LoginResponseDto> logout(
           @CookieValue(name="access-token", required=false) String access){ 
        logger.info("Пользователь инициировал выход из системы");

        try {       
            logger.info("Пользователь успешно вышел из системы");
            return authenticationService.logout(access);
        } catch (Exception e) {
            logger.error("Ошибка при выходе из системы", e);
            throw e;
        }
    }

    @GetMapping("/info")
    public ResponseEntity<StudentLoggedDto> info(){ 
        logger.debug("Запрос информации о текущем пользователе");
        try {            
            logger.debug("Информация о пользователе успешно возвращена");
            return ResponseEntity.ok(authenticationService.info());
        } catch (Exception e) {
            logger.error("Ошибка при получении информации о пользователе", e);
            throw e;
        }
        
    }
    @PatchMapping("/changePassword")
    public ResponseEntity <LoginResponseDto> changePassword(ChangePasswordRequestDto request) {
        try {
            logger.info("Пароль успешно изменён");
            return authenticationService.changePassword(request);
        } catch (Exception e) {
            logger.warn("Ошибка при смене пароля: {}", e.getMessage());
            throw e;
        }
        
    }
}
