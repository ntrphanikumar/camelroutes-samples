package learn.camel.sample.routes;

import java.util.Date;

import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;

import learn.camel.sample.component.CachePolicy;
import learn.camel.sample.component.CustomRouteBuilder;

public class CacheRouteSample extends CustomRouteBuilder {

    @Override
    public void configure() throws Exception {
        CachePolicy testCachePolicy = CachePolicy.builder().cacheBody(true).withBodyInKey(true).build();

        from("direct:cacheRetriever")
            .cache("direct:testTryCache", testCachePolicy)
            .log("Response recieved is")
            .log("${body}");
        
        from("direct:testTryCache")
            .log("Processing data and setting to body")
            .process(exchange -> exchange.getIn().setBody("Some data at: "+ new Date()));
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new CacheRouteSample());
        main.start();
        main.getCamelTemplate().send("direct:cacheRetriever", new DefaultExchange(main.getOrCreateCamelContext()));
        System.out.println("calling again");
        main.getCamelTemplate().send("direct:cacheRetriever", new DefaultExchange(main.getOrCreateCamelContext()));
    }
}
