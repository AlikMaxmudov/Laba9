package Laba7;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * scan html to find definite tags or response data
 * */
public class HtmlParser {

    private static final String HTTP_HEADER = "http://";

    /**
     * scan server answer for 301 (moved permanently) error
     * @return location or null if server response doesn't have this error.
     * */
    public static String scanForRedirect(String url, BufferedReader in) throws IOException {
        String firstLine = in.readLine();
        if (firstLine.equals("HTTP/1.1 301 Moved Permanently")) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Location:")) {
                    String redirectTo = line.substring(10);
                    if (!redirectTo.equals(url)) {
                        return redirectTo;
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * scan server answer to find \<a href = "http//:"\/>
     * @return found urls
     * */
    public static List<String> scanHttpLinks(BufferedReader in) throws IOException {
        String line;
        LinkedList<String> foundLinks = new LinkedList<>();
        while ((line = in.readLine()) != null) {
            Pattern linkPattern = Pattern.compile("<a href=\"" + HTTP_HEADER + "\\S+\">", Pattern.CASE_INSENSITIVE);
            Matcher matcher = linkPattern.matcher(line);
            while (matcher.find()) {
                int startIndex = matcher.group().indexOf(HTTP_HEADER);
                int endIndex = matcher.group().indexOf(">") - 1;
                foundLinks.add(matcher.group().substring(startIndex, endIndex));
            }
        }
        return foundLinks;
    }
}