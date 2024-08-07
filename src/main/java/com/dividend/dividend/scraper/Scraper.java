package com.dividend.dividend.scraper;

import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.ScrapedResultDto;

public interface Scraper {
	CompanyDto scrapCompanyByTicker(String ticker);

	ScrapedResultDto scrap(CompanyDto companyDto);
}
