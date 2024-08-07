package com.dividend.dividend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dividend.dividend.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {

	private final FinanceService financeService;

	@GetMapping("/dividend/{companyName}")
	public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
		return ResponseEntity.ok(financeService.getDividendByCompanyName(companyName));
	}
}
