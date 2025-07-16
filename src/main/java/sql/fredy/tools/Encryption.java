/*
   Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
    
   Permission is hereby granted, free of charge, to any person obtaining a copy 
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:
  
   The above copyright notice and this permission notice shall be included in
   all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.

 */
package sql.fredy.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author fredy
 */
public class Encryption {

    private String alphabet;
    private char[] schluessel;
    private String otext; // orignal Text
    private String vtext; // decryptet Text
    private String etext; // encrypted Text
    private int verschiebung = 3;  // this ist he encryption offset, as Gauius Julius Caesar did, we do as well and take 3 :-)

    public Encryption() {
        String zahlen = "0123456789";
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseLetters = uppercaseLetters.toLowerCase();
        String punctuation = ".,;:-_+\"\\%&/()=?¦@#¬|¢´^~{}[]!§°<>$£";
        //String umlauteKlein = "öäüèéçëïñôêâ";
        //String umlauteGross = umlauteKlein.toUpperCase();
        char[] umlaute = new char[38];
        for (int i = 0; i < umlaute.length; i++) {
            umlaute[i] = (char) (i + 128);
        }

        setAlphabet(lowercaseLetters.concat(zahlen).concat(punctuation).concat(uppercaseLetters).concat(new String(umlaute)) + " ");
        schluessel = getAlphabet().toCharArray();
    }

    public void encr() {
        encrypt(getOtext(), getVerschiebung());
    }

    public void decr() {
        int v = getVerschiebung() * (-1);
        etext = encrypt(getOtext(), v);
    }

    public String decrypt(String t, int v) {
        v = v * (-1);
        etext = encrypt(t, v);
        return etext;
    }

    public String getEncrypted(String t) {
        return encrypt(t, getVerschiebung());
    }

    /*
    Netto offset
     */
    public int getNettoverschiebung(int v) {
        boolean minus = false;
        int l = alphabet.length();

        if (v < 0) {
            minus = true;
        }
        if (minus) {
            v = v * -1;
        }
        int v2 = (v - ((v / l)) * l) - 1;

        if (minus) {
            v2 = v2 * -1;
        }
        return v2;
    }

    /*
     Verschluesselnn:
    wir verschluessseln den oText und legen den verschluessselten text im vText ab
     */
    public String encrypt(String t, int v) {

        char[] o = t.toCharArray();
        char[] z = new char[o.length];
        int oPosition = 0;
        int vPosition = 0;
        for (int i = 0; i < o.length; i++) {

            oPosition = alphabet.indexOf(o[i]);
            if (oPosition < 0) {
                z[i] = o[i];
            } else {
                vPosition = oPosition + getNettoverschiebung(v);
                if (vPosition > alphabet.length()) {
                    vPosition = vPosition - alphabet.length() - 1;
                }
                if (vPosition < 0) {
                    vPosition = alphabet.length() + vPosition + 1;
                }
                z[i] = schluessel[vPosition];
            }

            //System.out.print("|" + vPosition);
        }
        setVtext(new String(z));
        return new String(z);
    }

    /**
     * @return the alphabet
     */
    public String getAlphabet() {
        return alphabet;
    }

    /**
     * @param alphabet the alphabet to set
     */
    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * @return the schluessel
     */
    public char[] getSchluessel() {
        return schluessel;
    }

    /**
     * @param schluessel the schluessel to set
     */
    public void setSchluessel(char[] schluessel) {
        this.schluessel = schluessel;
    }

    /**
     * @return the oText
     */
    public String getOtext() {
        return otext;
    }

    /**
     * @param oText the oText to set
     */
    public void setOtext(String oText) {
        this.otext = oText;
    }

    /**
     * @return the vText
     */
    public String getVtext() {
        return vtext;
    }

    /**
     * @param vText the vText to set
     */
    public void setVtext(String vText) {
        this.vtext = vText;
    }

    /**
     * @return the eText
     */
    public String getEtext() {
        return etext;
    }

    /**
     * @param eText the eText to set
     */
    public void setEtext(String eText) {
        this.etext = eText;
    }

    /**
     * @return the verschiebung
     */
    public int getVerschiebung() {
        return verschiebung;
    }

    /**
     * @param verschiebung the verschiebung to set
     */
    public void setVerschiebung(int verschiebung) {
        this.verschiebung = verschiebung;
    }

    /*
      just for test purpose
     */
 /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

    public static void main(String args[]) {
        Encryption e = new Encryption();
        int offset = 33;
        e.setVerschiebung(offset);
        String myTextToEncrypt = "a cool and fancy passwort containing UPPERCASE and lowercase and figures 01234567898 and also special characters +*ç%&/()=?!$£";
        String encryptDecrypt = "e";
        //System.out.println("Encrypted: " + e.encrypt(myTextToEncrypt, offset) + "\nDecrypted: " + e.decrypt(e.encrypt(myTextToEncrypt, offset), offset));

        while (true) {
            encryptDecrypt = readFromPrompt("[E]ncrypt or [D]ecrypt? (q =quit)", encryptDecrypt);
            switch (encryptDecrypt.toLowerCase()) {
                case "e":
                    break;
                case "d":
                    break;
                case "q":
                    System.exit(0);
                default:
                    System.out.println("unknown command, quitting");
                    System.exit(0);
            }

            myTextToEncrypt = readFromPrompt(encryptDecrypt.equalsIgnoreCase("e") ? "Text to encrypt (q = quit)" : "Text to decrypt (q = quit)", "q");
            if (myTextToEncrypt.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            String off_set = readFromPrompt("Offset (q=quit)", "3");
            if (off_set.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            offset = Integer.parseInt(off_set);
            System.out.println(encryptDecrypt.equalsIgnoreCase("e") ? "Encrypted: " + e.encrypt(myTextToEncrypt, offset) + "\nDecrypted: " + e.decrypt(e.encrypt(myTextToEncrypt, offset), offset)
                    : "Decrypted: " + e.decrypt(myTextToEncrypt, offset) + "\nEncrypted: " + myTextToEncrypt);
            //System.out.println("Encrypted: " + e.encrypt(myTextToEncrypt, offset) + "\nDecrypted: " + e.decrypt(e.encrypt(myTextToEncrypt, offset), offset));
        }

    }
}
