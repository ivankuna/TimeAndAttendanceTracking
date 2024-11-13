package hr.betaSoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class PinController {
    @GetMapping("/pin")
    public String renderMenuPin(Model model) {

        model.addAttribute("path", "/employees");

        return "pin-menu";
    }

    @GetMapping("/pin/pin-in")
    public String renderPinIn() {
        return "pin-in";
    }

    @GetMapping("/pin/pin-out")
    public String renderPinOut() {
        return "pin-out";
    }

    @GetMapping("/pin/camera")
    public String showCamera() {
        return "pin-camera";
    }

    @GetMapping("/pin/resolution")
    public String showRes() {
        return "test-resolution";
    }

    @GetMapping("/pin/george")
    public String showGeorge() {
        return "test-george";
    }
}