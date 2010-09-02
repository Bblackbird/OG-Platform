package com.opengamma.util.timeseries.fudge;

import java.util.TimeZone;

import org.fudgemsg.mapping.FudgeBuilderFor;

import com.opengamma.util.timeseries.zoneddatetime.ZonedDateTimeEpochMillisConverter;


/**
 * Fudge message builder (serializer/deserializer) for DateEpochMillisConverter
 */
@FudgeBuilderFor(ZonedDateTimeEpochMillisConverter.class)
public class ZonedDateTimeEpochMillisConverterBuilder extends DateTimeConverterBuilder<ZonedDateTimeEpochMillisConverter> {
  @Override
  public ZonedDateTimeEpochMillisConverter makeConverter(TimeZone timeZone) {
    return new ZonedDateTimeEpochMillisConverter(timeZone);
  }
}
