package de.governikus.identification.report.objects;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.governikus.identification.report.constants.SchemaLocations;
import de.governikus.identification.report.setup.FileReferences;
import de.governikus.identification.report.validation.SchemaValidator;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Pascal Knueppel
 * @since 07.09.2022
 */
@Slf4j
public class EidAuthenticationTest implements FileReferences
{

  /**
   * verifies that an empty document is not accepted
   */
  @Test
  public void testEmptyEidAuthentication()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder().build();
    Assertions.assertFalse(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION,
                                                       eidAuthentication));
  }

  /**
   * verifies that only the restrictedId is required
   */
  @Test
  public void testEidAuthenticationWithRestrictedIdOnly()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .build();

    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION,
                                                      eidAuthentication));
  }

  /**
   * verifies that validation succeeds if only the freetext place is used within the address
   */
  @Test
  public void testFreetextPlaceIsAccepted()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .givenName("Max")
                                                           .familyName("Mustermann")
                                                           .dateOfBirth("01-01-1999")
                                                           .placeOfBirth("Bremen")
                                                           .birthName("Liebmann")
                                                           .placeOfResidence(Address.builder()
                                                                                    .freeTextPlace("street 1")
                                                                                    .build())
                                                           .build();
    JsonObject jsonObject = JsonObject.mapFrom(eidAuthentication);
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION, jsonObject));
  }

  /**
   * verifies that validation succeeds if only the noPlaceInfo place is used within the address
   */
  @Test
  public void testNoPlaceInfoIsAccepted()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .givenName("Max")
                                                           .familyName("Mustermann")
                                                           .dateOfBirth("01-01-1999")
                                                           .placeOfBirth("Bremen")
                                                           .birthName("Liebmann")
                                                           .placeOfResidence(Address.builder()
                                                                                    .noPlaceInfo("nowhere")
                                                                                    .build())
                                                           .build();
    JsonObject jsonObject = JsonObject.mapFrom(eidAuthentication);
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION, jsonObject));
  }

  /**
   * verifies that validation succeeds if only the noPlaceInfo place is used within the address
   */
  @Test
  public void testStructuredPlaceAndNoPlaceInfoAreNotAcceptedTogether()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .givenName("Max")
                                                           .familyName("Mustermann")
                                                           .dateOfBirth("01-01-1999")
                                                           .placeOfBirth("Bremen")
                                                           .birthName("Liebmann")
                                                           .placeOfResidence(Address.builder()
                                                                                    .street("some street")
                                                                                    .city("Bremen")
                                                                                    .noPlaceInfo("nowhere")
                                                                                    .build())
                                                           .build();
    JsonObject jsonObject = JsonObject.mapFrom(eidAuthentication);
    Assertions.assertFalse(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION, jsonObject));
  }

  /**
   * verifies that validation fails if there are several address types present
   */
  @Test
  public void testMixOfAddressTypes()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .givenName("Max")
                                                           .familyName("Mustermann")
                                                           .dateOfBirth("01-01-1999")
                                                           .placeOfBirth("Bremen")
                                                           .birthName("Liebmann")
                                                           .placeOfResidence(Address.builder()
                                                                                    .street("street 1")
                                                                                    .locality("bremen")
                                                                                    .region("bremen")
                                                                                    .country("D")
                                                                                    .zipCode("22222")
                                                                                    .noPlaceInfo("nowhere")
                                                                                    .freeTextPlace("somewhere")
                                                                                    .build())
                                                           .build();
    JsonObject jsonObject = JsonObject.mapFrom(eidAuthentication);
    Assertions.assertFalse(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION, jsonObject));
  }

  /**
   * verifies that the date of birth value is successfully accepted with the given values
   */
  @ParameterizedTest
  @ValueSource(strings = {"1-1-1999", "01-1-2000", "1-01-2001", "31-12-2002", "12-2003", "1-2004", "02-2005", "2006"})
  public void testDateOfBirth(String dateOfBirth)
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .givenName("Max")
                                                           .familyName("Mustermann")
                                                           .dateOfBirth(dateOfBirth)
                                                           .placeOfBirth("Bremen")
                                                           .birthName("Liebmann")
                                                           .build();
    JsonObject jsonObject = JsonObject.mapFrom(eidAuthentication);
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION, jsonObject));
  }

  /**
   * verifies that validation does not fail if additional properties unknown to the schema are added to the json
   * document
   */
  @SneakyThrows
  @Test
  public void testUseAdditionalProperties()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .givenName("Max")
                                                           .familyName("Mustermann")
                                                           .dateOfBirth("01-01-1999")
                                                           .placeOfBirth("Bremen")
                                                           .birthName("Liebmann")
                                                           .placeOfResidence(Address.builder()
                                                                                    .freeTextPlace("somewhere")
                                                                                    .build())
                                                           .additionalProperties(Map.of("custom", "custom"))
                                                           .build();
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION,
                                                      eidAuthentication));
    JsonObject jsonObject = JsonObject.mapFrom(eidAuthentication);
    Assertions.assertEquals("custom", jsonObject.getString("custom"));
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION, jsonObject));
  }

  /**
   * verifies that the schema-id is correctly extracted from the json schema that represents the specific object
   */
  @Test
  public void testSchemaIdIsCorrectlyExtracted()
  {
    EidAuthentication eidAuthentication = EidAuthentication.builder()
                                                           .restrictedId("1234567890123456789012345678901234567890123456789012345678901234")
                                                           .build();
    Assertions.assertEquals("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                            + "/schema/eid-authentication.json",
                            eidAuthentication.getSchemaId());
  }
}
