package de.governikus.identification.report.objects;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;

import de.governikus.identification.report.constants.SchemaLocations;
import de.governikus.identification.report.constants.ValidationConstants;
import de.governikus.identification.report.jwt.JwtHandler;
import de.governikus.identification.report.utils.Utilities;
import de.governikus.identification.report.validation.SchemaValidator;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.OutputUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Pascal Knueppel
 * @since 06.09.2022
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentificationReport
{

  /**
   * Must be unique to a single report. GUID is recommended here
   */
  private String reportId;

  /**
   * This is a server id as software instance. This could be the server IP address, servername or the URL of the
   * SAML endpoint providing this identity report (like ServerIdentity in XTA2 Version 4)
   */
  private String serverIdentity;

  /**
   * Datetime of the creation of this report (like ReportTime in XTA2 Version 4)
   */
  private Instant reportTime;

  /**
   * Datetime of the identification process as stated in the original id statement
   */
  private Instant identificationTime;

  /**
   * The method that was used to authenticate the user and that determines the value of #/properties/loaSent
   */
  private String authenticationMethod;

  /**
   * in eIDAS contexts an id scheme (if it is notified) has a known LoA. When used in a national context only
   * the values from the authority bsi.bund.de SHALL be used, which correspond to the levels as defined in
   * [TR-03107-1]. See also BSI TR-03130
   */
  private LevelOfAssurance levelOfAssurance;

  /**
   * This is the value to indicate the status of the identification process. In case of failure the
   * corresponding reason shall be stated in the idStatement attribute
   */
  private String idStatus;

  /**
   * This is the error reason or some additional information in case of unknown or success situations. The
   * corresponding message should be human-readable
   */
  private String idStatement;

  /**
   * Describes the object type that is to be expected within the #/properties/subjectRef attribute in form of a
   * schema-id.
   */
  private String subjectRefType;

  /**
   * This should be a short link to the subject authenticated. Currently supported: firstName and lastName
   */
  private AuthenticationObject subjectRef;

  /**
   * The element corresponds to the TransactionContext in BSI TR-03130 which MAY be used to transmit context
   * information like an ID or a hash. To have a link between this identification report and the service for
   * which the identification process was started
   */
  private String contextInformation;

  /**
   * This element can contain references to documents including their hashes. This is an optional attribut.
   */
  private List<JsonObject> documentReferences;

  /**
   * additional elements that should be added to the json structure
   */
  @JsonIgnore // prevents the map from getting serialized in a nested property named "keyValueMap"
  @JsonAnyGetter // will add all properties within the map as flattened properties into the json structure
  @Builder.Default
  private Map<String, Object> keyValueMap = new HashMap<>();

  /**
   * will parse the given json string into an identification report with an internal {@link #subjectRef} that is
   * determined automatically by the inline value of {@link #subjectRefType}
   *
   * @param json the json string to parse evaluating the value of {@link #subjectRefType}
   * @return the representational identification report
   */
  @JsonIgnore
  @SneakyThrows
  public static IdentificationReport fromJson(String json)
  {
    JsonObject jsonObject = new JsonObject(json);
    return fromJson(jsonObject);
  }

  /**
   * will parse the given json string into an identification report with an internal {@link #subjectRef} that is
   * determined automatically by the inline value of {@link #subjectRefType}
   *
   * @param jsonObject the json object to parse evaluating the value of {@link #subjectRefType}
   * @return the representational identification report
   */
  @JsonIgnore
  @SneakyThrows
  public static IdentificationReport fromJson(JsonObject jsonObject)
  {
    final String subjectRefKey = "subjectRef";
    final String subjectRefTypeKey = "subjectRefType";

    String subjectRefType = jsonObject.getString(subjectRefTypeKey);
    Class<? extends AuthenticationObject> type = ValidationConstants.getSubType(subjectRefType);
    JsonObject subjectRefJson = jsonObject.getJsonObject(subjectRefKey);
    if (type == null && subjectRefJson != null)
    {
      throw new IllegalStateException(String.format("Unregistered schema with id '%s' found. Register the schema "
                                                    + "first with its corresponding subtype.",
                                                    subjectRefType));
    }
    jsonObject.remove(subjectRefKey);
    IdentificationReport identificationReport = ValidationConstants.OBJECT_MAPPER.readValue(jsonObject.toString(),
                                                                                            IdentificationReport.class);
    if (type == null)
    {
      return identificationReport;
    }
    String subjectRefJsonValue = subjectRefJson.toString();
    AuthenticationObject subjectRef = ValidationConstants.OBJECT_MAPPER.readValue(subjectRefJsonValue, type);
    identificationReport.setSubjectRef(subjectRef);
    return identificationReport;
  }

  /**
   * will parse the given json string into an identification report with an internal {@link #subjectRef} of the
   * given type
   *
   * @param json the json string to parse
   * @param subjectRefType the type of the {@link #subjectRef}. This should be possible to determine by
   *          evaluating the value of {@link #subjectRefType}
   * @return the representational identification report
   * @param <T> a subtype of {@link AuthenticationObject}
   */
  @JsonIgnore
  @SneakyThrows
  public static <T extends AuthenticationObject> IdentificationReport fromJson(String json, Class<T> subjectRefType)
  {
    JsonObject jsonObject = new JsonObject(json);
    return fromJson(jsonObject, subjectRefType);
  }

  /**
   * will parse the given json string into an identification report with an internal {@link #subjectRef} of the
   * given type
   *
   * @param jsonObject the json object to parse
   * @param subjectRefType the type of the {@link #subjectRef}. This should be possible to determine by
   *          evaluating the value of {@link #subjectRefType}
   * @return the representational identification report
   * @param <T> a subtype of {@link AuthenticationObject}
   */
  @JsonIgnore
  @SneakyThrows
  public static <T extends AuthenticationObject> IdentificationReport fromJson(JsonObject jsonObject,
                                                                               Class<T> subjectRefType)
  {
    final String subjectRefKey = "subjectRef";
    String subjectRefJson = jsonObject.getJsonObject(subjectRefKey).toString();
    AuthenticationObject subjectRef = ValidationConstants.OBJECT_MAPPER.readValue(subjectRefJson, subjectRefType);
    jsonObject.remove(subjectRefKey);
    IdentificationReport identificationReport = ValidationConstants.OBJECT_MAPPER.readValue(jsonObject.toString(),
                                                                                            IdentificationReport.class);
    identificationReport.setSubjectRef(subjectRef);
    return identificationReport;
  }

  /**
   * verifies if this object matches against its schemas definition
   *
   * @return true if the schema validation succeeds, false else
   */
  public boolean validate()
  {
    OutputUnit outputUnit = getValidationResult();
    Utilities.logErrors(outputUnit);
    return outputUnit.getValid();
  }

  /**
   * executes a schema validation on this object and returns the result of the validation
   *
   * @return the validation result
   */
  @JsonIgnore
  public OutputUnit getValidationResult()
  {
    Optional.ofNullable(subjectRef).map(AuthenticationObject::validate);
    return SchemaValidator.validateJsonObject(SchemaLocations.IDENTIFICATION_REPORT_2_0_SCHEMA_LOCATION,
                                              JsonObject.mapFrom(this));
  }

  /**
   * will parse instance into its json string representation
   */
  @JsonIgnore
  public JsonObject toJson()
  {
    return JsonObject.mapFrom(this);
  }

  /**
   * will add flat added properties as values to this object
   */
  @JsonAnySetter
  public void add(String key, Object value)
  {
    keyValueMap.put(key, value);
  }

  /**
   * used by jackson databind. With this method we map the null-value to {@link LevelOfAssurance#UNKNOWN}
   */
  @JsonSetter
  public void setLevelOfAssurance(LevelOfAssurance levelOfAssurance)
  {
    if (levelOfAssurance == null)
    {
      this.levelOfAssurance = LevelOfAssurance.UNKNOWN;
      return;
    }
    this.levelOfAssurance = levelOfAssurance;
  }

  /**
   * parses a JWS string into an identification report and verifies the signature of the JWS
   *
   * @param certificate the certificate needed to validate the signature
   * @param json the json string that should be validated and parsed
   * @param subjectRefType the type of the subjectRef within the identification report.
   * @return the body of the JWS parsed into an {@link IdentificationReport} instance
   */
  public static <T extends AuthenticationObject> IdentificationReport fromJws(X509Certificate certificate,
                                                                              String json,
                                                                              Class<T> subjectRefType)
  {
    JwtHandler jwtHandler = new JwtHandler(null, certificate);
    JwtHandler.PlainJwtData plainJwtData = jwtHandler.handleJwt(json);
    return fromJson(plainJwtData.getBody().toString(), subjectRefType);
  }

  @Override
  public String toString()
  {
    return toJson().encode();
  }
}
