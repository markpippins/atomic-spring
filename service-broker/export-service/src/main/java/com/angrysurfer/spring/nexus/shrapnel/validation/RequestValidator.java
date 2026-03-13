package com.angrysurfer.spring.nexus.shrapnel.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.angrysurfer.spring.nexus.shrapnel.exception.InvalidExportRequestException;
import com.angrysurfer.spring.nexus.shrapnel.service.ExportsService;
import com.angrysurfer.spring.nexus.shrapnel.service.Request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Service
public class RequestValidator implements IRequestValidator {

    private final ExportsService exportsService;
    private final Validator validator;

    public RequestValidator(ExportsService exportsService) {
        this.exportsService = exportsService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public void validate(Request request) {

        Set< ConstraintViolation> violations = new HashSet<>();
        violations.addAll(this.validator.validate(request, IRequestValidation.RequestExport.class));

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            violations.forEach(v -> sb.append(v.getPropertyPath()).append(" ").append(v.getMessage()).append("\n"));
            throw new InvalidExportRequestException(String.format("Invalid DBExport Request:\n%s", sb.toString()));
        }

        if (!Arrays.asList(ExportsService.CSV, ExportsService.PDF, ExportsService.XLSX).contains(request.getFileType().toLowerCase(Locale.ROOT))) {
            throw new InvalidExportRequestException(String.format("Unknown file extension: %s.", request.getFileType()));
        }

        if (Objects.isNull(exportsService.getFactory(request))) {
            throw new InvalidExportRequestException(String.format("No factory found for %s.", request.getName()));
        }
    }
}
