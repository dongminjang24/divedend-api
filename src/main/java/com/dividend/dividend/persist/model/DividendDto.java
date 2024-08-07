package com.dividend.dividend.persist.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DividendDto {

	private LocalDateTime localDateTime;
	private String dividend;

}
