package de.governikus.identification.report.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Pascal Knueppel
 * @since 02.11.2022
 */
public class NaturalPersonMinimalAuthenticationTest
{

  /**
   * verifies that the document is validating successfully if a firstname and lastname is present
   */
  @Test
  public void testIsValidatedSuccessfully()
  {
    NaturalPersonMinimalAuthentication authentication = NaturalPersonMinimalAuthentication.builder()
                                                                                          .givenName("Max")
                                                                                          .familyName("Mustermann")
                                                                                          .build();
    Assertions.assertTrue(authentication.validate().getValid());
  }

  /**
   * verifies that the schema-id is correctly extracted from the json schema that represents the specific object
   */
  @Test
  public void testSchemaIdIsCorrectlyExtracted()
  {
    NaturalPersonMinimalAuthentication authentication = NaturalPersonMinimalAuthentication.builder()
                                                                                          .givenName("Max")
                                                                                          .familyName("Mustermann")
                                                                                          .build();
    Assertions.assertEquals("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                            + "/schema/natural-person-minimal.json",
                            authentication.getSchemaId());
  }
}
