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
    
    private static final Map<String, CachePolicy> URI_CACHE_POLICY = new HashMap<>();

    private Map<String, Cache> cacheByUri = new HashMap<String, Cache>() {
        private static final long serialVersionUID = -1607353690959834086L;
        @Override
        public Cache get(Object uri) {
            if(!containsKey(uri)) {
                put(uri.toString(), new Cache(uri.toString()));
            }
            return super.get(uri);
        }
    };
    
    class Cache extends HashMap<CacheEntity, CacheEntity> {
        private static final long serialVersionUID = -5855026353296951671L;

        private final String uri;

        public Cache(String uri) {
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }
    }
    
    @Override
    public CustomRouteDefinition from(String uri) {
        return (CustomRouteDefinition) super.from(uri);
    }

    public CustomRouteDefinition from(String uri, CachePolicy cachePolicy) {
        URI_CACHE_POLICY.put(uri, cachePolicy);
        return this.from(uri);
    }
    
    @Override
    public RouteDefinition to(String uri) {
        this.choice()
                .when(notCachable(uri))
                    .to(uri)
                .when(isUpdatedFromCache(uri))
                    .log("Serving from cache for: " + uri)
                .otherwise()
                    .log("Not found in cache. Processing for: " + uri)
                    .to(uri).process(updateCache(uri))
            .end()
            .process(exchange -> exchange.removeProperty(cacheKeyProperty(uri)));
        return this;
    }
    
    private Predicate notCachable(String uri) {
        return exchange -> !URI_CACHE_POLICY.containsKey(uri);
    }
    
    private Processor updateCache(String uri) {
        return exchange -> {
            CachePolicy cachePolicy = URI_CACHE_POLICY.get(uri);
            CacheEntity key = getCacheKey(exchange, uri);
            cacheByUri.get(uri).put(key, buildExchangeCacheEntity(exchange,
                    cachePolicy.isCacheBody(), cachePolicy.getHeadersToCache(), cachePolicy.getPropertiesToCache()));
            scheduleCacheCleanup(cachePolicy, key, cacheByUri.get(uri));
        };
    }

    private CacheEntity getCacheKey(Exchange exchange, String uri) {
        return exchange.getProperty(cacheKeyProperty(uri), CacheEntity.class);
    }
    
    private String cacheKeyProperty(String uri) {
        return "KEY#" + uri;
    }

    private void scheduleCacheCleanup(CachePolicy cachePolicy, CacheEntity key, Cache cache) {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                cache.remove(key);
            }
        }, cachePolicy.getTimeToLive() * 1000);
    }

    private Predicate isUpdatedFromCache(String uri) {
        return exchange -> {
            CachePolicy cachePolicy = URI_CACHE_POLICY.get(uri);
            CacheEntity key = buildExchangeCacheEntity(exchange, cachePolicy.isBodyInKey(),
                    cachePolicy.getHeadersInKey(), cachePolicy.getPropertiesInKey());
            CacheEntity entity = cacheByUri.get(uri).get(key);
            if (entity == null) {
                exchange.setProperty(cacheKeyProperty(uri), key);
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

    private CacheEntity buildExchangeCacheEntity(Exchange exchange, boolean body, Set<String> headers, Set<String> properties) {
        CacheEntity cacheEntity = new CacheEntity();
        if (body && exchange.getIn().getBody() != null) {
            // Just a temporary way to convert to bytes.. actually should read from input steam
            cacheEntity.setBody(exchange.getIn().getBody(String.class).getBytes());
        }
        for (String header : headers) {
            cacheEntity.getHeaders().put(header, exchange.getIn().getHeader(header));
        }
        for (String property : properties) {
            cacheEntity.getProperties().put(property, exchange.getProperty(property));
        }
        return cacheEntity;
    }
}
