package de.governikus.identification.report.validation;

import java.util.Optional;

import de.governikus.identification.report.constants.ValidationConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.OutputUnit;
import io.vertx.json.schema.Validator;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Pascal Knueppel
 * @since 28.10.2022
 */
@Slf4j
public class SchemaValidator
{

  /**
   * reads the JSON schema from the given location and validates the json object against the given schema. This
   * method will print the results from the validation on info-level.
   *
   * @param schemaLocation the schemas location that is used to validate the json object
   * @param jsonObject the json object that should be validated
   * @return true if the object matches the schemas definitions
   */
  public static boolean isJsonValid(String schemaLocation, JsonObject jsonObject)
  {
    OutputUnit result = validateJsonObject(schemaLocation, jsonObject);
    Optional.ofNullable(result.getErrors()).ifPresent(errors -> errors.forEach(error -> log.info(error.toString())));
    return result.getValid();
  }

  /**
   * reads the JSON schema from the given location and validates the json object against the given schema. This
   * method will print the results from the validation on info-level.
   *
   * @param schemaLocation the schemas location that is used to validate the json object
   * @param pojo the object that should be validated
   * @return true if the object matches the schemas definitions
   */
  public static boolean isJsonValid(String schemaLocation, Object pojo)
  {
    return isJsonValid(schemaLocation, JsonObject.mapFrom(pojo));
  }

  /**
   * reads the JSON schema from the given location and validates the json object against the given schema. This
   * method can be used if the output-result from the validation API is directly needed. This might be necessary
   * in cases if the object is validated only for a specific or if the errors should be logged in another way as
   * the default.
   *
   * @param schemaLocation the schemas location that is used to validate the json object
   * @param pojo the json object representation that should be validated
   * @return true if the object matches the schemas definitions
   */
  public static OutputUnit validateJsonObject(String schemaLocation, Object pojo)
  {
    return validateJsonObject(schemaLocation, JsonObject.mapFrom(pojo));
  }

  /**
   * reads the JSON schema from the given location and validates the json object against the given schema. This
   * method can be used if the output-result from the validation API is directly needed. This might be necessary
   * in cases if the object is validated only for a specific or if the errors should be logged in another way as
   * the default.
   *
   * @param schemaLocation the schemas location that is used to validate the json object
   * @param jsonObject the json object that should be validated
   * @return true if the object matches the schemas definitions
   */
  public static OutputUnit validateJsonObject(String schemaLocation, JsonObject jsonObject)
  {
    JsonSchema schema = ValidationConstants.getSchema(schemaLocation);
    return validateJsonObject(schema, jsonObject);
  }

  /**
   * reads the JSON schema from the given location and validates the json object against the given schema. This
   * method can be used if the output-result from the validation API is directly needed. This might be necessary
   * in cases if the object is validated only for a specific or if the errors should be logged in another way as
   * the default.
   *
   * @param schema used to validate the json object with the existing schema
   * @param jsonObject the json object that should be validated
   * @return true if the object matches the schemas definitions
   */
  public static OutputUnit validateJsonObject(JsonSchema schema, JsonObject jsonObject)
  {
    return Validator.create(schema,
                            new JsonSchemaOptions().setBaseUri("https://identification-report.de")
                                                   .setDraft(Draft.DRAFT202012))
                    .validate(jsonObject);
  }
}
