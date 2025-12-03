package com.tienda_sm.TiendaRest.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String showMainMenu(HttpSession session) {
        Object loggedIn = session.getAttribute("loggedInUser");
        Object isAdminFlag = session.getAttribute("isAdmin");
        boolean isAdmin = isAdminFlag instanceof Boolean && (Boolean) isAdminFlag;

        if (loggedIn == null) {
            return "redirect:/login";
        }

        return isAdmin ? "menu" : "redirect:/empleados";
    }
}