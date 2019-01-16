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

Create the private key (containing information to create the public key).

  $ openssl genrsa -out privkey.pem 2048
  $ openssl pkcs8 -topk8 -in privkey.pem -inform PEM -nocrypt -outform DER -out privkey.der
 
Extract the public key, for publishing.
  $ openssl rsa -in privkey.pem -out pubkey.der -pubout -outform DER

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
			    <version>0.10</version>
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

## Generate a license file




# Java version
This library is compatible with Java7 and earlier.

# License

This program is free software; you can redistribute it and/or modify
it under the terms of Apache License 2.0
    
You should have received a copy of the Apache License 2.0
along with this program in the file named "LICENSE".

This package is based on the following article:
http://blog.afewguyscoding.com/2012/02/licensing-module-java/
