package com.dividend.dividend.persist.model;

import org.springframework.core.serializer.Deserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

	@JsonDeserialize(using = StringDeserializer.class)
	private String ticker;

	@JsonDeserialize(using = StringDeserializer.class)
	private String name;



}
