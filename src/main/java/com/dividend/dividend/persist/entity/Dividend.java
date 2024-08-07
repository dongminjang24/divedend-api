package com.dividend.dividend.persist.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dividend.dividend.persist.model.DividendDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Entity(name = "dividend")
@Table(uniqueConstraints = @UniqueConstraint(
	columnNames = {"companyId","localDateTime"}
))
public class Dividend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long companyId;

	private LocalDateTime localDateTime;

	private String dividend;

	public Dividend(Long companyId, DividendDto dividendDto) {
		this.companyId = companyId;
		this.localDateTime = dividendDto.getLocalDateTime();
		this.dividend = dividendDto.getDividend();
	}
}
