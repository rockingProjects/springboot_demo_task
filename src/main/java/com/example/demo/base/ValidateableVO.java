package com.example.demo.base;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class ValidateableVO implements Serializable {
	private static final long serialVersionUID = -1931188613814797156L;

	public Set<String> validate() {
		return validate((Set<String>) null);
	}

	/**
	 * Validates the VO, but ignoring all errors starting with values in
	 * ignoreSet
	 * 
	 * @param ignoreSet
	 *            errors to ignore
	 * @return set of errors
	 */
	public Set<String> validate(Set<String> ignoreSet) {
		Set<String> result = new TreeSet<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<ValidateableVO>> cv = validator.validate(this);
		Iterator<ConstraintViolation<ValidateableVO>> iterator = cv.iterator();
		if (cv.size() > 0) {
			while (iterator.hasNext()) {
				ConstraintViolation<ValidateableVO> next = iterator.next();
				if (ignoreSet == null) {
					result.add(next.getMessage());
				} else {
					String message = next.getMessage();
					boolean beginsWithElement = beginsWithElement(ignoreSet, message);
					if (!beginsWithElement) {
						result.add(message);						
					}
				}
			}
		}
		return result;
	}

	private static boolean beginsWithElement(Set<String> elements, String element) {
		for (String e : elements) {
			if (element.startsWith(e)) {
				return true;
			}
		}
		return false;
	}
}
