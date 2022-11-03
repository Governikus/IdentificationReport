package de.governikus.identification.report;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.governikus.identification.report.constants.SchemaLocations;
import de.governikus.identification.report.objects.AuthenticationObject;
import de.governikus.identification.report.objects.IdentificationReport;
import de.governikus.identification.report.objects.LevelOfAssurance;
import de.governikus.identification.report.objects.NaturalPersonAuthentication;
import de.governikus.identification.report.setup.FileReferences;
import de.governikus.identification.report.validation.SchemaValidator;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.OutputUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Pascal Knueppel
 * @since 05.09.2022
 */
@Slf4j
public class IdentificationReportTest implements FileReferences
{

  /**
   * validates identification reports with an empty subject and with two different types of subjects
   */
  @SneakyThrows
  @ParameterizedTest
  @ValueSource(strings = {IDENTIFICATION_REPORT_2_0, IDENTIFICATION_REPORT_WITH_NATURAL_PERSON_SUBJECT_2_0,
                          IDENTIFICATION_REPORT_WITH_LEGAL_PERSON_SUBJECT_2_0})
  public void testVerifyWithAndWithoutSubjects(String reportLocation)
  {
    JsonObject resource = new JsonObject(readResourceFile(reportLocation));
    Assertions.assertTrue(SchemaValidator.isJsonValid(SchemaLocations.IDENTIFICATION_REPORT_2_0_SCHEMA_LOCATION,
                                                      resource));
  }

  /**
   * this test shows that the validation fails if the required fields are missing
   */
  @Test
  public void testRequiredFieldsAreMissing()
  {
    IdentificationReport identificationReport = IdentificationReport.builder().build();

    JsonObject resource = identificationReport.toJson();

    OutputUnit result = SchemaValidator.validateJsonObject(SchemaLocations.IDENTIFICATION_REPORT_2_0_SCHEMA_LOCATION,
                                                           resource);

    Assertions.assertFalse(result.getValid(), result.getError());
    Assertions.assertEquals(5, result.getErrors().size());
    for ( OutputUnit error : result.getErrors() )
    {
      Assertions.assertEquals("required", error.getKeyword());
    }
  }

  /**
   * this test makes sure that the validation will fail if a customized property was added within the document
   * references
   */
  @Test
  public void testCustomPropertyInDocumentReferenceAdded()
  {
    // @formatter:off
    JsonObject documentReference = JsonObject.of("documentId", UUID.randomUUID().toString(),
                                                 "documentName", "test.pdf",
                                                 "customField", "unwanted property");
    // @formatter:on

    IdentificationReport identificationReport = IdentificationReport.builder()
                                                                    .reportId(UUID.randomUUID().toString())
                                                                    .serverIdentity("https://test.governikus-eid.de/gov_autent/async")
                                                                    .reportTime(Instant.now())
                                                                    .identificationTime(Instant.now())
                                                                    .levelOfAssurance(LevelOfAssurance.EIDAS_LOW)
                                                                    .documentReferences(List.of(documentReference))
                                                                    .build();
    JsonObject resource = identificationReport.toJson();

    OutputUnit result = SchemaValidator.validateJsonObject(SchemaLocations.IDENTIFICATION_REPORT_2_0_SCHEMA_LOCATION,
                                                           resource);

    Assertions.assertFalse(result.getValid(), result.getError());
    Assertions.assertTrue(result.getErrors()
                                .stream()
                                .anyMatch(error -> error.getInstanceLocation().endsWith("documentReferences")));
    Assertions.assertTrue(result.getErrors()
                                .stream()
                                .anyMatch(error -> error.getInstanceLocation().endsWith("customField")));
  }

  /**
   * makes sure that the level of assurance values are correctly deserialized
   */
  @ParameterizedTest
  @CsvSource({"http://eidas.europa.eu/LoA/high,EIDAS_HIGH", "http://eidas.europa.eu/LoA/substantial,EIDAS_SUBSTANTIAL",
              "http://eidas.europa.eu/LoA/low,EIDAS_LOW",
              "http://eidas.europa.eu/NotNotified/LoA/high,EIDAS_NOT_NOTIFIED_HIGH",
              "http://eidas.europa.eu/NotNotified/LoA/substantial,EIDAS_NOT_NOTIFIED_SUBSTANTIAL",
              "http://eidas.europa.eu/NotNotified/LoA/low,EIDAS_NOT_NOTIFIED_LOW",
              "http://bsi.bund.de/eID/LoA/hoch,BSI_EID_HIGH",
              "http://bsi.bund.de/eID/LoA/substantiell,BSI_EID_SUBSTANTIAL",
              "http://bsi.bund.de/eID/LoA/normal,BSI_EID_LOW", "unknown,UNKNOWN", ",UNKNOWN"})
  public void testLevelOfAssuranceIsDeserializable(String levelOfAssuranceValue, LevelOfAssurance expectedEnumValue)
  {
    JsonObject jsonObject = JsonObject.of("levelOfAssurance", levelOfAssuranceValue);
    IdentificationReport identificationReport = Assertions.assertDoesNotThrow(() -> {
      return IdentificationReport.fromJson(jsonObject.encode());
    });
    Assertions.assertEquals(expectedEnumValue, identificationReport.getLevelOfAssurance());
  }

