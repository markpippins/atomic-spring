package com.angrysurfer.spring.atomic.shrapnel.validation;

import com.angrysurfer.spring.atomic.shrapnel.service.Request;

public interface IRequestValidator {

	void validate(Request request);
}
