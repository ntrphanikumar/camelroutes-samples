package learn.camel.sample;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

public class CustomCamelContext extends DefaultCamelContext {
    
    @Override
    public void startRoute(RouteDefinition route) throws Exception {
        if (route instanceof CustomRouteDefinition) {
            CustomRouteDefinition customRouteDefinition = (CustomRouteDefinition) route;
            if (customRouteDefinition.getCachePolicy() != null) {
                super.startRoute(customRouteDefinition.buildCacheSourceRoute());
            }
        }
        super.startRoute(route);
    }
}
