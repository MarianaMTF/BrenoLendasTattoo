package com.projecttattoo.BrenoLendaTattoo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projecttattoo.BrenoLendaTattoo.dto.LoginDto;

import org.springframework.ui.Model;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/home")
public class HomeController {
	
	@GetMapping
	public String indexLeadPage(Model model) {
		model.addAttribute("login", new LoginDto(null, null));
		return "leadpage";
	}
}