  /**
   * makes sure that the subjectRef is correctly translated into the given type if set into the parser method
   */
  @ParameterizedTest
  // @formatter:off
  @CsvSource({IDENTIFICATION_REPORT_2_0
                + ",https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0/schema/eid-authentication.json"
                + ",de.governikus.identification.report.objects.NaturalPersonAuthentication",
              IDENTIFICATION_REPORT_WITH_NATURAL_PERSON_SUBJECT_2_0
                + ",https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0/schema/eid-authentication.json"
                + ",de.governikus.identification.report.objects.NaturalPersonAuthentication",
              IDENTIFICATION_REPORT_WITH_LEGAL_PERSON_SUBJECT_2_0
                + ",https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0/schema/legal-person-authentication.json"
                + ",de.governikus.identification.report.objects.LegalPersonAuthentication"})
  // @formatter:on
  public void testSubjectRefIsCorrectlyParsedBySubjectRefType(String jsonLocation,
                                                              String subjectRefType,
                                                              Class<? extends AuthenticationObject> expectecClassType)
  {
    final String json = readResourceFile(jsonLocation);
    JsonObject jsonObject = new JsonObject(json);
    jsonObject.put("subjectRefType", subjectRefType);

    IdentificationReport identificationReport = Assertions.assertDoesNotThrow(() -> {
      return IdentificationReport.fromJson(jsonObject.toString());
    });
    MatcherAssert.assertThat(identificationReport.getSubjectRef().getClass(),
                             Matchers.typeCompatibleWith(expectecClassType));
  }

  /**
   * makes sure that the subjectRef is correctly translated into the correct type if the subjectRefType is used
   * to identify the objects subtype
   */
  @ParameterizedTest
  // @formatter:off
  @CsvSource({IDENTIFICATION_REPORT_2_0 + ",de.governikus.identification.report.objects.NaturalPersonAuthentication",
              IDENTIFICATION_REPORT_WITH_NATURAL_PERSON_SUBJECT_2_0
                + ",de.governikus.identification.report.objects.NaturalPersonAuthentication",
              IDENTIFICATION_REPORT_WITH_LEGAL_PERSON_SUBJECT_2_0
                + ",de.governikus.identification.report.objects.LegalPersonAuthentication",
              FINK_BANKING_REPORT
                + ",de.governikus.identification.report.objects.NaturalPersonMinimalAuthentication"})
  // @formatter:on
  public void testSubjectRefIsCorrectlyParsed(String jsonLocation, Class<? extends AuthenticationObject> subjectRefType)
  {
    final String json = readResourceFile(jsonLocation);
    IdentificationReport identificationReport = Assertions.assertDoesNotThrow(() -> {
      return IdentificationReport.fromJson(json, subjectRefType);
    });
    MatcherAssert.assertThat(identificationReport.getSubjectRef().getClass(),
                             Matchers.typeCompatibleWith(subjectRefType));
    Assertions.assertTrue(identificationReport.validate());
  }

  /**
   * verifies that the values of {@link NaturalPersonAuthentication} are set
   */
  @Test
  public void testValuesOfEidAuthAreSet()
  {
    final String json = readResourceFile(IDENTIFICATION_REPORT_2_0);
    IdentificationReport identificationReport = Assertions.assertDoesNotThrow(() -> {
      return IdentificationReport.fromJson(json, NaturalPersonAuthentication.class);
    });

    Assertions.assertEquals("be4f9806-0b5f-45c3-a008-96fd2750f8cb", identificationReport.getReportId());
    Assertions.assertEquals("https://test.governikus-eid.de/gov_autent/async",
                            identificationReport.getServerIdentity());
    Assertions.assertEquals(Instant.parse("2020-06-25T10:20:39+02:00"), identificationReport.getReportTime());
    Assertions.assertEquals(Instant.parse("2020-06-25T10:19:54+02:00"), identificationReport.getIdentificationTime());
    Assertions.assertEquals("successful identification sent by SAML-Assertion", identificationReport.getIdStatement());
    Assertions.assertEquals(LevelOfAssurance.EIDAS_HIGH, identificationReport.getLevelOfAssurance());

    NaturalPersonAuthentication naturalPersonAuthentication = (NaturalPersonAuthentication)identificationReport.getSubjectRef();
    Assertions.assertEquals("John", naturalPersonAuthentication.getGivenName());
    Assertions.assertEquals("Doe", naturalPersonAuthentication.getFamilyName());
  }

}
