package de.governikus.identification.report.objects;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.governikus.identification.report.utils.Utilities;
import de.governikus.identification.report.validation.SchemaValidator;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.OutputUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;


/**
 * @author Pascal Knueppel
 * @since 27.10.2022
 */
@Data
@NoArgsConstructor
public abstract class AuthenticationObject
{

  private static final Map<Class<? extends AuthenticationObject>, String> TYPE_TO_SCHEMA_ID_MAP = new HashMap<>();

  /**
   * contains potentially additional attributes that are not defined within the schema
   */
  @JsonIgnore // prevents the map from getting serialized in a nested property named "keyValueMap"
  @JsonAnyGetter // will add all properties within the map as flattened properties into the json structure
  private Map<String, Object> additionalProperties = new HashMap<>();

  public AuthenticationObject(Map<String, Object> additionalProperties)
  {
    this.additionalProperties = Optional.ofNullable(additionalProperties).orElseGet(HashMap::new);
  }

  /**
   * will add flat added properties as values to this object
   */
  @JsonAnySetter
  public void add(String key, Object value)
  {
    additionalProperties.put(key, value);
  }

  /**
   * the location of the schema that can ge used to validate this object
   */
  @JsonIgnore
  protected abstract String getSchemaLocation();

  /**
   * the ID of the schema under {@link #getSchemaLocation()}
   */
  @SneakyThrows
  @JsonIgnore
  public String getSchemaId()
  {
    String schemaId = TYPE_TO_SCHEMA_ID_MAP.get(getClass());
    if (schemaId != null)
    {
      return schemaId;
    }
    String schemaLocation = getSchemaLocation();
    try (InputStream inputStream = getClass().getResourceAsStream(schemaLocation))
    {
      JsonObject jsonObject = new JsonObject(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
      schemaId = jsonObject.getString("$id");
      TYPE_TO_SCHEMA_ID_MAP.put(getClass(), schemaId);
    }
    return schemaId;
  }

  /**
   * validates this subtype against its corresponding schema definition
   */
  @JsonIgnore
  public OutputUnit validate()
  {
    OutputUnit outputUnit = SchemaValidator.validateJsonObject(getSchemaLocation(), JsonObject.mapFrom(this));
    Utilities.logErrors(outputUnit);
    return outputUnit;
  }
}
