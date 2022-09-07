package de.governikus.identification.report.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * @author Pascal Knueppel
 * @since 28.10.2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemaLocations
{

  public static final String BASE_PATH = "/de/governikus/identification/report/schemas";

  public static final String IDENTIFICATION_REPORT_1_0_SCHEMA_LOCATION = BASE_PATH
                                                                         + "/identification-report-schema-1.0.json";

  public static final String IDENTIFICATION_REPORT_2_0_SCHEMA_LOCATION = BASE_PATH
                                                                         + "/identification-report-schema-2.0.json";

  public static final String EID_AUTHENTICATION_SCHEMA_LOCATION = BASE_PATH + "/eid-authentication-schema.json";

  public static final String LEGAL_PERSON_AUTHENTICATION_SCHEMA_LOCATION = BASE_PATH
                                                                           + "/legal-person-authentication-schema.json";

  public static final String FINK_AML_AUTHENTICATION_SCHEMA_LOCATION = BASE_PATH
                                                                       + "/fink-banking-authentication-schema.json";
}
