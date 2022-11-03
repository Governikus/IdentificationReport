package de.governikus.identification.report.objects;

import java.util.Map;

import de.governikus.identification.report.constants.SchemaLocations;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Pascal Knueppel
 * @since 02.11.2022
 */
@Data
@NoArgsConstructor
public class NaturalPersonMinimalAuthentication extends AuthenticationObject
{

  private String givenName;

  private String familyName;

  @Builder
  public NaturalPersonMinimalAuthentication(String givenName,
                                            String familyName,
                                            Map<String, Object> additionalProperties)
  {
    super(additionalProperties);
    this.givenName = givenName;
    this.familyName = familyName;
  }

  @Override
  protected String getSchemaLocation()
  {
    return SchemaLocations.NATURAL_PERSON_MINIMAL_SCHEMA_LOCATION;
  }
}
