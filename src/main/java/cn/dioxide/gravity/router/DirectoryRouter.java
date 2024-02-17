package cn.dioxide.gravity.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

/**
 * @author Dioxide.CN
 * @date 2023/5/28
 * @since 1.0
 */
@Component
@AllArgsConstructor
public class DirectoryRouter {

    private static final String KEY = "directory";

    @Bean
    RouterFunction<ServerResponse> directoryRouter() {
        return route(GET("/directory"),
                request -> ServerResponse.ok().render(KEY, Map.of())
        );
    }

}
