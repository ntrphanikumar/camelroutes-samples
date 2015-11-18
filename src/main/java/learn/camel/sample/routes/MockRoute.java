package learn.camel.sample.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class MockRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:start").to("direct:foo").to("log:transformed").to("mock:result");
        from("direct:foo").transform(constant("New phani mocked"));

        from("timer://prepareHttp?repeatCount=1")
            .to("http://www.google.com")
            .log("${body}")
            .to("direct:start");

    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new MockRoute());
        main.run();
    }
}
