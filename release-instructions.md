# Release Instructions

to release the project it is necessary to add a signature with help of the gpg plugin to the deployed artifacts. IN 
order to provide this signature enter the following part into your mavens `settings.xml` file.


```xml
<profiles>
    <profile>
        <id>ossrh</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <gpg.executable>C:\Program Files (x86)\GnuPG\bin\gpg.exe</gpg.executable>
            <gpg.keyname>${gpg-key-id}</gpg.keyname>
            <gpg.passphrase>${gpg-key-password}</gpg.passphrase>
        </properties>
    </profile>
</profiles>
```

Note that the path to the executable is simply an example and must be adjusted based on your system.

Afterwards execute:

1. `mvn clean release:prepare`
2. `mvn clean release:perform`
