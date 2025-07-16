/*
 This is a testing thing to verify, I'm able to find 'some' files in a directory
 */
package sql.fredy.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author a_tkfir
 */
public class FileLister {

    public FileLister(String directoryName, String fileStartPattern, String fileEndPattern) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryName))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {

                    String fileName = path.getFileName().toString();
                    boolean startpattern = false;
                    if ((fileStartPattern != null) && (fileName.toLowerCase().startsWith(fileStartPattern.toLowerCase()))) {
                        startpattern = true;
                    }
                    boolean endpattern = false;
                    if ((fileEndPattern != null) && (fileName.toLowerCase().startsWith(fileEndPattern.toLowerCase()))) {
                        endpattern = true;
                    }

                    if ((fileStartPattern != null) && (startpattern) && (fileEndPattern != null) && (endpattern)) {
                        // with start and end
                        System.out.println(path.toString());
                    } else {
                        if ((fileStartPattern != null) && (startpattern) && (fileEndPattern == null) && (!endpattern)) {
                            // only start
                            System.out.println(path.toString());
                        } else {
                            if ((fileStartPattern == null) && (!startpattern) && (fileEndPattern != null) && (endpattern)) {
                                // only end    
                            } else {
                                System.out.println(path.toString());
                            }
                        }
                    }
                }
            }
        } catch (IOException iox) {
        }

    }

    public void RegExLister(String directoryName, String pattern) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryName))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {

                    String fileName = path.getFileName().toString();

                    Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(path.getFileName().toString());
                    if (m.matches()) {
                        System.out.println(path.toString());
                    }
                }
            }
        } catch (IOException iox) {
        }
    }

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

    public static void main(String[] args) {
        String directory = null;
        String startFilePattern = null;
        String endFilePattern = null;

        directory = readFromPrompt("Directory (q = quit)", "q");
        if (directory.equalsIgnoreCase("q")) {
            System.exit(0);
        }

        startFilePattern = readFromPrompt("Starting Filename pattern", "null");
        if (startFilePattern.equalsIgnoreCase("q")) {
            System.exit(0);
        }
        if (startFilePattern.equalsIgnoreCase("null")) {
            startFilePattern = null;
        }

        endFilePattern = readFromPrompt("Ending Filename pattern", "null");
        if (endFilePattern.equalsIgnoreCase("q")) {
            System.exit(0);
        }

        if (endFilePattern.equalsIgnoreCase("null")) {
            endFilePattern = null;
        }
        FileLister fl = new FileLister(directory, startFilePattern, endFilePattern);

        while (true) {

            System.out.println("\n\nPattern matcher");
            String pattern = readFromPrompt("Pattern", "q");
            if (pattern.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            fl.RegExLister(directory, pattern);

        }
    }
}
