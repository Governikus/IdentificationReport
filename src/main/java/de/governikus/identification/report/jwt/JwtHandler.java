package de.governikus.identification.report.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Optional;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


/**
 * @author Pascal Knueppel
 * @since 04.11.2022
 */
@RequiredArgsConstructor
public class JwtHandler
{

  /**
   * key for signing or decrypting a jwt
   */
  private final PrivateKey privateKey;

  /**
   * key for encrypting or validating a signature of a jwt
   */
  private final X509Certificate certificate;

  /**
   * creates a signed JWT (JWS) based on the given key material
   *
   * @param body the body that should either be encrypted or signed
   * @return the signed or encrypted JWT
   */
  @SneakyThrows
  public String createJws(String body)
  {
    JWSAlgorithm signatureAlgorithm = selectSignatureAlgorithm();
    JWSHeader jwsHeader = buildJwsHeader(signatureAlgorithm);
    return createSignedJwt(jwsHeader, body);
  }

  /**
   * creates an encrypted JWT (JWE) based on the given key material
   *
   * @param body the body that should either be encrypted or signed
   * @return the signed or encrypted JWT
   */
  @SneakyThrows
  public String createJwe(String body)
  {
    JWEAlgorithm keyWrapAlgorithm = selectJweKeyWrapAlgorithm();
    EncryptionMethod contentEncryptionMethod = EncryptionMethod.A256GCM;
    JWEHeader jweHeader = buildJweHeader(keyWrapAlgorithm, contentEncryptionMethod);
    return createEncryptedJwt(jweHeader, body);
  }

  /**
   * verifies either the signature of a signed JWT (JWS) or tries to decrypt the given JWT (JWE)
   *
   * @param jwt the signed or encrypted JWT
   * @return the payload of the verified or decrypted content
   */
  @SneakyThrows
  public PlainJwtData handleJwt(String jwt)
  {
    final int numberOfJwtParts = jwt.split("\\.").length;
    if (numberOfJwtParts == 3)
    {
      return verifySignature(jwt);
    }
    else
    {
      return decryptJwt(jwt);
    }
  }

