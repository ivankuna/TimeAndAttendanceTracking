package hr.betaSoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PinController {
    @GetMapping("/pin")
    public String enterPinTest() {
        return "enter-pin";
    }
}