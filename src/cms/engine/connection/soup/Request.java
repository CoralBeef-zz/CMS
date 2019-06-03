package cms.engine.connection.soup;

import org.apache.http.Header;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

public class Request {
    public String url;
    public boolean useProxy;
    public boolean useCookie;
    public String userAgent;
    public String charset;
    public int timeout;
    public HttpRequestRetryHandler httpRequestRetryHandler;
    public ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy;
    public List<Header> headerList;

    public Request(String url) {
        this.url = url;
        useProxy = true;
        useCookie = true;
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";
        charset = "UTF-8";
        timeout = 5000;
        httpRequestRetryHandler = new DefaultHttpRequestRetryHandler();
        serviceUnavailableRetryStrategy = new DefaultServiceUnavailableRetryStrategy();
        headerList = new ArrayList<>();

        headerList.add(new BasicHeader( "Content-Type", "charset=UTF-8"));
    }

    public Request(String url, String charset_to_use) {
        this.url = url;
        useProxy = true;
        useCookie = true;
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";
        charset = charset_to_use;
        timeout = 5000;
        httpRequestRetryHandler = new DefaultHttpRequestRetryHandler();
        serviceUnavailableRetryStrategy = new DefaultServiceUnavailableRetryStrategy();
        headerList = new ArrayList<>();

        headerList.add(new BasicHeader( "Content-Type", "charset="+charset_to_use));
    }
}