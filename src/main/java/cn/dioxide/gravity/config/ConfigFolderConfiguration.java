package cn.dioxide.gravity.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import cn.dioxide.gravity.util.FutureDownloader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import run.halo.app.plugin.BasePlugin;

/**
 * @author Dioxide.CN
 * @date 2023/7/21
 * @since 1.0
 */
@Slf4j
@Configuration
public class ConfigFolderConfiguration {

    private Class<? extends BasePlugin> clazz;
    @Getter private String CONFIG_HOME;

    private static final String FILE_URL = "https://cdn.jsdelivr.net/npm/geolite2-city@1.0.0/GeoLite2-City.mmdb.gz";

    public void init(Class<? extends BasePlugin> clazz) {
        this.clazz = clazz;
        homeDirBuilder();
        buildGraphQLHome();
        FutureDownloader.call(CONFIG_HOME, FILE_URL);
    }

    private void homeDirBuilder() {
        URL url = this.clazz.getProtectionDomain().getCodeSource().getLocation();
        String filePath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
        this.CONFIG_HOME = new File(filePath).getParent() + File.separator + "gravity-power";
        log.info("detected config directory " + this.CONFIG_HOME);

        Path path = Paths.get(this.CONFIG_HOME);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    private void buildGraphQLHome() {
        Path path = Paths.get(this.CONFIG_HOME + File.separator + "graphql");
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

}
