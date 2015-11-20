package learn.camel.sample.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;

public class CustomRouteDefinition extends RouteDefinition {
    
    private static final String ROUTE_CACHE = "ROUTE_CACHE";
    private static final String ROUTE_CACHE_KEY = "ROUTE_CACHE_KEY";

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
        this.process(pushCacheAndCacheKey(uri))
            .choice()
                .when(isUpdatedFromCache(uri))
                .log("Serving from cache for: " + uri)
                    .process(popCacheAndCacheKey(uri))
                .otherwise()
                    .log("Not found in cache. Processing for: " + uri)
                    .doTry().to(uri).process(updateCache(uri))
                    .doFinally().process(popCacheAndCacheKey(uri))
                    .endDoTry()
            .end();
        return this;
    }
    
    private boolean isCachable(String uri) {
        return URI_CACHE_POLICY.containsKey(uri);
    }

    private Processor popCacheAndCacheKey(String uri) {
        return exchange -> {
            if(!isCachable(uri)) {
                return;
            }
            Stack<Cache> cacheStack = (Stack<Cache>) exchange.getProperty(ROUTE_CACHE);
            if (cacheStack != null && !cacheStack.isEmpty() && cacheStack.peek().getUri().equalsIgnoreCase(uri)) {
                Cache cache = cacheStack.pop();
                Stack<CacheEntity> keyStack = (Stack<CacheEntity>)exchange.getProperty(ROUTE_CACHE_KEY);
                if (keyStack != null && !keyStack.isEmpty() && cache.containsKey(keyStack.peek())) {
                    keyStack.pop();
                }
            }
        };
    }

    private Processor pushCacheAndCacheKey(String uri) {
        return exchange -> {
            if(!isCachable(uri)) {
                return;
            }
            Stack<Cache> cacheStack = (Stack<Cache>) exchange.getProperty(ROUTE_CACHE);
            if (cacheStack == null) {
                cacheStack = new Stack<>();
                exchange.setProperty(ROUTE_CACHE, cacheStack);
            }
            cacheStack.push(cacheByUri.get(uri));

            Stack<CacheEntity> keyStack = (Stack<CacheEntity>)exchange.getProperty(ROUTE_CACHE_KEY);
            if (keyStack == null) {
                keyStack = new Stack<>();
                exchange.setProperty(ROUTE_CACHE_KEY, keyStack);
            }
            CachePolicy cachePolicy = URI_CACHE_POLICY.get(uri);
            keyStack.push(buildExchangeCacheEntity(exchange, cachePolicy.isBodyInKey(),
                    cachePolicy.getHeadersInKey(), cachePolicy.getPropertiesInKey()));
        };
    }

    private Processor updateCache(String uri) {
        return exchange -> {
            if(!isCachable(uri)) {
                return;
            }
            CachePolicy cachePolicy = URI_CACHE_POLICY.get(uri);
            CacheEntity key = getCacheKey(exchange);
            cache(exchange).put(key, buildExchangeCacheEntity(exchange,
                    cachePolicy.isCacheBody(), cachePolicy.getHeadersToCache(), cachePolicy.getPropertiesToCache()));
            scheduleCacheCleanup(cachePolicy, key, cache(exchange));
        };
    }

    private void scheduleCacheCleanup(CachePolicy cachePolicy, CacheEntity key, Cache cache) {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                cache.remove(key);
            }
        }, cachePolicy.getTimeToLive() * 1000);
    }

    private CacheEntity getCacheKey(Exchange exchange) {
        return ((Stack<CacheEntity>) exchange.getProperty(ROUTE_CACHE_KEY)).peek();
    }

    private Cache cache(Exchange exchange) {
        return ((Stack<Cache>) exchange.getProperty(ROUTE_CACHE)).peek();
    }

    private Predicate isUpdatedFromCache(String uri) {
        return exchange -> {
            if(!isCachable(uri)) {
                return false;
            }
            CacheEntity entity = cache(exchange).get(getCacheKey(exchange));
            if (entity == null) {
                return false;
            }
            CachePolicy cachePolicy = URI_CACHE_POLICY.get(uri);
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
