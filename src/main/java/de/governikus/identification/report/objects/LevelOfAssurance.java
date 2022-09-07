package de.governikus.identification.report.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;


/**
 * @author Pascal Knueppel
 * @since 27.10.2022
 */
public enum LevelOfAssurance
{

  EIDAS_HIGH("http://eidas.europa.eu/LoA/high"),
  EIDAS_SUBSTANTIAL("http://eidas.europa.eu/LoA/substantial"),
  EIDAS_LOW("http://eidas.europa.eu/LoA/low"),
  EIDAS_NOT_NOTIFIED_HIGH("http://eidas.europa.eu/NotNotified/LoA/high"),
  EIDAS_NOT_NOTIFIED_SUBSTANTIAL("http://eidas.europa.eu/NotNotified/LoA/substantial"),
  EIDAS_NOT_NOTIFIED_LOW("http://eidas.europa.eu/NotNotified/LoA/low"),
  BSI_EID_HIGH("http://bsi.bund.de/eID/LoA/hoch"),
  BSI_EID_SUBSTANTIAL("http://bsi.bund.de/eID/LoA/substantiell"),
  BSI_EID_LOW("http://bsi.bund.de/eID/LoA/normal"),
  UNKNOWN("unknown");

  /**
   * the string value that is representing this level of assurance
   */
  @Getter
  @JsonValue
  private String value;

  LevelOfAssurance(String value)
  {
    this.value = value;
  }

  /**
   * used by jackson to identify the enum constant based on its value when deserializing a json string
   *
   * @param value the level of assurances value
   * @return the level of assurance or null
   */
  @JsonCreator
  public static LevelOfAssurance forValue(String value)
  {
    if (value == null)
    {
      return UNKNOWN;
    }
    for ( LevelOfAssurance levelOfAssurance : values() )
    {
      if (levelOfAssurance.value.equals(value))
      {
        return levelOfAssurance;
      }
    }
    throw new IllegalStateException(String.format("Cannot deserialize value for LevelOfAssurance '%s'", value));
  }
}
