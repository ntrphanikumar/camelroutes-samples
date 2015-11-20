package learn.camel.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CacheManager {

    private static final CacheManager INSTANCE = new CacheManager();
    
    private Map<String, Cache> cacheByName = new HashMap<String, Cache>() {
        private static final long serialVersionUID = -1607353690959834086L;
        @Override
        public Cache get(Object uri) {
            if(!containsKey(uri)) {
                put(uri.toString(), new Cache());
            }
            return super.get(uri);
        }
    };
    
    public class Cache extends HashMap<CacheEntity, CacheEntity> {
        private static final long serialVersionUID = -5855026353296951671L;
    }
    
    private CacheManager() {
        
    }
    
    public static CacheManager instance() {
        return INSTANCE;
    }

    public CacheEntity get(String cacheName, CacheEntity cacheKey) {
        return cacheByName.get(cacheName).get(cacheKey);
    }
    
    public void put(String cacheName, CacheEntity cacheKey, CacheEntity value, int timeToLive) {
        Cache cache = cacheByName.get(cacheName);
        cache.put(cacheKey, value);
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                cache.remove(cacheKey);
            }
        }, timeToLive * 1000);
    }
}
