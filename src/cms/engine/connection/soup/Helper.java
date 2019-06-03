package cms.engine.connection.soup;

import com.google.gson.Gson;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static Gson gson = new Gson();

    public static Document getDocument(String url) throws IOException {
        Request request = new Request(url);
        request.useCookie = false;

        return Http.getDocument(request);
    }

    public static Document getDocument(String url, String charset) throws IOException {
        Request request = new Request(url, charset);
        request.useCookie = false;

        return Http.getDocument(request);
    }

    public static String extractFirstMatch(String pattern, String text) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        if (matcher.find()) return matcher.group(0);
        else return "";
    }

    public static String extractFirstEmail(String text) {
        return extractFirstMatch("[\\w\\d\\.]+@[\\w\\d\\-\\.]+(\\.[\\w\\d\\-\\.]+)+", text);
    }
}