
[![Build](http://git.patrikdufresne.com/pdsl/license/badges/master/build.svg)](http://git.patrikdufresne.com/pdsl/license)
[![Coverage](http://sonar.patrikdufresne.com/api/project_badges/measure?project=com.patrikdufresne%3Alicense&metric=coverage)](http://sonar.patrikdufresne.com/dashboard?id=com.patrikdufresne%3Alicense)

# How it work ?
This project provide a java library to be used by your java project to easily validate a license file.

1. You generate a private and public key.
2. You publish your application with this library and your public key.
3. You keep the private key for youself.

1. You generate a license file for your user with a deadline.
2. You are sending this file to your user (via email or similar).
3. Your user install this license file where your application can pick it up.
4. When your application start, this library is used to validate the license file and validate the deadline.

# Usage

## Generate your public and private key

Create the private key:

    openssl genrsa -out privkey.pem 2048
    openssl pkcs8 -topk8 -in privkey.pem -inform PEM -nocrypt -outform DER -out privkey.der
 
Extract the public key, for publishing:

    openssl rsa -in privkey.pem -out pubkey.der -pubout -outform DER

## Publish your application with this library
To make use of this license library, you must change a bit the implementation of your application. First, you must import the library. For maven project, you may edit the `pom.xml` as follow:

    <repositories>
	    <repository>
		    <id>patrikdufresne</id>
		    <url>http://nexus.patrikdufresne.com/content/groups/public/</url>
	    </repository>
    </repositories>

    ...

    <dependencies>
		    <dependency>
		    <groupId>com.patrikdufresne</groupId>
		    <artifactId>license</artifactId>
		    <version>0.11</version>
	    </dependency>
    </dependencies>


In your java code, you must implement something similar.

    License l;
    try {
        l = LicenseManager.validate(Main.class.getResourceAsStream("/pubkey.der"), new File("application.lic"));
    } catch (LicenseException e) {
        System.out.println("invalid license file:" + e.getMessage());
        System.exit(1);
        return;
    }
    System.out.println("License to: " + l.getProperty(License.NAME));
    System.out.println("Expired: " + l.getProperty(License.EXPIRATION));

Take a loot at http://git.patrikdufresne.com/pdsl/license/blob/master/docs/examples/Main.java

## Generate a license file

To generate a license file you may use the jar it self in command line:

    wget https://nexus.patrikdufresne.com/repository/public/com/patrikdufresne/license/0.11/license-0.11.jar
    java -jar license-0.11.jar create -p pubkey.der -P privkey.der -n username -m test@example.com -e 2020-01-09

It's generate a license file named `application.lic`.


# Notes
This library is compatible with Java 7 and earlier. This library doesn't need any dependecies.

