package com.dividend.dividend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.scraper.YahooFinanceScraper;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DividendApplication {

	public static void main(String[] args) {

		SpringApplication.run(DividendApplication.class, args);
		//
		// YahooFinanceScraper yahooFinanceScraper = new YahooFinanceScraper();
		// // ScrapedResultDto result = yahooFinanceScraper.scrap(CompanyDto.builder().ticker("O").build());
		// // System.out.println("result = " + result);
		//
		// CompanyDto resultName = yahooFinanceScraper.scrapCompanyByTicker("KKK");
		// System.out.println("resultName = " + resultName);

	}

}
