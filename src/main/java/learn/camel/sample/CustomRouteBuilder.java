package learn.camel.sample;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;

public abstract class CustomRouteBuilder extends RouteBuilder {

    public class CustomRoutesDefinition extends RoutesDefinition {
        public CustomRouteDefinition from(String uri, CachePolicy cachePolicy) {
            CustomRouteDefinition route = new CustomRouteDefinition();
            route.from(uri, cachePolicy);
            return (CustomRouteDefinition) route(route);
        }
    }

    private CustomRoutesDefinition routeCollection = new CustomRoutesDefinition();

    public CustomRouteBuilder() {
        super();
        setRouteCollection(routeCollection);
    }

    public CustomRouteDefinition from(String uri, CachePolicy cachePolicy) {
        routeCollection.setCamelContext(getContext());
        CustomRouteDefinition answer = routeCollection.from(uri, cachePolicy);
        configureRoute(answer);
        return answer;
    }
    
    @Override
    protected void checkInitialized() throws Exception {
        super.checkInitialized();
        Set<RouteDefinition> cacheSourceRoutes = new HashSet<>();
        for(RouteDefinition route: routeCollection.getRoutes()) {
            if (route instanceof CustomRouteDefinition) {
                CustomRouteDefinition customRouteDefinition = (CustomRouteDefinition) route;
                if (customRouteDefinition.getCachePolicy() != null) {
                    RouteDefinition cacheSourceRoute = customRouteDefinition.buildCacheSourceRoute();
                    cacheSourceRoute.markPrepared();
                    cacheSourceRoutes.add(cacheSourceRoute);
                    customRouteDefinition.makeRouteCacheSourceChoice();
                }
            }
        }
        routeCollection.getRoutes().addAll(cacheSourceRoutes);
    }
}
