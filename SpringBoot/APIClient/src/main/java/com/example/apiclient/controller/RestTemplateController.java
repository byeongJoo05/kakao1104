package com.example.apiclient.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.apiclient.dto.MemberDTO;
import com.example.apiclient.service.RestTemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest-template")
public class RestTemplateController {
	private final RestTemplateService restTemplateService;

	@GetMapping
	public String getName() {
		return restTemplateService.getName();
	}

	@GetMapping("/path-variable")
	public String getNameWithPathVariable() {
		return restTemplateService.getNameWithPathVariable();
	}

	@GetMapping("/parameter")
	public String getNameWithParameter() {
		return restTemplateService.getNameWithParamater();
	}

	@PostMapping
	public ResponseEntity<MemberDTO> postDto() {
		return restTemplateService.postWithParamAndBody();
	}

	@PostMapping("/header")
	public ResponseEntity<MemberDTO> postWithHeader() {
		return restTemplateService.postWithHeader();
	}
}
