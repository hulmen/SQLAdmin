/*
 * The MIT License
 *
 * Copyright 2024 fredy.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author fredy
 */
public class ReadRessource {

    public void Reader(String fileName) {
        InputStream input;
        String line = null;
        try {

            /*
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            input = classloader.getResourceAsStream(fileName);
            */
            /*
            input = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(fileName);
             */
            URL url = this.getClass().getResource(fileName);
            input = url.openStream();
            
            
            if (input != null) {
                try (InputStreamReader isr = new InputStreamReader(input); BufferedReader br = new BufferedReader(isr);) {

                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    input.close();
                }
            } else {
                System.out.println(fileName + " not found, InputStream is null");
            }
        } catch (IOException e) {
            System.out.println("Exception " + e.getMessage() + " while trying to read " + fileName);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ReadRessource rr = new ReadRessource();
        while (true) {
            System.out.println("Enter file to read from resources directory (q= quit): ");
            String fileName = scanner.nextLine();
            if (fileName.equalsIgnoreCase("q")) {
                break;
            }
            rr.Reader(fileName);
        }

    }
}
