package de.governikus.identification.report.setup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;


/**
 * author Pascal Knueppel <br>
 * created at: 04.10.2019 - 20:07 <br>
 * <br>
 */
public interface FileReferences
{

  String BASE_PATH = "/de/governikus/identification/report";

  String IDENTIFICATION_REPORT_2_0 = BASE_PATH + "/identification-report-2.0.json";

  String IDENTIFICATION_REPORT_WITH_NATURAL_PERSON_SUBJECT_2_0 = BASE_PATH
                                                                 + "/identification-report-with-natural-person-subject-2.0.json";

  String IDENTIFICATION_REPORT_WITH_LEGAL_PERSON_SUBJECT_2_0 = BASE_PATH
                                                               + "/identification-report-with-legal-person-subject-2.0.json";

  String FINK_BANKING_REPORT = BASE_PATH + "/natural-person-minimal-report.json";

  /**
   * reads a file from the test-resources and modifies the content
   *
   * @param resourcePath the path to the resource
   * @return the resource read into a string value
   */
  default String readResourceFile(String resourcePath)
  {
    return readResourceFile(resourcePath, null);
  }

  /**
   * reads a file from the test-resources and modifies the content
   *
   * @param resourcePath the path to the resource
   * @param changeResourceFileContent a function on the file content to modify the return string
   * @return the resource read into a string value
   */
  default String readResourceFile(String resourcePath, Function<String, String> changeResourceFileContent)
  {
    try (InputStream resourceInputStream = getClass().getResourceAsStream(resourcePath))
    {
      String content = IOUtils.toString(resourceInputStream, StandardCharsets.UTF_8.name());
      if (changeResourceFileContent != null)
      {
        content = changeResourceFileContent.apply(content);
      }
      return content;
    }
    catch (IOException e)
    {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
