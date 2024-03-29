package cn.dioxide.gravity.infra;

import cn.dioxide.gravity.GravityStarter;
import cn.dioxide.gravity.entity.CityInfo;
import cn.dioxide.gravity.entity.WeatherResponse;
import com.maxmind.geoip2.DatabaseReader;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.annotation.Resource;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Dioxide.CN
 * @date 2023/7/28
 * @since 1.0
 */
@Slf4j
@Component
public class GeoLiteReader {

    @Resource
    private GravityStarter PLUGIN;

    public Mono<CityInfo> getCityCode(String ip, final String key) {
        File database = new File(PLUGIN.getConfigContext().getCONFIG_HOME() + File.separator + "GeoLite2-City.mmdb");
        try {
            DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
            return Mono.fromCallable(() -> InetAddress.getByName(ip))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(ipAddress -> Mono.fromCallable(() -> dbReader.city(ipAddress)))
                    .flatMap(response -> getCityIdFromLatLon(
                            response.getLocation().getLatitude().toString(),
                            response.getLocation().getLongitude().toString(),
                            key
                    ))
                    .doOnTerminate(() -> safeClose(dbReader));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    /**
     * 依据传入的IP经纬度请求和风天气API获取城市ID
     * @param latitude 纬度
     * @param longitude 经度
     * @return String类型的响应式体
     */
    private Mono<CityInfo> getCityIdFromLatLon(String latitude, String longitude, final String key) {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(String.format("https://geoapi.qweather.com/v2/city/lookup?location=%s&key=%s",
                URLEncoder.encode(longitude + "," + latitude, StandardCharsets.UTF_8),
                URLEncoder.encode(key, StandardCharsets.UTF_8)));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .GET()
                .build();
        // 让整个请求过程发生在响应式链路中避免阻塞
        return GzipResponse.handle(client, request)
                .flatMap(this::parseCityId);
    }

    /**
     * Gson处理异步过来的HttpResponse
     * @param responseBody 经过GZIP解压缩的JSON格式的HttpResponse字符串
     * @return 返回一个从HttpResponse中获取城市ID的响应式体
     */
    private Mono<CityInfo> parseCityId(String responseBody) {
        return Mono.fromCallable(() -> {
            Gson gson = new Gson();
            WeatherResponse response = gson.fromJson(responseBody, WeatherResponse.class);
            WeatherResponse.Location location = response.getLocation().get(0);
            return new CityInfo(location.getId(), location.getName());
        });
    }

    private void safeClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
