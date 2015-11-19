package learn.camel.sample.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;

public class CustomRouteDefinition extends RouteDefinition {
    
    private static final String ROUTE_CACHE_KEY = "ROUTE_CACHE_KEY";
    private Map<ExchangeCacheEntity, ExchangeCacheEntity> orchestrationCache = new HashMap<>();

    @Override
    public CustomRouteDefinition from(String uri) {
        return (CustomRouteDefinition) super.from(uri);
    }

    public CustomRouteDefinition cache(String uri, CachePolicy cachePolicy) {
        this.process(updateWithCacheKey(cachePolicy))
            .choice()
                .when(isUpdatedFromCache(cachePolicy))
                    .log("Respond from cache...")
                .otherwise()
                    .log("Not found in cache.. allowing normal process to happen")
                    .to(uri)
                    .log("save response in cache")
                    .process(updateCache(cachePolicy))
            .endChoice();
        return this;
    }

    private Processor updateWithCacheKey(CachePolicy cachePolicy) {
        return exchange -> exchange.setProperty(ROUTE_CACHE_KEY, buildExchangeCacheEntity(exchange,
                cachePolicy.isBodyInKey(), cachePolicy.getHeadersInKey(), cachePolicy.getPropertiesInKey()));
    }

    private Processor updateCache(CachePolicy cachePolicy) {
        return exchange -> {
            ExchangeCacheEntity key = getCacheKey(exchange);
            orchestrationCache.put(key, buildExchangeCacheEntity(exchange,
                    cachePolicy.isCacheBody(), cachePolicy.getHeadersToCache(), cachePolicy.getPropertiesToCache()));
            scheduleCacheCleanup(cachePolicy, key);
        };
    }

    private void scheduleCacheCleanup(CachePolicy cachePolicy, ExchangeCacheEntity key) {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                orchestrationCache.remove(key);
            }
        }, cachePolicy.getTimeToLive() * 1000);
    }

    private ExchangeCacheEntity getCacheKey(Exchange exchange) {
        return (ExchangeCacheEntity) exchange.getProperty(ROUTE_CACHE_KEY);
    }

    private Predicate isUpdatedFromCache(CachePolicy cachePolicy) {
        return exchange -> {
            ExchangeCacheEntity entity = orchestrationCache.get(getCacheKey(exchange));
            if (entity == null) {
                return false;
            }
            if (cachePolicy.isCacheBody()) {
                exchange.getIn().setBody(entity.getBody());
            }
            for (String header : cachePolicy.getHeadersToCache()) {
                exchange.getIn().setHeader(header, entity.getHeaders().get(header));
            }
            for (String property : cachePolicy.getPropertiesToCache()) {
                exchange.setProperty(property, entity.getProperties().get(property));
            }
            return true;
        };
    }
    
    private ExchangeCacheEntity buildExchangeCacheEntity(Exchange exchange, boolean body, Set<String> headers, Set<String> properties) {
        ExchangeCacheEntity exchangeCacheEntity = new ExchangeCacheEntity();
        if (body && exchange.getIn().getBody() != null) {
            exchangeCacheEntity.setBody(exchange.getIn().getBody(String.class).getBytes());
        }
        for (String header : headers) {
            exchangeCacheEntity.getHeaders().put(header, exchange.getIn().getHeader(header));
        }
        for (String property : properties) {
            exchangeCacheEntity.getProperties().put(property, exchange.getProperty(property));
        }
        return exchangeCacheEntity;
    }
}
