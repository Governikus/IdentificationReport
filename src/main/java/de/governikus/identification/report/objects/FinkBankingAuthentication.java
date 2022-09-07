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
public class FinkBankingAuthentication extends AuthenticationObject
{

  private String firstName;

  private String lastName;

  @Builder
  public FinkBankingAuthentication(String firstName, String lastName, Map<String, Object> additionalProperties)
  {
    super(additionalProperties);
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  protected String getSchemaLocation()
  {
    return SchemaLocations.FINK_AML_AUTHENTICATION_SCHEMA_LOCATION;
  }
}
