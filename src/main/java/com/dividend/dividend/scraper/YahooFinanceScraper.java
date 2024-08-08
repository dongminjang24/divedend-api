package com.dividend.dividend.scraper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.dividend.dividend.persist.model.CompanyDto;
import com.dividend.dividend.persist.model.DividendDto;
import com.dividend.dividend.persist.model.ScrapedResultDto;
import com.dividend.dividend.persist.model.constants.Month;

@Component
public class YahooFinanceScraper implements Scraper{

	private static final String STATIC_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
	private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s/";

	private static final long START_TIME = 86400; // 60 * 60 * 24

	@Override
	public ScrapedResultDto scrap(CompanyDto companyDto) {
		ScrapedResultDto scrapedResultDto = new ScrapedResultDto();
		scrapedResultDto.setCompanyDto(companyDto);

		try {
			Long start = 0L;
			Long end = System.currentTimeMillis() / 1000;

			String url = String.format(STATIC_URL, companyDto.getTicker(), start, end);

			Connection connect = Jsoup.connect(url);
			Document document = connect.get();

			Elements parsingDivs = document.getElementsByClass("table yf-ewueuo");
			Element tableElement = parsingDivs.get(0);
			Element tbody = tableElement.children().get(1);
			List<DividendDto> dividends = new ArrayList<>();
			for (Element tr : tbody.children()) {
				String text = tr.text();
				if (!text.endsWith("Dividend")) {
					continue;
				}
				String[] split = text.split(" ");
				String month = split[0];
				int monthNumber = Month.strToNumber(month);
				if (monthNumber < 0) {
					throw new RuntimeException("Unexpected Month enum value -> " + split[0]);
				}

				int day = Integer.parseInt(split[1].replace(",", ""));
				int year = Integer.parseInt(split[2]);
				String dividend = split[3];
				dividends.add(new DividendDto(LocalDateTime.of(year, monthNumber, day, 0, 0),dividend));

			}
			scrapedResultDto.setDividendDtoList(dividends);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scrapedResultDto;
	}

	@Override
	public CompanyDto scrapCompanyByTicker(String ticker) {
		String url = String.format(SUMMARY_URL, ticker, ticker);
		try {
			Document document = Jsoup.connect(url).get();

			Element titleElement = document.getElementsByTag("h1").get(1);
			String title = titleElement.text().split("\\(")[0].trim();
			return new CompanyDto(ticker,title);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
