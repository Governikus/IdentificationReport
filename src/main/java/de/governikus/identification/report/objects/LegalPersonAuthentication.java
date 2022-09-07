package de.governikus.identification.report.objects;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.governikus.identification.report.constants.SchemaLocations;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * @author Pascal Knueppel
 * @since 12.09.2022
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LegalPersonAuthentication extends AuthenticationObject
{

  private String id;

  private String organizationName;

  private String registerType;

  private String registerPlace;

  private String registerNumber;

  private String legalForm;

  private String legalFormKey;

  private String occupation;

  private String occupationKey;

  private String personId;

  private Address address;

  @Builder
  public LegalPersonAuthentication(String id,
                                   String organizationName,
                                   String registerType,
                                   String registerPlace,
                                   String registerNumber,
                                   String legalForm,
                                   String legalFormKey,
                                   String occupation,
                                   String occupationKey,
                                   String personId,
                                   Address address,
                                   Map<String, Object> additionalProperties)
  {
    super(additionalProperties);
    this.id = id;
    this.organizationName = organizationName;
    this.registerType = registerType;
    this.registerPlace = registerPlace;
    this.registerNumber = registerNumber;
    this.legalForm = legalForm;
    this.legalFormKey = legalFormKey;
    this.occupation = occupation;
    this.occupationKey = occupationKey;
    this.personId = personId;
    this.address = address;
  }

  /**
   * the location to the schema "legal-person-authentication-schema.json" to validate this object type
   */
  @Override
  public String getSchemaLocation()
  {
    return SchemaLocations.LEGAL_PERSON_AUTHENTICATION_SCHEMA_LOCATION;
  }
}
