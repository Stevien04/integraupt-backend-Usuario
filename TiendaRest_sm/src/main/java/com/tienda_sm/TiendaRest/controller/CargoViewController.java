package com.tienda_sm.TiendaRest.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CargoViewController {

    @GetMapping("/cargos")
    public String showCargoManager(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "cargos";
    }
}