package de.governikus.identification.report.constants;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;


/**
 * @author Pascal Knüppel
 * @since 06.11.2022
 */
public class SchemaIdsTest
{

  /**
   * verifies that the ids within the schema do match their corresponding constants within the {@link SchemaIds}
   * class
   *
   * @param fileLocation the location of the file that should contain the expected id
   * @param schemaId the id that must match the id within the given file
   */
  @SneakyThrows
  @ParameterizedTest
  @CsvSource({SchemaLocations.IDENTIFICATION_REPORT_2_0_SCHEMA_LOCATION + "," + SchemaIds.IDENTIFICATION_REPORT_2_0_ID,
              SchemaLocations.NATURAL_PERSON_MINIMAL_SCHEMA_LOCATION + "," + SchemaIds.NATURAL_PERSON_MINIMAL_ID,
              SchemaLocations.NATURAL_PERSON_SCHEMA_LOCATION + "," + SchemaIds.NATURAL_PERSON_ID,
              SchemaLocations.LEGAL_PERSON_AUTHENTICATION_SCHEMA_LOCATION + "," + SchemaIds.LEGAL_PERSON_ID})
  public void testAuthenticationSchemaConstantsMatchFileIds(String fileLocation, String schemaId)
  {
    final String fileInput;
    try (InputStream inputStream = getClass().getResourceAsStream(fileLocation))
    {
      fileInput = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
    JsonObject schemaJson = new JsonObject(fileInput);
    Assertions.assertEquals(schemaJson.getString("$id"), schemaId);
  }

}
