package de.governikus.identification.report.setup;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;


/**
 * @author Pascal Knueppel
 * @since 07.11.2022
 */
@Data
@NoArgsConstructor
public class KeystoreEntry
{

  /**
   * an entry within the keystore entry of this application
   */
  private String alias;

  /**
   * the password to access the private key of this entry
   */
  private String privateKeyPassword;

  /**
   * the key type that might be RSA or EC
   */
  private String keyAlgorithm;

  /**
   * just added as meta information for easier code reading
   */
  private Integer keyLength;


  public KeystoreEntry(String alias, String privateKeyPassword, String keyAlgorithm, Integer keyLength)
  {
    this.alias = alias;
    this.privateKeyPassword = privateKeyPassword;
    this.keyAlgorithm = keyAlgorithm;
    this.keyLength = keyLength;
  }

  @SneakyThrows
  public PrivateKey getPrivateKey(KeyStore keyStore)
  {
    if (!keyStore.isKeyEntry(alias))
    {
      return null;
    }
    return (PrivateKey)keyStore.getKey(alias, privateKeyPassword.toCharArray());
  }

  @SneakyThrows
  public X509Certificate getCertificate(KeyStore keyStore)
  {
    return (X509Certificate)keyStore.getCertificate(alias);
  }
}
