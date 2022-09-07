package de.governikus.identification.report.objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Pascal Knueppel
 * @since 07.09.2022
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address
{

  private String street;

  private String city;

  private String state;

  private String locality;

  private String region;

  private String country;

  private String zipCode;

  private String noPlaceInfo;

  private String freeTextPlace;

}
