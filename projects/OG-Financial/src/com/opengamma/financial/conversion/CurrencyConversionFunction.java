/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.conversion;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;

/**
 * Converts a value from one currency to another, preserving all other properties.
 */
public class CurrencyConversionFunction extends AbstractFunction.NonCompiledInvoker {

  private static final Logger s_logger = LoggerFactory.getLogger(CurrencyConversionFunction.class);

  private final ComputationTargetType _targetType;
  private final Set<String> _valueNames;
  private String _rateLookupValueName = "CurrencyConversion";
  private String _rateLookupIdentifierScheme = "CurrencyConversion";

  public CurrencyConversionFunction(final ComputationTargetType targetType, final String valueName) {
    ArgumentChecker.notNull(targetType, "targetType");
    ArgumentChecker.notNull(valueName, "valueName");
    _targetType = targetType;
    _valueNames = Collections.singleton(valueName);
  }

  public CurrencyConversionFunction(final ComputationTargetType targetType, final String... valueNames) {
    ArgumentChecker.notNull(targetType, "targetType");
    ArgumentChecker.notEmpty(valueNames, "valueNames");
    _targetType = targetType;
    _valueNames = new HashSet<String>(Arrays.asList(valueNames));
  }

  public void setRateLookupValueName(final String rateLookupValueName) {
    ArgumentChecker.notNull(rateLookupValueName, "rateLookupValueName");
    _rateLookupValueName = rateLookupValueName;
  }

  public String getRateLookupValueName() {
    return _rateLookupValueName;
  }

  public void setRateLookupIdentifierScheme(final String rateLookupIdentifierScheme) {
    ArgumentChecker.notNull(rateLookupIdentifierScheme, "rateLookupIdentifierScheme");
    _rateLookupIdentifierScheme = rateLookupIdentifierScheme;
  }

  public String getRateLookupIdentifierScheme() {
    return _rateLookupIdentifierScheme;
  }

  protected Set<String> getValueNames() {
    return _valueNames;
  }

  private ValueRequirement getInputValueRequirement(final ValueRequirement desiredValue) {
    return new ValueRequirement(desiredValue.getValueName(), desiredValue.getTargetSpecification(), desiredValue.getConstraints().copy().withAny(ValuePropertyNames.CURRENCY).get());
  }

  /**
   * Multiples the value (as a double) by the conversion rate. Override this in a subclass for anything more elaborate - e.g. if 
   * the value is in "somethings per currency unit foo" so needs dividing by the "foo to bar" conversion rate.
   * 
   * @param inputValue input value to convert
   * @param desiredValue requested value requirement
   * @param conversionRate conversion rate to use
   * @return the converted value
   */
  protected Object convertValue(final ComputedValue inputValue, final ValueRequirement desiredValue, final double conversionRate) {
    final Object value = inputValue.getValue();
    if (!(value instanceof Double)) {
      s_logger.warn("Can't convert {} to {}", inputValue, desiredValue);
      return null;
    }
    return (Double) value * conversionRate;
  }

  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    final Set<ComputedValue> results = Sets.newHashSetWithExpectedSize(desiredValues.size());
    final Collection<ComputedValue> inputValues = inputs.getAllValues();
    desiredValueLoop: for (ValueRequirement desiredValue : desiredValues) {
      final ValueRequirement inputRequirement = getInputValueRequirement(desiredValue);
      for (ComputedValue inputValue : inputValues) {
        if (inputRequirement.isSatisfiedBy(inputValue.getSpecification())) {
          final String inputCurrency = inputValue.getSpecification().getProperty(ValuePropertyNames.CURRENCY);
          final String outputCurrency = desiredValue.getConstraint(ValuePropertyNames.CURRENCY);
          s_logger.debug("Converting from {} to {}", inputCurrency, outputCurrency);
          final ValueRequirement rateRequirement = getCurrencyConversion(inputCurrency, outputCurrency);
          final Object rate = inputs.getValue(rateRequirement);
          if (!(rate instanceof Double)) {
            s_logger.warn("Invalid rate {} for {}", rate, rateRequirement);
            continue desiredValueLoop;
          }
          final Object converted = convertValue(inputValue, desiredValue, (Double) rate);
          if (converted != null) {
            results.add(new ComputedValue(new ValueSpecification(desiredValue, desiredValue.getConstraints()), converted));
          }
          continue desiredValueLoop;
        }
      }
    }
    return results;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    return target.getType() == getTargetType();
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    // Actual input requirement is desired requirement with the currency wild-carded
    return Collections.singleton(getInputValueRequirement(desiredValue));
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    // Maximal set of outputs is the valueNames with the infinite property set
    final ComputationTargetSpecification targetSpec = target.toSpecification();
    if (getValueNames().size() == 1) {
      return Collections.singleton(new ValueSpecification(getValueNames().iterator().next(), targetSpec, ValueProperties.all()));
    } else {
      final Set<ValueSpecification> result = new HashSet<ValueSpecification>();
      for (String valueName : getValueNames()) {
        result.add(new ValueSpecification(valueName, targetSpec, ValueProperties.all()));
      }
      return result;
    }
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target, final Set<ValueSpecification> inputs) {
    // Resolved outputs are the inputs with the currency wild-carded - even the function ID will be preserved
    final Set<ValueSpecification> result = Sets.newHashSetWithExpectedSize(inputs.size());
    for (ValueSpecification input : inputs) {
      result.add(new ValueSpecification(input.getValueName(), input.getTargetSpecification(), input.getProperties().copy().withAny(ValuePropertyNames.CURRENCY).get()));
    }
    return result;
  }

  private Set<String> getCurrencies(final Set<ValueSpecification> specs) {
    final Set<String> currencies = new HashSet<String>();
    for (ValueSpecification spec : specs) {
      currencies.add(spec.getProperty(ValuePropertyNames.CURRENCY));
    }
    return currencies;
  }

  private ValueRequirement getCurrencyConversion(final String fromCurrency, final String toCurrency) {
    return new ValueRequirement(getRateLookupValueName(), new ComputationTargetSpecification(ComputationTargetType.PRIMITIVE, UniqueIdentifier.of(getRateLookupIdentifierScheme(), fromCurrency + "_"
        + toCurrency)));
  }

  @Override
  public Set<ValueRequirement> getAdditionalRequirements(final FunctionCompilationContext context, final ComputationTarget target, final Set<ValueSpecification> inputs,
      final Set<ValueSpecification> outputs) {
    s_logger.debug("FX requirements for {} -> {}", inputs, outputs);
    final Set<String> inputCurrencies = getCurrencies(inputs);
    final Set<String> outputCurrencies = getCurrencies(outputs);
    if ((inputCurrencies.size() == 1) && (outputCurrencies.size() == 1)) {
      return Collections.singleton(getCurrencyConversion(inputCurrencies.iterator().next(), outputCurrencies.iterator().next()));
    } else {
      // NOTE 2010-10-27 Andrew -- The cross product is not optimal for all input/output possibilities (e.g. IN={A1,B2}, OUT={A3,B4} gives the unused 1->4 and 2->3) but the current graph typically
      // won't produce such complex nodes
      final Set<ValueRequirement> rateLookups = Sets.newHashSetWithExpectedSize(inputCurrencies.size() * outputCurrencies.size());
      for (String inputCurrency : inputCurrencies) {
        for (String outputCurrency : outputCurrencies) {
          rateLookups.add(getCurrencyConversion(inputCurrency, outputCurrency));
        }
      }
      return rateLookups;
    }
  }

  @Override
  public ComputationTargetType getTargetType() {
    return _targetType;
  }

}
