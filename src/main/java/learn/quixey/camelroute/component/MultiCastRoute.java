package learn.quixey.camelroute.component;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

public class MultiCastRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
//        from("timer://somestuff?repeatCount=1")
//            .multicast()
//            .parallelProcessing()
//            .aggregationStrategy(new AggregationStrategy() {
//                public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//                    String oldBody = "", newBody="";
//                    if(oldExchange!= null && oldExchange.getIn() != null && oldExchange.getIn().getBody()!=null) {
//                        oldBody = oldExchange.getIn().getBody(String.class);
//                    }
//                    if(newExchange!= null && newExchange.getIn() != null && newExchange.getIn().getBody()!=null) {
//                        newBody = newExchange.getIn().getBody(String.class);
//                    }
//                    Exchange targetExchange = newExchange == null? oldExchange: newExchange;
//                    targetExchange.getIn().setBody(oldBody + newBody, String.class);
//                    return targetExchange;
//                }
//            })
//            .to("http://www.google.com")
//            .to("http://www.cricbuzz.com")
//            .end()
//            .log("${body}");
        
        
        
        from("timer://somestuff?repeatCount=1")
        .multicast()
        .parallelProcessing()
        .aggregationStrategy(new GroupedExchangeAggregationStrategy())
        .to("http://www.google.com")
        .to("http://www.cricbuzz.com")
        .end()
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String body = "";
                List<Exchange> exchanges = exchange.getIn().getBody(List.class);
                for(Exchange inExchange:exchanges) {
                    body+=inExchange.getIn().getBody(String.class);
                }
                exchange.getIn().setBody(body, String.class);
            }
        })
        .log("${body}");
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();

        main.addRouteBuilder(new MultiCastRoute());
        main.run();
    }
}
