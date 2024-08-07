package com.dividend.dividend.persist.entity;

import com.dividend.dividend.persist.model.CompanyDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Entity(name = "company")
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String ticker;

	private String name;

	public Company(CompanyDto companyDto) {
		this.ticker = companyDto.getTicker();
		this.name = companyDto.getName();
	}
}
