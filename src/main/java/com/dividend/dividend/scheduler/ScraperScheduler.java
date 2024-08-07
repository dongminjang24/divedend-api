package com.dividend.dividend.scheduler;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dividend.dividend.persist.entity.Company;
import com.dividend.dividend.persist.entity.Dividend;
import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.DividendDto;
import com.dividend.dividend.persist.model.ScrapedResultDto;
import com.dividend.dividend.persist.repository.CompanyRepository;
import com.dividend.dividend.persist.repository.DividendRepository;
import com.dividend.dividend.scraper.Scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {
	private final CompanyRepository companyRepository;
	private final Scraper yahooFinanaceScraper;
	private final DividendRepository dividendRepository;

	@Scheduled(cron = "${scheduler.scrape.yahoo}")
	public void yahooFinanceScheduling() {
		log.info("scraping scheduler is started");
		// 저장된 회사 목록 조회
		List<Company> companies = companyRepository.findAll();

		//회사마다 배당 금 정보를 새로 스크래핑
		for (Company company : companies) {
			ScrapedResultDto scrapedResultDto = yahooFinanaceScraper.scrap(CompanyDto.builder()
				.ticker(company.getTicker())
				.name(company.getName())
				.build());
			// 스크래핑한 배당금 정보 중
			scrapedResultDto.getDividendDtoList().stream()
				.map(e ->
					new Dividend(company.getId(), e)
				).forEach(e ->
				{
					boolean exists = dividendRepository.existsByCompanyIdAndLocalDateTime(e.getCompanyId(),
						e.getLocalDateTime());
					if (!exists) {
						dividendRepository.save(e);
					}
				});
			// 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}


		}

	}
}
