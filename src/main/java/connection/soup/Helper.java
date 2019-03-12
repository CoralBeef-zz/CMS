package connection.soup;

import com.google.gson.Gson;
import org.jsoup.nodes.Document;

import java.io.IOException;

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
}