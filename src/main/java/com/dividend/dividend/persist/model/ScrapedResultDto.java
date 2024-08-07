package com.dividend.dividend.persist.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapedResultDto {

	private CompanyDto companyDto;
	private List<DividendDto> dividendDtoList;

	public ScrapedResultDto() {
		this.dividendDtoList = new ArrayList<>();
	}
}
