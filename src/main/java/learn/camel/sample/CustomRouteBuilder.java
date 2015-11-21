package learn.camel.sample;

import org.apache.camel.builder.RouteBuilder;
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
}
