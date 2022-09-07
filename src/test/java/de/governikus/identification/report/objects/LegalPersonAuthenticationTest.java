package de.governikus.identification.report.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.governikus.identification.report.constants.SchemaLocations;
import de.governikus.identification.report.setup.FileReferences;
import de.governikus.identification.report.validation.SchemaValidator;


/**
 * @author Pascal Knueppel
 * @since 28.10.2022
 */
public class LegalPersonAuthenticationTest implements FileReferences
{

  /**
   * verifies that an empty document is not accepted
   */
  @Test
  public void testEmptyLegalPersonAuthentication()
  {
    LegalPersonAuthentication legalPersonAuthentication = LegalPersonAuthentication.builder().build();
    Assertions.assertFalse(SchemaValidator.isJsonValid(SchemaLocations.LEGAL_PERSON_AUTHENTICATION_SCHEMA_LOCATION,
                                                       legalPersonAuthentication));
  }

  /**
   * verifies that only the id attribute is required
   */
  @Test
  public void testEidAuthenticationWithRestrictedIdOnly()
  {
    LegalPersonAuthentication authentication = LegalPersonAuthentication.builder().id("abcdefghijklmno").build();

    Assertions.assertTrue(authentication.validate().getValid());
  }

  /**
   * verifies that a full legal person setup is accepted by the schema validation
   */
  @Test
  public void testCompleteResourceIsValid()
  {
    Address address = Address.builder()
                             .street("GROẞENHAINER STR. 133/135")
                             .locality("DRESDEN")
                             .country("D")
                             .zipCode("01129")
                             .build();
    LegalPersonAuthentication authentication = LegalPersonAuthentication.builder()
                                                                        .id("98828d19-55be-4624-84e7-81cea366963e")
                                                                        .organizationName("Organization A")
                                                                        .registerType("Handelsregister A")
                                                                        .registerPlace("Bremen")
                                                                        .registerNumber("HB-102")
                                                                        .legalForm("GmbH")
                                                                        .legalFormKey("12")
                                                                        .occupation("CEO")
                                                                        .occupationKey("1")
                                                                        .personId("daa98cb4-288f-4cee-8798-84470bafadca")
                                                                        .address(address)
                                                                        .build();
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.LEGAL_PERSON_AUTHENTICATION_SCHEMA_LOCATION,
                                                      authentication));
  }

  /**
   * verifies that the schema-id is correctly extracted from the json schema that represents the specific object
   */
  @Test
  public void testSchemaIdIsCorrectlyExtracted()
  {
    LegalPersonAuthentication authentication = LegalPersonAuthentication.builder().id("abcdefghijklmno").build();
    Assertions.assertEquals("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                            + "/schema/legal-person-authentication.json",
                            authentication.getSchemaId());
  }
}
