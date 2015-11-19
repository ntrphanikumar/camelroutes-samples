package learn.camel.sample.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Predicate;
import org.apache.camel.model.RouteDefinition;

public class CustomRouteDefinition extends RouteDefinition {
    
    private Map<ExchangeCacheEntity, ExchangeCacheEntity> orchestrationCache = new HashMap<>();

    @Override
    public CustomRouteDefinition from(String uri) {
        return (CustomRouteDefinition) super.from(uri);
    }

    public CustomRouteDefinition cache(String uri, CachePolicy cachePolicy) {
        this.choice()
            .when(isInCache(cachePolicy))
                .log("Respond from cache...")
            .otherwise()
                .log("Not found in cache.. allowing normal process to happen")
                .to(uri)
                .log("save response in cache")
            .endChoice();
        return this;
    }

    private Predicate isInCache(CachePolicy cachePolicy) {
        return exchange -> {
            return true;
        };
    }
}
