package com.dividend.dividend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.dividend.dividend.exception.NoCompanyException;
import com.dividend.dividend.persist.entity.Company;
import com.dividend.dividend.persist.entity.Dividend;
import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.DividendDto;
import com.dividend.dividend.persist.model.ScrapedResultDto;
import com.dividend.dividend.persist.model.constants.CacheKey;
import com.dividend.dividend.persist.repository.CompanyRepository;
import com.dividend.dividend.persist.repository.DividendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceService {
	private final DividendRepository dividendRepository;
	private final CompanyRepository companyRepository;


	// 요청이 자주 들어오는가?
	// 자주 변경이 되는 데이터인가?
	@Cacheable(value = CacheKey.PREFIX_FINANCE, key = "#companyName")
	public ScrapedResultDto getDividendByCompanyName(String companyName) {
		// 1. 회사명을 기준으로 회사 정보 조회
		Company findCompany = companyRepository.findByName(companyName).orElseThrow(
			NoCompanyException::new
		);

		// 2. 조회된 회사 ID로 배당금 정보 조회
		List<Dividend> dividends = dividendRepository.findAllByCompanyId(findCompany.getId());
		List<DividendDto> dividendDtoList = dividends.stream()
			.map(dividend -> new DividendDto(dividend.getLocalDateTime(), dividend.getDividend())).toList();
		// 3. 결과 조합 후 반환
		CompanyDto companyDto = new CompanyDto(findCompany.getName(), findCompany.getTicker());
		return new ScrapedResultDto(companyDto, dividendDtoList);
	}
}
