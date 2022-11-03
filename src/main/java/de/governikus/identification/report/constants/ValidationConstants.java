package de.governikus.identification.report.constants;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.governikus.identification.report.objects.AuthenticationObject;
import de.governikus.identification.report.objects.LegalPersonAuthentication;
import de.governikus.identification.report.objects.NaturalPersonAuthentication;
import de.governikus.identification.report.objects.NaturalPersonMinimalAuthentication;
import de.governikus.identification.report.validation.SchemaValidator;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.JsonSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;


/**
 * @author Pascal Knueppel
 * @since 01.11.2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationConstants
{

  /**
   * the jackson object mapper used to parse json into java pojos and vice versa
   */
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * this map will remember each schema in order to prevent the schemas from being parsed over and over again
   */
  private static final Map<String, JsonSchema> SCHEMA_MAP = new HashMap<>();

  /**
   * contains references to subtype-classes of {@link AuthenticationObject}. These will be used to identify a
   * fitting subtype-class based on a schema identifier ($id-attribute). The natural-person-schema.json for
   * example is identified by the schema-id
   * "https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0/schema/eid-authentication.json",
   * wich points to the subtype-class {@link NaturalPersonAuthentication}.
   */
  private static final Map<String, Class<? extends AuthenticationObject>> AUTH_OBJECT_SUB_TYPE_REFERENCES = new HashMap<>();

  /*
   * registers the schema-ids with their corresponding subject-ref class types
   */
  static
  {
    AUTH_OBJECT_SUB_TYPE_REFERENCES.put("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                                        + "/schema/eid-authentication.json",
                                        NaturalPersonAuthentication.class);
    AUTH_OBJECT_SUB_TYPE_REFERENCES.put("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                                        + "/schema/legal-person-authentication.json",
                                        LegalPersonAuthentication.class);
    AUTH_OBJECT_SUB_TYPE_REFERENCES.put("https://raw.githubusercontent.com/Governikus/IdentificationReport/2.0.0"
                                        + "/schema/natural-person-minimal.json",
                                        NaturalPersonMinimalAuthentication.class);

    OBJECT_MAPPER.registerModule(new JavaTimeModule());
  }

  /**
   * adds new custom references to the {@link #AUTH_OBJECT_SUB_TYPE_REFERENCES} map to be able to automatically
   * resolve the subtypes of an identification-report if the schemas id is present within the
   * subjectRefType-attribute
   *
   * @param schemaId the id of the schema that is referenced
   * @param authType the subytpe of {@link AuthenticationObject}
   */
  public static void addSchemaSubTypeReference(String schemaId, Class<? extends AuthenticationObject> authType)
  {
    AUTH_OBJECT_SUB_TYPE_REFERENCES.put(schemaId, authType);
  }

  /**
   * @param schemaId the schema identifier that identifies the java pojo subtype-class.
   * @return the java pojo subtype-class of {@link AuthenticationObject} belonging to the given schemaId
   */
  public static Class<? extends AuthenticationObject> getSubType(String schemaId)
  {
    return AUTH_OBJECT_SUB_TYPE_REFERENCES.get(schemaId);
  }

  /**
   * reads the schema from the given location and stores it within a static map in order to prevent continuous
   * parsing of the file
   *
   * @param schemaLocation the location of the schema file that should be retrieved as json schema instance
   */
  @SneakyThrows
  public static JsonSchema getSchema(String schemaLocation)
  {
    final String jsonSchemaString;
    JsonSchema schemaOptional = SCHEMA_MAP.get(schemaLocation);
    if (schemaOptional != null)
    {
      return schemaOptional;
    }

    try (InputStream inputStream = SchemaValidator.class.getResourceAsStream(schemaLocation))
    {
      jsonSchemaString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
    JsonObject object = new JsonObject(jsonSchemaString);
    JsonSchema schema = JsonSchema.of(object);

    SCHEMA_MAP.put(schemaLocation, schema);
    return schema;
  }
}
