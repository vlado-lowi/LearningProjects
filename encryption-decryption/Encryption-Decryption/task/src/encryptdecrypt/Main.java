package encryptdecrypt;

import java.io.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        String[] arguments = getArgumentValues(args);

        String mode = arguments[0];
        int key = Integer.parseInt(arguments[1]);
        String data = arguments[2];

        File in = null;
        File out = null;

        try {
            in = new File(arguments[3]);
        }   catch (Exception e) {
            System.out.println("Error, invalid input file name.");
            System.err.println(e.getMessage());
            System.exit(0);
        }

        try {
            out = new File(arguments[4]);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        //Set message which is to be processed
        String message;
        //Message from cmd has priority over msg from file
        if (!data.isEmpty()) {
            message = data;
        } else {
            message = getMessageFromFile(in);
        }

        String output;

        // Encrypt message
        if (Objects.equals(mode, "enc")) {
            // Encrypt the message
            output = encryptMessage(message, key);
        }

        // Decrypt cipher
        else {
            output = decryptMessage(message, key);
        }

        // Write result to file
        if (Objects.nonNull(out)) {
            printCipherToFile(out, output);
        }

        // Else write result to console
        else {
            System.out.println(output);
        }
    }

    private static String getMessageFromFile(File in) {
        String result = "";
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(in))) {
            result = bufferedReader.readLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    private static void printCipherToFile(File out, String cipher) {
        try (PrintWriter printWriter = new PrintWriter(
                new BufferedWriter(new FileWriter(out)))) {
            printWriter.println(cipher);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /*
    0 == -mode
    1 == -key
    2 == -data
    3 == -in
    4 == -out
     */
    private static String[] getArgumentValues(String[] args) {
        String[] result = new String[5];

        for (String arg : args) {
            switch (arg) {
                case "-mode":
                    result[0] = getArg("-mode", "enc", args);
                    break;
                case "-key":
                    result[1] = getArg("-key", "0", args);
                    break;
                case "-data":
                    result[2] = getArg("-data", "", args);
                    break;
                case "-in":
                    result[3] = getArg("-in", "", args);
                    break;
                case "-out":
                    result[4] = getArg("-out", "", args);
                    break;
            }
        }
        for (int i = 0; i < result.length; i++) {
            if (Objects.isNull(result[i])) {
                result[i] = "";
            }
        }
        //No value for argument
        if (result[3].equals("")) {
            System.out.println("Error, mode -in used but no filename specified.");
            System.exit(0);
        }

        return result;
    }

    private static String getArg(String arg, String defaultValue, String[] args) {
        String argValue = defaultValue;
        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], arg)) {
                try {
                    if (!args[i + 1].startsWith("-")) {
                        argValue = args[i + 1];
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return argValue;
    }

    private static String encryptMessage(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (int ch : text.toCharArray()) {
            ch = (ch + key) % 128;
            result.append((char)ch);
        }
        return result.toString();
    }

    private static String decryptMessage(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (int ch : text.toCharArray()) {
            ch = (ch - key) % 128;
            result.append((char)ch);
        }
        return result.toString();
    }
}
