package cn.zachqie.wrench.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName HttpUtils
 * @Author qiezhichao
 * @Date 2025/5/30 13:13
 */
@Slf4j
public class HttpUtils {

    /** SSL 信任所有证书开关，默认 false（严格校验证书与主机名），对接自签名内部服务时可显式打开 */
    private static final boolean SSL_TRUST_ALL = Boolean.parseBoolean(
            System.getProperty("zachqie.wrench.http.ssl.trust-all", "false"));

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(5000, TimeUnit.MILLISECONDS)
            .setResponseTimeout(5000, TimeUnit.MILLISECONDS)
            .build();

    /** http 客户端，静态单例，JVM 生命周期内复用，无需按请求关闭 */
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
            .setConnectionManager(buildPool(false))
            .setDefaultRequestConfig(requestConfig)
            .build();

    /** https 客户端，静态单例，默认严格 SSL 校验 */
    private static final CloseableHttpClient HTTPS_CLIENT = HttpClients.custom()
            .setConnectionManager(buildPool(true))
            .setDefaultRequestConfig(requestConfig)
            .build();

    private static PoolingHttpClientConnectionManager buildPool(boolean ssl) {
        PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(100)    // 设置最大连接数
                .setMaxConnPerRoute(20); // 设置每个路由的最大连接数
        if (ssl && SSL_TRUST_ALL) {
            builder.setSSLSocketFactory(createSSLConnSocketFactory());
        }
        return builder.build();
    }

    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustAllStrategy()).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (GeneralSecurityException e) {
            log.error("createSSLConnSocketFactory error, {}", e.getMessage(), e);
        }
        return sslsf;
    }

    private static CloseableHttpClient getHttpClient(String apiUrl) {
        return apiUrl.startsWith("https") ? HTTPS_CLIENT : HTTP_CLIENT;
    }

    private static String buildGetMethodURL(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder param = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (param.length() == 0) {
                    param.append("?");
                } else {
                    param.append("&");
                }
                param.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.name()));
            }
        } catch (UnsupportedEncodingException e) {
            // UTF-8 必然可用，理论不可达
            throw new RuntimeException(e);
        }
        return url + param;
    }

    private static HttpPost buildHttpPost(String apiUrl, Map<String, String> header) {
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(requestConfig);
        if (header != null) {
            for (String key : header.keySet()) {
                httpPost.setHeader(key, header.get(key));
            }
        }
        return httpPost;
    }

    /*****************************************************************************************************/
    public static JSONObject doGet(String url, Map<String, Object> params) {
        log.info("doGet begin, url={}, params={}", url, params);
        String apiUrl = buildGetMethodURL(url, params);
        log.info("apiUrl={}", apiUrl);
        CloseableHttpClient httpClient = getHttpClient(apiUrl);
        HttpGet httpGet = new HttpGet(apiUrl);

        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet);) {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
            log.info("doGet end, result={}", result);
            return JSON.parseObject(result);
        } catch (IOException e) {
            log.error("doGet Map error, {}", e.getMessage(), e);
        }
        return null;
    }

    public static JSONObject doPost(String apiUrl, Map<String, Object> params, Map<String, String> header) {
        log.info("doPost begin, apiUrl={}, params={}, header={}", apiUrl, params, header);
        CloseableHttpClient httpClient = getHttpClient(apiUrl);
        HttpPost httpPost = buildHttpPost(apiUrl, header);
        // 设置请求头
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_JSON));

        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("doPost end, result={}", result);
            return JSON.parseObject(result);
        } catch (Exception e) {
            log.error("doPost error, {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * @desc 执行sse请求，这里使用 IDoExecute4Sse 接口，是由于需要从SSE请求获取 InputStream
     *       但又不能直接返回 InputStream，因为我们需要关闭 CloseableHttpResponse，
     *       如果直接返回 InputStream，一旦 CloseableHttpResponse 关闭了
     *       返回的 InputStream 就无法读取数据，会抛出异常。
     *       因此采用函数式编程，将要处理的业务逻辑函数当成参数传入。
     * @author qiezhichao
     * @date 2025/6/6 17:39
     * @param
     * @return
     */
    public static void doGet4Sse(String url, Map<String, Object> params, Executor helper) {
        log.info("doGet4Sse begin, url={}, params={}", url, params);
        String apiUrl = buildGetMethodURL(url, params);
        log.info("apiUrl={}", apiUrl);
        CloseableHttpClient httpClient = getHttpClient(apiUrl);
        HttpGet httpGet = new HttpGet(apiUrl);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (Objects.isNull(entity)) {
                log.warn("remote call entity is null, doGet4Sse end...");
                return;
            }

            log.info("doGet4Sse end...");
            InputStream inputStream = entity.getContent();
            // 执行业务操作
            helper.doExecuteAfterSSE(inputStream);
        } catch (Exception e) {
            log.error("doGet4Sse error, {}", e.getMessage(), e);
        } finally {
            if (Objects.nonNull(httpResponse)) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("close HttpResponse error, {}", e.getMessage(), e);
                }
            }
        }
    }

    public static JSONObject doGet(String url) {
        return doGet(url, new HashMap<>());
    }

    public static JSONObject doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<>(), null);
    }

    public static JSONObject doPost(String apiUrl, Map<String, Object> params) {
        return doPost(apiUrl, params, null);
    }

    /*****************************************************************************************/
    public interface Executor {
        public void doExecuteAfterSSE(InputStream inputStream) throws Exception;
    }

}
