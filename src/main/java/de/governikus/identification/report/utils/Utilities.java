package de.governikus.identification.report.utils;

import io.vertx.json.schema.OutputUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Pascal Knueppel
 * @since 02.11.2022
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utilities
{

  public static void logErrors(OutputUnit outputUnit)
  {
    if (outputUnit.getValid() != null && outputUnit.getValid())
    {
      return;
    }
    if (outputUnit.getErrors() == null)
    {
      log.info(outputUnit.toString());
      return;
    }
    for ( OutputUnit error : outputUnit.getErrors() )
    {
      logErrors(error);
    }
  }

}
