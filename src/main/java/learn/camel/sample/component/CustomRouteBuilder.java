package learn.camel.sample.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RoutesDefinition;

public abstract class CustomRouteBuilder extends RouteBuilder {

    public class CustomRoutesDefinition extends RoutesDefinition {

        public CustomRouteDefinition from(String uri, CachePolicy cachePolicy) {
            CustomRouteDefinition route = createRoute();
            route.from(uri, cachePolicy);
            return (CustomRouteDefinition) route(route);
        }

        @Override
        protected CustomRouteDefinition createRoute() {
            return new CustomRouteDefinition();
        }
    }

    private CustomRoutesDefinition routeCollection = new CustomRoutesDefinition();

    public CustomRouteBuilder() {
        super();
        setRouteCollection(routeCollection);
    }

    public CustomRouteDefinition from(String uri) {
        return (CustomRouteDefinition) super.from(uri);
    }

    public CustomRouteDefinition from(String uri, CachePolicy cachePolicy) {
        routeCollection.setCamelContext(getContext());
        CustomRouteDefinition answer = routeCollection.from(uri, cachePolicy);
        configureRoute(answer);
        return answer;
    }
}
