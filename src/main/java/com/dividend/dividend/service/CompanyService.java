package com.dividend.dividend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.dividend.dividend.exception.NoCompanyException;
import com.dividend.dividend.persist.entity.Company;
import com.dividend.dividend.persist.entity.Dividend;
import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.ScrapedResultDto;
import com.dividend.dividend.persist.repository.CompanyRepository;
import com.dividend.dividend.persist.repository.DividendRepository;
import com.dividend.dividend.scraper.Scraper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

	private final Trie trie;
	private final Scraper yahooFinanceScraper;
	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;

	public Page<Company> getAllCompany(Pageable pageable) {
		return companyRepository.findAll(pageable);
	}

	public CompanyDto save(String ticker) {

		boolean exist = companyRepository.existsByTicker(ticker);
		if (exist) {
			throw new RuntimeException("already exists ticker -> " + ticker);
		}
		return storeCompanyAndDividend(ticker);
	}

	public CompanyDto storeCompanyAndDividend(String ticker) {

		// ticker 를 기준으로 회사를 스크래핑
		CompanyDto companyDto = yahooFinanceScraper.scrapCompanyByTicker(ticker);
		if (ObjectUtils.isEmpty(companyDto)) {
			throw new RuntimeException("failed to scrap ticker -> " + ticker);
		}

		// 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
		ScrapedResultDto scrapedResult = yahooFinanceScraper.scrap(companyDto);

		// 스크래핑 통과
		Company company = new Company(companyDto);
		Company savedCompany = companyRepository.save(company);
		List<Dividend> dividendList = scrapedResult.getDividendDtoList().stream()
			.map(e -> new Dividend(savedCompany.getId(), e))
			.toList();
		dividendRepository.saveAll(dividendList);
		return companyDto;
	}

	public List<String> getCompanyNamesByKeyword(String keyword) {
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Company> companyList = companyRepository.findByNameStartingWithIgnoreCase(keyword,pageRequest);
		return companyList.stream().map(Company::getName).toList();
	}

	public void addAutoCompleteKeyword(String keyword) {
		trie.put(keyword, null);
	}

	public List<String> autoComplete(String keyword) {
		List<String> result = (List<String>)trie.prefixMap(keyword).keySet().stream()
			.limit(10)
			.collect(Collectors.toList());
		return result;
	}

	public void deleteAutoCompleteKeyword(String keyword) {
		trie.remove(keyword);
	}

	@Transactional
	public String deleteCompany(String ticker) {
		Company company = this.companyRepository.findByTicker(ticker).orElseThrow(
			NoCompanyException::new
		);

		this.dividendRepository.deleteByCompanyId(company.getId());
		this.companyRepository.delete(company);

		this.deleteAutoCompleteKeyword(company.getName());
		return company.getName();
	}
}
