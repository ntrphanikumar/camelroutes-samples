package learn.camel.sample;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;

public abstract class CustomRouteBuilder extends RouteBuilder {

    public class CustomRoutesDefinition extends RoutesDefinition {
        public RouteDefinition from(String uri, CachePolicy cachePolicy) {
            return route(new CachableRouteDefinition().from(uri, cachePolicy));
        }
    }

    private CustomRoutesDefinition routeCollection = new CustomRoutesDefinition();

    public CustomRouteBuilder() {
        super();
        setRouteCollection(routeCollection);
    }

    public RouteDefinition from(String uri, CachePolicy cachePolicy) {
        routeCollection.setCamelContext(getContext());
        RouteDefinition answer = routeCollection.from(uri, cachePolicy);
        configureRoute(answer);
        return answer;
    }
    
    @Override
    protected void checkInitialized() throws Exception {
        super.checkInitialized();
        Set<RouteDefinition> cacheSourceRoutes = new HashSet<>();
        for(RouteDefinition route: routeCollection.getRoutes()) {
            if (route instanceof CachableRouteDefinition) {
                CachableRouteDefinition customRouteDefinition = (CachableRouteDefinition) route;
                if (customRouteDefinition.getCachePolicy() != null) {
                    cacheSourceRoutes.add(customRouteDefinition.makeRouteCacheCapable());
                }
            }
        }
        routeCollection.getRoutes().addAll(cacheSourceRoutes);
    }
}
