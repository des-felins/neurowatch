package dev.cyberjar.neurowatch.ai.validation;

import dev.cyberjar.neurowatch.ai.domain.IncidentSignal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, IncidentSignal> {

    @Override
    public boolean isValid(IncidentSignal signal, ConstraintValidatorContext constraintValidatorContext) {

        if (signal == null) return true;
        return signal.to().isAfter(signal.from());
    }

}
