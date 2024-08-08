package com.dividend.dividend.web;

import java.util.List;

import org.apache.commons.collections4.Trie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dividend.dividend.persist.entity.Company;
import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.constants.CacheKey;
import com.dividend.dividend.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/company"))
public class CompanyController {

	private final CompanyService companyService;

	private final RedisCacheManager redisCacheManager;

	@GetMapping("/autocomplete")
	public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
		List<String> result = companyService.getCompanyNamesByKeyword(keyword);
		return ResponseEntity.ok(result);
	}

	@GetMapping
	@PreAuthorize("hasRole('READ')")
	public ResponseEntity<?> searchCompany(final Pageable pageable) {
		Page<Company> allCompany = companyService.getAllCompany(pageable);
		return ResponseEntity.ok(allCompany);
	}

	@PreAuthorize("hasRole('WRITE')")
	@PostMapping
	public ResponseEntity<?> addCompany(@RequestBody CompanyDto request) {
		String ticker = request.getTicker().trim();
		if (ObjectUtils.isEmpty(ticker)) {
			throw new RuntimeException("ticker is Empty");
		}
		CompanyDto companyDto = companyService.save(ticker);
		companyService.addAutoCompleteKeyword(companyDto.getName());
		return ResponseEntity.ok(companyDto);
	}

	@DeleteMapping("/{ticker}")
	@PreAuthorize("hasRole('WRITE')")
	public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
		String companyName = this.companyService.deleteCompany(ticker);
		this.clearFinanceCache(companyName);
		return ResponseEntity.ok(companyName);
	}

	public void clearFinanceCache(String companyName) {
		this.redisCacheManager.getCache(CacheKey.PREFIX_FINANCE).evict(companyName);
	}
}