  /**
   * selects automatically the signature algorithm based on the given key
   */
  @SneakyThrows
  private JWSHeader buildJwsHeader(JWSAlgorithm signatureAlgorithm)
  {
    Base64URL sha256Thumbprint = getSha256Thumbprint();
    JWSHeader jwsHeader;
    switch (certificate.getPublicKey().getAlgorithm())
    {
      case "RSA":
        jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS512).x509CertSHA256Thumbprint(sha256Thumbprint).build();
        break;
      case "EC":
        jwsHeader = new JWSHeader.Builder(signatureAlgorithm).x509CertSHA256Thumbprint(sha256Thumbprint).build();
        break;
      default:
        throw new IllegalArgumentException(String.format("Key of type '%s' is not supported for JWT signatures",
                                                         certificate.getPublicKey().getAlgorithm()));
    }
    return jwsHeader;
  }

  /**
   * automatically selects the EC algorithm based on the given key type
   */
  private JWSAlgorithm selectSignatureAlgorithm()
  {
    switch (certificate.getPublicKey().getAlgorithm())
    {
      case "RSA":
        return JWSAlgorithm.RS512;
      case "EC":
        Integer keyLength = getKeyLength();
        switch (keyLength)
        {
          case 256:
            if (((ECPublicKey)certificate.getPublicKey()).getParams().toString().startsWith("secp256k1"))
            {
              return JWSAlgorithm.ES256K;
            }
            return JWSAlgorithm.ES256;
          case 384:
            return JWSAlgorithm.ES384;
          case 521:
            return JWSAlgorithm.ES512;
          default:
            throw new IllegalArgumentException(String.format("Unsupported key length for EC key type: %s-bit",
                                                             keyLength));
        }
      default:
        throw new IllegalArgumentException(String.format("Unsupported key type '%s'",
                                                         certificate.getPublicKey().getAlgorithm()));
    }
  }

  /**
   * selects automatically the encryption algorithm based on the given key
   */
  private JWEHeader buildJweHeader(JWEAlgorithm keyWrapAlgorithm, EncryptionMethod contentEncryptionMethod)
  {
    Base64URL sha256Thumbprint = getSha256Thumbprint();
    return new JWEHeader.Builder(keyWrapAlgorithm, contentEncryptionMethod).x509CertSHA256Thumbprint(sha256Thumbprint)
                                                                           .build();
  }

  /**
   * automatically selects the key wrap algorithm for the encrypted json webtoken
   */
  private JWEAlgorithm selectJweKeyWrapAlgorithm()
  {
    switch (certificate.getPublicKey().getAlgorithm())
    {
      case "RSA":
        return JWEAlgorithm.RSA_OAEP_512;
      case "EC":
        return JWEAlgorithm.ECDH_ES_A256KW;
      default:
        throw new IllegalArgumentException(String.format("Unsupported key type for JWT encryption '%s'",
                                                         certificate.getPublicKey().getAlgorithm()));
    }
  }

  /**
   * adds the sha-256 thumbprint to the JWT header
   */
  @SneakyThrows
  private Base64URL getSha256Thumbprint()
  {
    return Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded()));
  }

  /**
   * verifies the signature based on the data within the header
   */
  @SneakyThrows
  private PlainJwtData verifySignature(String jws)
  {
    SignedJWT signedJwt = SignedJWT.parse(jws);
    JWSVerifier jwsVerifier = getVerifier(signedJwt.getHeader());
    boolean isValid = signedJwt.verify(jwsVerifier);
    if (!isValid)
    {
      throw new IllegalStateException("Signature validation has failed with signature key");
    }
    return PlainJwtData.builder()
                       .operationExecuted(OperationExecuted.SIGNATURE_VERIFIED)
                       .header(signedJwt.getHeader())
                       .body(signedJwt.getPayload())
                       .build();
  }

  /**
   * decrypts the JWT based on the data within the header
   *
   * @return the decrypted content of the JWT
   */
  @SneakyThrows
  private PlainJwtData decryptJwt(String jwt)
  {
    EncryptedJWT encryptedJWT = EncryptedJWT.parse(jwt);
    JWEHeader jweHeader = encryptedJWT.getHeader();
    Payload plainTextBody = decryptJwt(encryptedJWT);
    return PlainJwtData.builder()
                       .operationExecuted(OperationExecuted.DECRYPTED)
                       .header(jweHeader)
                       .body(plainTextBody)
                       .build();
  }

  /**
   * decrypts the given encrypted JWT with the given keypair
   *
   * @return the plain content of the decrypted JWT
   */
  @SneakyThrows
  private Payload decryptJwt(EncryptedJWT encryptedJWT)
  {
    JWEDecrypter jweDecrypter = getJweDecrypter();
    encryptedJWT.decrypt(jweDecrypter);
    return encryptedJWT.getPayload();
  }

  /**
   * gets fitting decrypter based on the type of key that was selected
   */

  @SneakyThrows
  private JWEDecrypter getJweDecrypter()
  {
    switch (certificate.getPublicKey().getAlgorithm())
    {
      case "RSA":
        RSAKey rsaKey = toRsaJwk();
        return new RSADecrypter(rsaKey);
      case "EC":
        ECKey ecKey = toEcJwk();
        return new ECDHDecrypter(ecKey);
      default:
        throw new IllegalArgumentException(String.format("Cannot sign with key of type '%s'",
                                                         certificate.getPublicKey().getAlgorithm()));
    }
  }

  /**
   * builds an encrypted JWT with the given public key based on the data within the header
   *
   * @param jweHeader contains the algorithm to use for encryption
   * @param body the body that should be encrypted
   * @return the encrypted JWT
   */
  @SneakyThrows
  private String createEncryptedJwt(JWEHeader jweHeader, String body)
  {
    JWEEncrypter jweEncrypter = getJweEncrypter();
    JWECryptoParts jweCryptoParts = jweEncrypter.encrypt(jweHeader, body.getBytes(StandardCharsets.UTF_8));
    EncryptedJWT encryptedJWT = new EncryptedJWT(jweCryptoParts.getHeader().toBase64URL(),
                                                 jweCryptoParts.getEncryptedKey(),
                                                 jweCryptoParts.getInitializationVector(),
                                                 jweCryptoParts.getCipherText(), jweCryptoParts.getAuthenticationTag());
    return encryptedJWT.serialize();
  }

  /**
   * selects an encrypter based on the given key
   */
  @SneakyThrows
  private JWEEncrypter getJweEncrypter()
  {
    switch (certificate.getPublicKey().getAlgorithm())
    {
      case "RSA":
        RSAKey rsaKey = toRsaJwk();
        return new RSAEncrypter(rsaKey);
      case "EC":
        ECKey ecKey = toEcJwk();
        return new ECDHEncrypter(ecKey);
      default:
        throw new IllegalArgumentException(String.format("Cannot encrypt with key of type '%s'",
                                                         certificate.getPublicKey().getAlgorithm()));
    }
  }

  /**
   * builds a signature with the given key and builds the signed JWT from it
   *
   * @param jwsHeader contains the algorithm to use for signature
   * @param body the body to sign together with the header
   * @return the signed JWT
   */
  @SneakyThrows
  private String createSignedJwt(JWSHeader jwsHeader, String body)
  {

    JWK jwk = toJwk();
    JWSSigner jwsSigner = new DefaultJWSSignerFactory().createJWSSigner(jwk, jwsHeader.getAlgorithm());
    Payload payload = new Payload(body);
    String headerAndBody = jwsHeader.toBase64URL().toString() + "." + payload.toBase64URL().toString();
    Base64URL signature = jwsSigner.sign(jwsHeader, headerAndBody.getBytes(StandardCharsets.UTF_8));
    SignedJWT jws = new SignedJWT(jwsHeader.toBase64URL(), Base64URL.encode(body), signature);
    return jws.serialize();
  }

  /**
   * parsed an RSA or EC key to its JWK representation
   */
  private JWK toJwk()
  {
    switch (certificate.getPublicKey().getAlgorithm())
    {
      case "RSA":
        return toRsaJwk();
      case "EC":
        return toEcJwk();
      default:
        return null; // should not happen due to previous validation
    }
  }

  /**
   * parses an EC key pair into its EC JWK representation
   */
  private ECKey toEcJwk()
  {
    ECPublicKey ecPublicKey = (ECPublicKey)certificate.getPublicKey();
    ECParameterSpec ecParameterSpec = ecPublicKey.getParams();
    Curve curve = Curve.forECParameterSpec(ecParameterSpec);
    ECKey.Builder builder = new ECKey.Builder(curve, ecPublicKey);
    Optional.ofNullable(privateKey).ifPresent(builder::privateKey);
    return builder.build();
  }

  /**
   * parses an RSA key pair into its RSA JWK representation
   */
  private RSAKey toRsaJwk()
  {
    RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey)certificate.getPublicKey());
    Optional.ofNullable(privateKey).ifPresent(builder::privateKey);
    return builder.build();
  }

  /**
   * retrieves the signature verifier based on the data within the JWS header
   */
  @SneakyThrows
  private JWSVerifier getVerifier(JWSHeader header)
  {
    boolean isRsaAlgorithm = JWSAlgorithm.Family.RSA.stream().anyMatch(rsa -> rsa.equals(header.getAlgorithm()));
    if (isRsaAlgorithm)
    {
      return new RSASSAVerifier((RSAPublicKey)certificate.getPublicKey());
    }

    boolean isEcAlgorithm = JWSAlgorithm.Family.EC.stream().anyMatch(ec -> ec.equals(header.getAlgorithm()));
    if (isEcAlgorithm)
    {
      return new ECDSAVerifier((ECPublicKey)certificate.getPublicKey());
    }

    String errorMessage = String.format("Unsupported algorithm found '%s'", header.getAlgorithm());
    throw new IllegalArgumentException(errorMessage);
  }

  /**
   * gets the key length of the currently provided key material
   *
   * @return the length of the key.
   */
  private Integer getKeyLength()
  {
    PublicKey publicKey = certificate.getPublicKey();
    switch (publicKey.getAlgorithm())
    {
      case "RSA":
        return ((RSAPublicKey)publicKey).getModulus().bitLength();
      case "EC":
        return ((ECPublicKey)publicKey).getParams().getOrder().bitLength();
      default:
        throw new IllegalStateException(String.format("Not supporting keys of type '%s'", publicKey.getAlgorithm()));
    }
  }

  /**
   * represents the plain data of a JWT
   */
  @Getter
  @Builder
  public static class PlainJwtData
  {

    /**
     * tells us which operation was executed on the JWT if its signature was verified or if it was decrypted
     */
    private final OperationExecuted operationExecuted;

    /**
     * the JWT header
     */
    private final Header header;

    /**
     * the JWT body
     */
    private final Payload body;
  }

  public static enum OperationExecuted
  {
    SIGNATURE_VERIFIED, DECRYPTED
  }
}
