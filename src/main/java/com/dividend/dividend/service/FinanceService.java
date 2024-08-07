package com.dividend.dividend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dividend.dividend.persist.entity.Company;
import com.dividend.dividend.persist.entity.Dividend;
import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.DividendDto;
import com.dividend.dividend.persist.model.ScrapedResultDto;
import com.dividend.dividend.persist.repository.CompanyRepository;
import com.dividend.dividend.persist.repository.DividendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceService {
	private final DividendRepository dividendRepository;
	private final CompanyRepository companyRepository;

	public ScrapedResultDto getDividendByCompanyName(String company) {
		// 1. 회사명을 기준으로 회사 정보 조회
		Company findCompany = companyRepository.findByName(company).orElseThrow(
			()->new RuntimeException("존재하지 않는 회사명입니다.")
		);

		// 2. 조회된 회사 ID로 배당금 정보 조회
		List<Dividend> dividends = dividendRepository.findAllByCompanyId(findCompany.getId());
		List<DividendDto> dividendDtoList = dividends.stream().map(dividend -> DividendDto.builder()
			.localDateTime(dividend.getLocalDateTime())
			.dividend(dividend.getDividend())
			.build()).toList();
		// 3. 결과 조합 후 반환
		CompanyDto companyDto = CompanyDto.builder()
			.name(findCompany.getName())
			.ticker(findCompany.getTicker())
			.build();
		return new ScrapedResultDto(companyDto, dividendDtoList);
	}
}
