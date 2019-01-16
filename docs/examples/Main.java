/**
 * Copyright(C) 2013 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ekwos;

import java.io.File;

import com.patrikdufresne.license.License;
import com.patrikdufresne.license.LicenseException;
import com.patrikdufresne.license.LicenseManager;

public class Main {

    public static void main(String[] args) {

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

    }

}
