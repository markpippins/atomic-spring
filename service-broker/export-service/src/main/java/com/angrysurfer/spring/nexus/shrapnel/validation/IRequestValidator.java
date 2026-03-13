package com.angrysurfer.spring.nexus.shrapnel.validation;

import com.angrysurfer.spring.nexus.shrapnel.service.Request;

public interface IRequestValidator {

	void validate(Request request);
}
