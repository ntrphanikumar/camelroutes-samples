package learn.quixey.camelroute.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class AggregationStrategyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://startone?repeatCount=1")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader("name", "google");
                    exchange.getIn().setHeader("link", "www.google.com");
                }
            })
            .to("direct:startone");
        
        from("direct:startone")
            .to("http://${header[name]}")
            .to("file:///D:/CamelTest?fileName=${header[name]}");

    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();

        main.addRouteBuilder(new AggregationStrategyRoute());
        main.run();
    }
}
