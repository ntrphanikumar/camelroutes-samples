package learn.quixey.camelroute.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class HttpDirectRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer://something?repeatCount=1")
        .to("http://www.cricbuzz.com/").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String body = exchange.getIn().getBody(String.class);
                exchange.getIn().setBody(body.substring(0, 2000));
            }
        }).to("direct:dumptofile");
        
        from("timer://something?repeatCount=1")
        .to("http://www.google.com/").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String body = exchange.getIn().getBody(String.class);
                exchange.getOut().setBody(body.substring(0, 2000));
            }
        }).to("direct:dumptofile");
        
        from("direct:dumptofile")
            .log("Looks like working")
            .to("file:/Users/pkumar/logs/cricdump.txt");
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new HttpDirectRoute());
        main.run();
    }
}
