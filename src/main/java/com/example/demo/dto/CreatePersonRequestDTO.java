package com.example.demo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.example.demo.base.ValidateableVO;
import com.example.demo.entity.SexEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonRequestDTO extends ValidateableVO {

	private static final long serialVersionUID = 445938723426090316L;
	
	@NotNull(message = "CreatePersonRequestDTO.sex:NotNull")
	private SexEnum sex;
	@NotEmpty(message = "CreatePersonRequestDTO.firstName:NotEmpty")
	private String firstName;
	@NotEmpty(message = "CreatePersonRequestDTO.lastName:NotEmpty")
	private String lastName;
	
}
