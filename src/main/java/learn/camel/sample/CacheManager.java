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

        public void put(CacheEntity key, CacheEntity value, int timeToLive) {
            put(key, value);
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    remove(key);
                }
            }, timeToLive * 1000);
        }
    }
    
    private CacheManager() {
        
    }
    
    public static CacheManager instance() {
        return INSTANCE;
    }

    public CacheEntity get(String cacheName, CacheEntity cacheKey) {
        return cacheByName.get(cacheName).get(cacheKey);
    }
    
    public Cache get(String cacheName) {
        return cacheByName.get(cacheName);
    }
}
