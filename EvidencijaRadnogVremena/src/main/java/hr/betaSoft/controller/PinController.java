package hr.betaSoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

@Controller
public class PinController {
    @GetMapping("/pin/{called}")
    public String renderMenuPin(@PathVariable("called") String called, Model model) {

        model.addAttribute("path", "/" + called);

        return "menu-pin";
    }

    @GetMapping("/pin/pin-in")
    public String renderPinIn() {
        return "enter-pin-in";
    }

    @GetMapping("/pin/pin-out")
    public String renderPinOut() {
        return "enter-pin-out";
    }
}