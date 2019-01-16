/**
 * Copyright(C) 2018 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.patrikdufresne.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main entry point when this library is executed from command line.
 * 
 * @author Patrik Dufresne
 *
 */
public class Main {

    public static void create(String publicKey, String privateKey, String name, String email, String expiration, String output)
            throws GeneralSecurityException,
            IOException {
        if (publicKey == null) {
            throw new IllegalArgumentException("public key is missing");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("private key is missing");
        }
        if (expiration == null) {
            throw new IllegalArgumentException("expiration date is missing");
        }

        // Create license obj
        License license = new License();
        license.setProperty(License.NAME, name);
        license.setProperty(License.EMAIL, email);
        license.setProperty(License.LICENSE_TYPE, License.TYPE_TRIAL);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        try {
            d = dateFormat.parse(expiration);
        } catch (ParseException e1) {
            throw new IllegalArgumentException("Wrong date value: " + expiration);
        }
        license.setExpiration(d);

        // Create the license file
        LicenseManager manager = new LicenseManager(publicKey, privateKey);
        manager.writeLicense(license, new File(output));

    }

    public static void main(String[] args) throws GeneralSecurityException {
        if (args.length == 0) {
            usage();
            return;
        }
        // Parse arguments
        String publicKey = null;
        String privateKey = null;
        String name = "";
        String email = "";
        String expiration = null;
        String file = "application.lic";

        String action = args[0];
        if (!action.equals("create") && !action.equals("validate")) {
            usage();
            return;
        }
        int idx = 1;
        while (idx < args.length) {
            switch (args[idx]) {
            case "-p":
            case "--public":
                idx++;
                publicKey = args[idx];
                break;
            case "-P":
            case "--private":
                idx++;
                privateKey = args[idx];
                break;
            case "-n":
            case "--name":
                idx++;
                name = args[idx];
                break;
            case "-m":
            case "--email":
                idx++;
                email = args[idx];
                break;
            case "-e":
            case "--expiration":
                idx++;
                expiration = args[idx];
                break;
            case "-f":
            case "--file":
                idx++;
                file = args[idx];
                break;
            default:
                System.err.println("unknown arguments: " + args[idx]);
                usage();
                return;
            }
            idx++;
        }
        // Take action.
        try {
            if (action.equals("create")) {
                create(publicKey, privateKey, name, email, expiration, file);
                System.out.println("license " + file + " created");
            } else if (action.equals("validate")) {
                try {
                    LicenseManager.validate(new FileInputStream(publicKey), new File(file));
                } catch (LicenseException e) {
                    System.out.println("license " + file + " invalid: " + e.getMessage());
                    System.exit(1);
                }
                System.out.println("license " + file + " valid");
            } else {
                usage();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    public static void usage() {
        System.out.println("usage:");
        System.out.println("  create");
        System.exit(1);
    }

}
