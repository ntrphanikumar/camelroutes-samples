package learn.camel.sample.component;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RoutesDefinition;

public abstract class CustomRouteBuilder extends RouteBuilder {

    public class CustomRoutesDefinition extends RoutesDefinition {

        @Override
        public CustomRouteDefinition from(String uri) {
            return (CustomRouteDefinition) super.from(uri);
        }

        @Override
        protected CustomRouteDefinition createRoute() {
            return new CustomRouteDefinition();
        }
    }

    public CustomRouteBuilder() {
        super();
        setRouteCollection(new CustomRoutesDefinition());
    }

    public CustomRouteBuilder(CamelContext context) {
        super(context);
        setRouteCollection(new CustomRoutesDefinition());
    }

    @Override
    public CustomRouteDefinition from(String uri) {
        return (CustomRouteDefinition) super.from(uri);
    }
}
