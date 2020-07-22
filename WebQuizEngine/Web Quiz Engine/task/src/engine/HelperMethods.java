package engine;

import org.apache.tomcat.util.codec.binary.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperMethods {
    /**
     * @param email Email to be validated.
     * @return True if email is considered valid by RFC 5322 specification, else false.
     */
    public static boolean validateEmail(String email) {
        // General Email Regex (RFC 5322 Official Standard)
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Extract username from Basic authorization string.
     * @param authorizationString String do be decoded (Base64)
     * @return the username that was decoded from authorizationString or null if wasn't able to decode the string
     */
    public static String getUsernameFromAuthorizationString(String authorizationString) {
        if (authorizationString.contains("Basic ")){
            String stringToDecode = authorizationString.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.decodeBase64(stringToDecode);
            String decodedString =  new String(decodedBytes);
            // splitting decoded string from format username:password
            String[] split = decodedString.split(":");
            return split[0];
        }
        return null;
    }
}
