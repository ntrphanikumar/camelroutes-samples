package learn.camel.sample;

import java.util.HashSet;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CachePolicy {
    private boolean cacheBody = true, bodyInKey = true;
    private Set<String> headersToCache = new HashSet<>(), headersInKey = new HashSet<>(),
            propertiesToCache = new HashSet<>(), propertiesInKey = new HashSet<>();
    private int timeToLive = 300;

    public boolean isBodyInKey() {
        return bodyInKey;
    }
    public boolean isCacheBody() {
        return cacheBody;
    }
    
    public Set<String> getHeadersInKey() {
        return headersInKey;
    }

    public Set<String> getHeadersToCache() {
        return headersToCache;
    }

    public Set<String> getPropertiesInKey() {
        return propertiesInKey;
    }

    public Set<String> getPropertiesToCache() {
        return propertiesToCache;
    }

    public static CachePolicyBuilder newPolicy() {
        return new CachePolicyBuilder();
    }
    
    public int getTimeToLive() {
        return timeToLive;
    }

    @SuppressWarnings("unchecked")
    public static class CachePolicyBuilder {
        private final CachePolicy cachePolicy = new CachePolicy();

        public CachePolicyBuilder cacheBody(boolean cacheBody) {
            cachePolicy.cacheBody = cacheBody;
            return this;
        }

        public CachePolicyBuilder cacheHeaders(String... headers) {
            if (headers != null && headers.length > 0) {
                cachePolicy.headersToCache = new HashSet<>(Arrays.asList(headers));
            }
            return this;
        }

        public CachePolicyBuilder cacheProperties(String... properties) {
            if (properties != null && properties.length > 0) {
                cachePolicy.propertiesToCache = new HashSet<>(Arrays.asList(properties));
            }
            return this;
        }

        public CachePolicyBuilder withBodyInKey(boolean bodyInKey) {
            cachePolicy.bodyInKey = bodyInKey;
            return this;
        }

        public CachePolicyBuilder headersInKey(String... headers) {
            if (headers != null && headers.length > 0) {
                cachePolicy.headersInKey = new HashSet<>(Arrays.asList(headers));
            }
            return this;
        }

        public CachePolicyBuilder propertiesInKey(String... properties) {
            if (properties != null && properties.length > 0) {
                cachePolicy.propertiesInKey = new HashSet<>(Arrays.asList(properties));
            }
            return this;
        }

        public CachePolicyBuilder liveFor(int millis) {
            cachePolicy.timeToLive = millis;
            return this;
        }

        public CachePolicy build() {
            if (!cachePolicy.bodyInKey && cachePolicy.headersInKey.isEmpty() && cachePolicy.propertiesInKey.isEmpty()) {
                throw new IllegalArgumentException("No caching key parameters specified");
            }
            if(!cachePolicy.cacheBody && cachePolicy.headersToCache.isEmpty() && cachePolicy.propertiesToCache.isEmpty()) {
                throw new IllegalArgumentException("Nothing to cache");
            }
            return cachePolicy;
        }
    }
}
