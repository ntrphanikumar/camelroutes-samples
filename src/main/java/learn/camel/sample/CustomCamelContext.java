package learn.camel.sample;

import java.util.UUID;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

public class CustomCamelContext extends DefaultCamelContext {
    
    @Override
    public void startRoute(RouteDefinition route) throws Exception {
        if (route instanceof CustomRouteDefinition) {
            CustomRouteDefinition customRouteDefinition = (CustomRouteDefinition) route;
            if (customRouteDefinition.getCachePolicy() != null) {
                String cacheSourceDirect = "direct:cachesource-" + UUID.randomUUID();
                super.startRoute(customRouteDefinition.buildCacheSourceRoute(cacheSourceDirect));
                customRouteDefinition.makeRouteCacheSourceChoice(cacheSourceDirect);
            }
        }
        super.startRoute(route);
    }
}
