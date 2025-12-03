package com.tienda_sm.TiendaRest.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Random;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final Random random = new Random();

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        prepareCaptcha(model, session);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("captcha") String captchaResponse,
            Model model,
            HttpSession session) {

        if (!isCaptchaValid(captchaResponse, session)) {
            model.addAttribute("error", "Captcha incorrecto, intenta de nuevo.");
            model.addAttribute("username", username);
            prepareCaptcha(model, session);
            return "login";
        }

        if (username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Por favor ingresa usuario y contraseña.");
            model.addAttribute("username", username);
            prepareCaptcha(model, session);
            return "login";
        }

        model.addAttribute("message", "Inicio de sesión exitoso (validación simulada).");
        model.addAttribute("username", username);
        prepareCaptcha(model, session);
        return "login";
    }

    private boolean isCaptchaValid(String captchaResponse, HttpSession session) {
        Object expected = session.getAttribute("captchaAnswer");
        if (!(expected instanceof Integer expectedAnswer)) {
            return false;
        }

        try {
            int provided = Integer.parseInt(captchaResponse.trim());
            return provided == expectedAnswer;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void prepareCaptcha(Model model, HttpSession session) {
        int first = random.nextInt(9) + 1;
        int second = random.nextInt(9) + 1;
        int answer = first + second;
        session.setAttribute("captchaAnswer", answer);
        model.addAttribute("captchaQuestion", first + " + " + second + " =");
    }
}