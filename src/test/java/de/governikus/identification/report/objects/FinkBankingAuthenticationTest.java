package de.governikus.identification.report.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Pascal Knueppel
 * @since 02.11.2022
 */
public class FinkBankingAuthenticationTest
{

  /**
   * verifies that the document is validating successfully if a firstname and lastname is present
   */
  @Test
  public void testIsValidatedSuccessfully()
  {
    FinkBankingAuthentication authentication = FinkBankingAuthentication.builder()
                                                                        .firstName("Max")
                                                                        .lastName("Mustermann")
                                                                        .build();
    Assertions.assertTrue(authentication.validate().getValid());
  }

  /**
   * verifies that the schema-id is correctly extracted from the json schema that represents the specific object
   */
  @Test
  public void testSchemaIdIsCorrectlyExtracted()
  {
    FinkBankingAuthentication authentication = FinkBankingAuthentication.builder()
                                                                        .firstName("Max")
                                                                        .lastName("Mustermann")
                                                                        .build();
    Assertions.assertEquals("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                            + "/schema/fink-aml-authentication.json",
                            authentication.getSchemaId());
  }
}
