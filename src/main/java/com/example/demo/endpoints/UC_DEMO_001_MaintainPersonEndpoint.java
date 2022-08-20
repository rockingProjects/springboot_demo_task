package com.example.demo.endpoints;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.activity.UC_DEMO_001_01_CreatePersonActivity;
import com.example.demo.dto.CreatePersonRequestDTO;
import com.example.demo.exception.KingdomNotFoundException;

import lombok.extern.java.Log;

@RestController
@Log
public class UC_DEMO_001_MaintainPersonEndpoint {
	@Autowired
	private UC_DEMO_001_01_CreatePersonActivity activity;
	
	@PostMapping(path = "/kingdom/{kingdomIdentifier}/person")
	public void createPerson(@PathVariable("kingdomIdentifier") @NotNull String kingdomIdentifier, @RequestBody CreatePersonRequestDTO request) throws KingdomNotFoundException {
		Set<String> validate = request.validate();
		if (validate.size() > 0) {
			throw new ValidationException(StringUtils.collectionToCommaDelimitedString(validate));
		}
		
		activity.doIt(kingdomIdentifier, request);
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ValidationException.class)
	@ResponseBody
	public String handleRequestValidationException(HttpServletRequest request, ValidationException e) {
		log.severe(e.getMessage());
		return e.getMessage();
	}
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(KingdomNotFoundException.class)
	@ResponseBody
	public String handleKingdomNotFoundException(HttpServletRequest request, KingdomNotFoundException e) {
		log.severe(e.getMessage());
		return e.getMessage();
	}
}
