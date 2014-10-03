/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.result;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang.text.StrBuilder;
import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.opengamma.util.ArgumentChecker;

/**
 * A result indicating the failure of a function.
 *
 * @param <T> the type of the underlying result which is only required
 * for allowing method signatures to match
 */
@BeanDefinition
public final class FailureResult<T>
    extends Result<T>
    implements ImmutableBean {

  /**
   * The set of failure instances.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final ImmutableSet<Failure> _failures;
  /**
   * The failure status.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final FailureStatus _status;
  /**
   * The failure message.
   */
  @PropertyDefinition(validate = "notNull", get = "private")
  private final String _message;

  //-------------------------------------------------------------------------
  /**
   * Obtains a failure result for a single failure instance.
   * 
   * @param failure  the failure instance, not null
   * @return the failure result, not null
   */
  static <U> Result<U> of(Failure failure) {
    ArgumentChecker.notNull(failure, "failure");
    return new FailureResult<>(ImmutableSet.of(failure), failure.getStatus(), failure.getMessage());
  }

  /**
   * Obtains a failure result for a non-empty list of failures.
   * 
   * @param failures  the failures, not empty, not null
   * @return the failure result, not null
   */
  static <U> Result<U> of(List<Failure> failures) {
    ArgumentChecker.notEmpty(failures, "failures");
    ImmutableSet<Failure> fails = ImmutableSet.copyOf(failures);
    FailureStatus status = fails.iterator().next().getStatus();
    StrBuilder buf = new StrBuilder();
    for (Failure failure : fails) {
      buf.appendSeparator(", ");
      buf.append(failure.getMessage());
      if (!status.equals(failure.getStatus())) {
        status = FailureStatus.MULTIPLE;
      }
    }
    Result<?> result = new FailureResult<>(fails, status, buf.toString());
    return Result.failure(result);
  }

  //-------------------------------------------------------------------------
  @ImmutableConstructor
  private FailureResult(Set<Failure> failures, FailureStatus status, String message) {
    _failures = ImmutableSet.copyOf(ArgumentChecker.notEmpty(failures, "failures"));
    _status = ArgumentChecker.notNull(status, "status");
    _message = ArgumentChecker.notEmpty(message, "message");
  }

  //-------------------------------------------------------------------------
  @Override
  public T getValue() {
    throw new IllegalStateException("Unable to get a value from a failure result. Message: " + getFailureMessage());
  }

  @Override
  public <U> Result<U> ifSuccess(Function<T, Result<U>> function) {
    return Result.failure(this);
  }

  @Override
  public <U, V> Result<V> combineWith(Result<U> other, Function2<T, U, Result<V>> function) {
    return Result.failure(this, other);
  }

  @Override
  public String getFailureMessage() {
    return _message;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  // deprecated methods --------------------------------------------------------------------

  /**
   * @return null
   * @deprecated use {@link #getFailures()} to find the cause of the failure
   */
  @Deprecated
  public ThrowableDetails getCauseDetails() {
    return null;
  }

  /**
   * Gets the error message associated with the failure.
   *
   * @return the value of the property, not null
   * @deprecated use {@link #getFailureMessage()}
   */
  @Deprecated
  public String getErrorMessage() {
    return getFailureMessage();
  }

  /**
   * @return the value of the property
   * @deprecated  always returns null, use {@link #getFailures()} to find the cause of the failure
   */
  @Deprecated
  public Exception getCause() {
    return null;
  }

 
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FailureResult}.
   * @return the meta-bean, not null
   */
  @SuppressWarnings("rawtypes")
  public static FailureResult.Meta meta() {
    return FailureResult.Meta.INSTANCE;
  }

  /**
   * The meta-bean for {@code FailureResult}.
   * @param <R>  the bean's generic type
   * @param cls  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> FailureResult.Meta<R> metaFailureResult(Class<R> cls) {
    return FailureResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FailureResult.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @param <T>  the type
   * @return the builder, not null
   */
  public static <T> FailureResult.Builder<T> builder() {
    return new FailureResult.Builder<T>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public FailureResult.Meta<T> metaBean() {
    return FailureResult.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the set of failure instances.
   * @return the value of the property, not null
   */
  @Override
  public ImmutableSet<Failure> getFailures() {
    return _failures;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the failure status.
   * @return the value of the property, not null
   */
  @Override
  public FailureStatus getStatus() {
    return _status;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the failure message.
   * @return the value of the property, not null
   */
  private String getMessage() {
    return _message;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder<T> toBuilder() {
    return new Builder<T>(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FailureResult<?> other = (FailureResult<?>) obj;
      return JodaBeanUtils.equal(getFailures(), other.getFailures()) &&
          JodaBeanUtils.equal(getStatus(), other.getStatus()) &&
          JodaBeanUtils.equal(getMessage(), other.getMessage());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getFailures());
    hash += hash * 31 + JodaBeanUtils.hashCode(getStatus());
    hash += hash * 31 + JodaBeanUtils.hashCode(getMessage());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("FailureResult{");
    buf.append("failures").append('=').append(getFailures()).append(',').append(' ');
    buf.append("status").append('=').append(getStatus()).append(',').append(' ');
    buf.append("message").append('=').append(JodaBeanUtils.toString(getMessage()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FailureResult}.
   * @param <T>  the type
   */
  public static final class Meta<T> extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code failures} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<Failure>> _failures = DirectMetaProperty.ofImmutable(
        this, "failures", FailureResult.class, (Class) ImmutableSet.class);
    /**
     * The meta-property for the {@code status} property.
     */
    private final MetaProperty<FailureStatus> _status = DirectMetaProperty.ofImmutable(
        this, "status", FailureResult.class, FailureStatus.class);
    /**
     * The meta-property for the {@code message} property.
     */
    private final MetaProperty<String> _message = DirectMetaProperty.ofImmutable(
        this, "message", FailureResult.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "failures",
        "status",
        "message");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 675938345:  // failures
          return _failures;
        case -892481550:  // status
          return _status;
        case 954925063:  // message
          return _message;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FailureResult.Builder<T> builder() {
      return new FailureResult.Builder<T>();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends FailureResult<T>> beanType() {
      return (Class) FailureResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code failures} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<Failure>> failures() {
      return _failures;
    }

    /**
     * The meta-property for the {@code status} property.
     * @return the meta-property, not null
     */
    public MetaProperty<FailureStatus> status() {
      return _status;
    }

    /**
     * The meta-property for the {@code message} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> message() {
      return _message;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 675938345:  // failures
          return ((FailureResult<?>) bean).getFailures();
        case -892481550:  // status
          return ((FailureResult<?>) bean).getStatus();
        case 954925063:  // message
          return ((FailureResult<?>) bean).getMessage();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code FailureResult}.
   * @param <T>  the type
   */
  public static final class Builder<T> extends DirectFieldsBeanBuilder<FailureResult<T>> {

    private Set<Failure> _failures = new HashSet<Failure>();
    private FailureStatus _status;
    private String _message;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FailureResult<T> beanToCopy) {
      this._failures = new HashSet<Failure>(beanToCopy.getFailures());
      this._status = beanToCopy.getStatus();
      this._message = beanToCopy.getMessage();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 675938345:  // failures
          return _failures;
        case -892481550:  // status
          return _status;
        case 954925063:  // message
          return _message;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder<T> set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 675938345:  // failures
          this._failures = (Set<Failure>) newValue;
          break;
        case -892481550:  // status
          this._status = (FailureStatus) newValue;
          break;
        case 954925063:  // message
          this._message = (String) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder<T> set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder<T> setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder<T> setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder<T> setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public FailureResult<T> build() {
      return new FailureResult<T>(
          _failures,
          _status,
          _message);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code failures} property in the builder.
     * @param failures  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<T> failures(Set<Failure> failures) {
      JodaBeanUtils.notNull(failures, "failures");
      this._failures = failures;
      return this;
    }

    /**
     * Sets the {@code status} property in the builder.
     * @param status  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<T> status(FailureStatus status) {
      JodaBeanUtils.notNull(status, "status");
      this._status = status;
      return this;
    }

    /**
     * Sets the {@code message} property in the builder.
     * @param message  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<T> message(String message) {
      JodaBeanUtils.notNull(message, "message");
      this._message = message;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("FailureResult.Builder{");
      buf.append("failures").append('=').append(JodaBeanUtils.toString(_failures)).append(',').append(' ');
      buf.append("status").append('=').append(JodaBeanUtils.toString(_status)).append(',').append(' ');
      buf.append("message").append('=').append(JodaBeanUtils.toString(_message));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
