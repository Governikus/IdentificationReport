package de.governikus.identification.report.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * @author Pascal Knüppel
 * @since 06.11.2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemaIds
{


  public static final String IDENTIFICATION_REPORT_2_0_ID = "https://raw.githubusercontent.com/Governikus/Identification"
                                                            + "Report/2.0.0/schema/identification-report.json";

  public static final String NATURAL_PERSON_MINIMAL_ID = "https://raw.githubusercontent.com/Governikus/Identification"
                                                         + "Report/2.0.0/schema/natural-person-minimal.json";

  public static final String NATURAL_PERSON_ID = "https://raw.githubusercontent.com/Governikus/IdentificationReport/"
                                                 + "2.0.0/schema/natural-person.json";

  public static final String LEGAL_PERSON_ID = "https://raw.githubusercontent.com/Governikus/IdentificationReport/"
                                               + "2.0.0/schema/legal-person-authentication.json";

}
