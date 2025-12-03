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

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return redirectByRole(session);
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return redirectByRole(session);
        }
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
        session.setAttribute("loggedInUser", username);
        session.setAttribute("isAdmin", isAdminUser(username));
        return redirectByRole(session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
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

    private boolean isAdminUser(String username) {
        return "administrador".equalsIgnoreCase(username.trim());
    }

    private String redirectByRole(HttpSession session) {
        Object adminFlag = session.getAttribute("isAdmin");
        boolean isAdmin = adminFlag instanceof Boolean && (Boolean) adminFlag;
        return isAdmin ? "redirect:/menu" : "redirect:/empleados";
    }
}
