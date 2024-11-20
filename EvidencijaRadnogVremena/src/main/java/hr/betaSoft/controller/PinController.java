package hr.betaSoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PinController {
    @GetMapping("/pin")
    public String renderMenuPin(Model model) {

        model.addAttribute("path", "/employees");

        return "pin-menu";
    }

    @GetMapping("/pin/pin-in")
    public String renderPinIn(Model model) {
        model.addAttribute("title", "DOLAZAK");
        model.addAttribute("action", "clockIn");
        return "pin-enter";
    }

    @GetMapping("/pin/pin-out")
    public String renderPinOut(Model model) {
        model.addAttribute("title", "ODLAZAK");
        model.addAttribute("action", "clockOut");
        return "pin-enter";
    }

    @GetMapping("/pin/message")
    public String success(@RequestParam(name = "message", required = false) String message, Model model) {
        // Add the message to the model
        model.addAttribute("message", message != null ? message : "No message provided.");
        return "pin-message"; // Return the Thymeleaf template name
    }

    @GetMapping("/pin/camera")
    public String showCamera() {
        return "pin-camera";
    }



    @GetMapping("/pin/resolution")
    public String showRes() {
        return "test-resolution";
    }



}