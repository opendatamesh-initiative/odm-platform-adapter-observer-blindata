package org.opendatamesh.platform.up.metaservice.blindata.validator.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.PolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.PolicyEvaluationResultRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.services.BlindataValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "validator/evaluate-policy")
@Tag(
        name = "Policies evaluation API",
        description = "API to evaluate one policy for a given object"
)
public class BlindataValidatorController {

    @Autowired
    private BlindataValidatorService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Evaluate an object",
            description = "Evaluate an object against the provided policy",
            tags = {"Policies evaluation API"}
    )
    public PolicyEvaluationResultRes evaluate(
            @Parameter(description = "JSON object containing the object to be evaluated and the policy to validate against")
            @Valid @RequestBody PolicyEvaluationRequestRes document
    ) {
        return service.validateDataProduct(document);
    }
}
