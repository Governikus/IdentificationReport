# Identification Report
JSON-Schema for a certain kind of payload to JSON Web Token (Signature)

## Motivation
Online identification is a transient process. The corresponding assertions have a short time to live to prevent attacks (e.g. replay attacks). Furthermore assertions and user token are generally encrypted. Therefore it is barely possible to prove a successful identification process later since you cannot use the assertions or user token. 
Then it is sometimes handy to have a token that proves a successful identification process. This little schema started as a first approach to have such a piece of text. 
The followig sequence diagram show the idea behind this identification report

![Sample-Sequence](doc/uml/ID-Report-Sequence.png?raw=true "Sample Sequence")

## Schema Description
Some ideas are taken from the XTA2 Service Report [(see "XTA 2 Version 4" here)](https://www.xoev.de/downloads-2316#XTA). Some were results of discussions with the public administration in Germany. 

The following attributes are currently in place.

Attribute    | Type          | Description
---- | ------------ | -------------
reportID | string  | Must be unique to a single report. GUID is recommended here.
serverIdentity | string | This is a server id as software instance. This could be the server IP address, servername or the URL of the SAML endpoint providing this identity report (like ServerIdentity in XTA2 Version 4).
reportTime | string  (date-time) | Datetime of the creation of this report (like ReportTime in XTA2 Version 4). 
identificationDate | string (date-time) |  Datetime of the identification process as stated in the original id statement.
idStatus | string  (enum) | This is the value to indicate the status of the identification process. In case of failure the corresponding reason shall be stated in the idStatement attribute.
idStatement | string |  This is the error reason or some additional information in case of unknown or success situations. The corresponding message should be human-understandable.
subjectRef | object  | This should be a short link to the subject authenticated. Currently supported: firstName and lastName. This can easily extended but keep in mind that this field is sensible according to data privacy and should contain just enough data to link to a person.
contextInformation |array (string) |  The element corresponds to the TransactionContext in BSI TR-03130 which MAY be used to transmit context information. To have a link between this identification report and the service for which the identification process was started.
documentReferences | array (objects) |  This element can contain references to documents including their hashes. This is an optional attribute since this is not always needed. This might be useful in some usecases.
loaSent | string (enum) | in eIDAS contexts an id scheme (if it is notified) has a known LoA. When used in a national context only the values from the authority ```bsi.bund.de``` SHALL be used, which correspond to the levels as defined in [TR-03107-1](https://www.bsi.bund.de/SharedDocs/Downloads/DE/BSI/Publikationen/TechnischeRichtlinien/TR03107/TR-03107-1.pdf). See also [BSI TR-03130](https://www.bsi.bund.de/DE/Publikationen/TechnischeRichtlinien/tr03130/tr-03130.html).


## Sample Data
The schema can be found in the ```schema``` directory. You will find the JSON Web Token there as well as the payload. If you want to verify the sample on your own feel free to use the certificate and key found in [Signature Material](#signature-material).

### Header
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```
### Report Sample (as payload for JWT) 
See also [sample-report](doc/sample/sample-report.json)

```json
{
    "reportID": "be4f9806-0b5f-45c3-a008-96fd2750f8cb",
    "serverIdentity": "https://test.governikus-eid.de/gov_autent/async",
    "reportTime": "2020-06-25T10:20:39+02:00",
    "identificationDate": "2020-06-25T10:19:54+02:00",
    "idStatus": "success",
    "subjectRef":  {"firstName": "John","lastName": "Doe" },
    "contextInformation": ["Antrag auf Kindergeld: 0815/763763"],
    "idStatement": "successful identification sent by SAML-Assertion",
    "documentReferences":[{
      "documentName":"test.pdf",
      "hashAlgo":"SHA-256","digest":"0c2720631b927e25d5cb8b5ca2b9408c552ea76797e3419245931296732fd0d2"
  }],
    "loaSent": "http://eidas.europa.eu/LoA/low"
  }
```
### Signature Materia
If you want to verifiy the JWT yourself feel free to use the test keys found here. 

#### Private Key
```
-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7YMQYIKlMQUVf
qFUiK1Pyrf26b/T/7Fhbu0L2t4XFcZFfqAjEYgiiuy3PWxBjVZmQWyxZ1NpN9qkt
N8+QrTvNGGyB/3MSHsHOEXLFYCXy4Oc5O/rMCI0cuTmqH2y+t0PJ2mku+xg9P6ZF
M8ZSi84mUgzj3rh0g0GOSC7Xm6azeN66Fzq0dDRAAMRw74JNXYJKId/pSkCMJh+U
6zx1EISyyw1izFBWPlXF4KBKbUM9iKZAkczsKjHfGrsAfOwweJxAUV/xOApe0yow
XGCO9epNKaJZReogRAgUZArMRw8psBr/W2PUzzuE1CQnFI5ZT7P9pbrGCQEFmPnw
J1OkMHSVAgMBAAECggEALhSVBO37VIJ0whqOnTGUmojJbjEcSlfgPublh/EIF89f
FewoA8usHgD0OhtGA3fIpSSW2pyq9yNi5XjlNco0e7K29J1SujEQLlJCLGIB0yIW
GxAyFD1CHr86fCjwQFKnfBuXZHNhuaZOiJQ8AI7bKrdg7iUWraid1ZTQ2DsyGs6C
Ay5RjzTgQbyjVpgz5ZJq9bnN/n5eZZvmPVfBgjDifKVMwyIbpdRlgQ/o9nfaQ8ml
R78pG6vIED0/4RlVkKsJqxh3IvpJAES80Ly/BhGrXYkKlsmO4z3riAS7YYfsICTV
ADHot1YKVvAdp+9dTRK5pZtD/5nJvw5Y4UyK6ncp3QKBgQDjU+RvCkPk0T//KQaT
aOVLfUACNmGG1mIVoFgzlTrFuSRAoU/sTx6hn3xrD7JbZiZjcSL+lek8LeHnM+wv
+C9KQPtpcKelVmV5d8YyEVWZ5fjBKGEKsRiiTMpPABGYMCnsNPw+Z6KcRNXiHjqw
5Dzqk136w+pxfWJpeKJV2PJ4uwKBgQDTAvNsUlQGlF4dZprTEUnvm0vxW7uV1Cmq
iYh9eoFwjFeg1IA89F1Doysa52P2MK8rZCmF4KK2kcx7UgzC/IGDE43Gai1PwXKc
7BbMA4gt+sLl3RKXd7vlhMHRbWSxYOmtSf/0qL4bfmjUQq8N6XhF7GLiy8i8Fri3
tKJDDCBa7wKBgQCpP3dueI0N2gC0nz3HGCzG5Ex4mTZJJmnGQigI14z3Up08BR21
CT78A/qk1v3qcIYaOUxdkQ0iAeMTuKObH0NOHE1SNk6KmWVZZyRHeIJr3z0xyjdd
t3Zot5VT9fAjh5BezSAT8iKuB83Z2LnHo1X7K5ansZ9luX1Am1D5a1kzKwKBgDo2
djFMBSJLjCtQQyY3fs47aZgMVpPfFB+YEa+MPGlwTjxwY3Btec1PFnU1oL4qcCjo
WQw2DK2RS3g6CotAXfprSpGSeS2nkJb6Cs/9qXQF2f7QHnDq90s6dL3yD/VfZxgH
cjVs2AV9ui3Ut7Z+0k94B0/KKHa7TfpgOuOEVOjBAoGARl9/k3hFAfxk0Qj40aob
8IpPZq7FgVa3hTQp/HklnRYpDTyHYReZ3fIexanUG4tlHcj1B9d6tJ+6qYJEVa2i
rhd/fZdC9MKJv+iN6e36uFj/3ClV1zS5p9E7L3bG0MiRrmT0kQ0mPc9TN74O1W6V
4x2FW4oPb7cbheWNp63VYAY=
-----END PRIVATE KEY-----
```
#### Certificate
```
-----BEGIN CERTIFICATE-----
MIIDgjCCAmoCCQDIfIBQuwJTuTANBgkqhkiG9w0BAQsFADCBgjELMAkGA1UEBhMC
REUxFDASBgNVBAgMC1Rlc3QgQ291bnR5MREwDwYDVQQHDAhUZXN0Y2l0eTEUMBIG
A1UECgwLVGVzdGluZyBpbmMxEjAQBgNVBAsMCVRlc3QgVGVhbTEgMB4GA1UEAwwX
VGVzdGtleXMgdG8gcGxheSBhcm91bmQwHhcNMjAwNjE1MTAzMDIyWhcNMjEwNjE1
MTAzMDIyWjCBgjELMAkGA1UEBhMCREUxFDASBgNVBAgMC1Rlc3QgQ291bnR5MREw
DwYDVQQHDAhUZXN0Y2l0eTEUMBIGA1UECgwLVGVzdGluZyBpbmMxEjAQBgNVBAsM
CVRlc3QgVGVhbTEgMB4GA1UEAwwXVGVzdGtleXMgdG8gcGxheSBhcm91bmQwggEi
MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC7YMQYIKlMQUVfqFUiK1Pyrf26
b/T/7Fhbu0L2t4XFcZFfqAjEYgiiuy3PWxBjVZmQWyxZ1NpN9qktN8+QrTvNGGyB
/3MSHsHOEXLFYCXy4Oc5O/rMCI0cuTmqH2y+t0PJ2mku+xg9P6ZFM8ZSi84mUgzj
3rh0g0GOSC7Xm6azeN66Fzq0dDRAAMRw74JNXYJKId/pSkCMJh+U6zx1EISyyw1i
zFBWPlXF4KBKbUM9iKZAkczsKjHfGrsAfOwweJxAUV/xOApe0yowXGCO9epNKaJZ
ReogRAgUZArMRw8psBr/W2PUzzuE1CQnFI5ZT7P9pbrGCQEFmPnwJ1OkMHSVAgMB
AAEwDQYJKoZIhvcNAQELBQADggEBAKUrroGncW1h1g4qGsFUNZB6390sNUkOQVRJ
sj2AuQg+4cheMaIk4pUftEDtmO0kiNcP2CcdhGWoK0hNtBEiMrOhGpaCtfTcba8e
5VzMTs7MghFRgjxA4I74I6Xk9NLuTNv36bbNKVKQWDRatsYdj/ZuJurnGfprYz3W
9d2VaSSd3T17ipW/bPEweKwpsiPkIEOUObLzZAxAbw+yQfhikXnaqdflfjThDfWO
iA9VqGJiwkCnv8YGq3kuaVwx/6hF/zUGa3c7fDow6BQoozNCkLLqT030LCln/wlv
8cMMAMcF1RnGLhofYyy7lRzIOC7gdaN3p2+TcyDsz6qTT1GZavs=
-----END CERTIFICATE-----
```  
