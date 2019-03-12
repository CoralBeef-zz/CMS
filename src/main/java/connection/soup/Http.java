package connection.soup;

import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import com.google.gson.stream.JsonReader;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Http {
    private static PoolingHttpClientConnectionManager pool;
    private static BasicCookieStore cookie;

    static {
        pool = new PoolingHttpClientConnectionManager();
        pool.setMaxTotal(10);
        cookie = new BasicCookieStore();
    }

    private static CloseableHttpClient buildHttpClient(Request request) {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setConnectionManager(pool);
        clientBuilder.setDefaultCookieStore(cookie);
        clientBuilder.setRetryHandler(request.httpRequestRetryHandler);
        clientBuilder.setServiceUnavailableRetryStrategy(request.serviceUnavailableRetryStrategy);
        clientBuilder.setDefaultHeaders(request.headerList);

        RequestConfig.Builder configBuilder = RequestConfig.custom()
                .setRedirectsEnabled(true)
                .setMaxRedirects(5)
                .setSocketTimeout(request.timeout)
                .setConnectTimeout(request.timeout)
                .setConnectionRequestTimeout(request.timeout);

        if (request.useCookie) {
            configBuilder.setCookieSpec(CookieSpecs.DEFAULT);
        } else {
            configBuilder.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
        }

        if (request.useProxy) {
            HttpHost proxy = new HttpHost("196.16.226.153", 3199); // TODO randomize it
            configBuilder.setProxy(proxy);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(proxy),
                    new UsernamePasswordCredentials("ylcauuf-61szs", "vjWPUIc1Vi")
            );
            clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        if (request.userAgent != null) {
            clientBuilder.setUserAgent(request.userAgent);
        }

        clientBuilder.setDefaultRequestConfig(configBuilder.build());

        return clientBuilder.build();
    }

    public static Document getDocument(Request request) throws IOException {
        CloseableHttpClient client = buildHttpClient(request);

        HttpGet getRequest = new HttpGet(request.url);

        CloseableHttpResponse response = client.execute(getRequest);

        InputStream contentStream = response.getEntity().getContent();

        return Jsoup.parse(contentStream, request.charset, request.url);
    }

    public static LinkedTreeMap<String, Object> getJson(Request request) throws IOException {
        CloseableHttpClient client = buildHttpClient(request);

        HttpGet getRequest = new HttpGet(request.url);

        CloseableHttpResponse response = client.execute(getRequest);

        InputStream contentStream = response.getEntity().getContent();

        JsonReader reader = new JsonReader(new InputStreamReader(contentStream, request.charset));

        return Helper.gson.fromJson(reader, LinkedTreeMap.class);
    }
}