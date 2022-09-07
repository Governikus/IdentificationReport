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
 * @since 07.09.2022
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EidAuthentication extends AuthenticationObject
{

  private String restrictedId;

  private String givenName;

  private String familyName;

  private String dateOfBirth;

  private String placeOfBirth;

  private String birthName;

  private Address placeOfResidence;

  @Builder
  public EidAuthentication(String restrictedId,
                           String givenName,
                           String familyName,
                           String dateOfBirth,
                           String placeOfBirth,
                           String birthName,
                           Address placeOfResidence,
                           Map<String, Object> additionalProperties)
  {
    super(additionalProperties);
    this.restrictedId = restrictedId;
    this.givenName = givenName;
    this.familyName = familyName;
    this.dateOfBirth = dateOfBirth;
    this.placeOfBirth = placeOfBirth;
    this.birthName = birthName;
    this.placeOfResidence = placeOfResidence;
  }

  /**
   * the location to the schema "eid-authentication-schema.json" to validate this object type
   */
  @Override
  public String getSchemaLocation()
  {
    return SchemaLocations.EID_AUTHENTICATION_SCHEMA_LOCATION;
  }
}
