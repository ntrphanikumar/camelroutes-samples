package learn.camel.sample.routes;

import java.util.Date;

import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;

import learn.camel.sample.component.CachePolicy;
import learn.camel.sample.component.CustomRouteBuilder;

public class CacheRouteSample extends CustomRouteBuilder {

    private static final String CACHE_RETRIEVER_DIRECT = "direct:cacheRetriever";

    @Override
    public void configure() throws Exception {
        CachePolicy cachePolicy = CachePolicy.newPolicy().cacheBody(true).withBodyInKey(true).liveFor(3).build();

        from(CACHE_RETRIEVER_DIRECT)
            .to("direct:testTryCache")
            .log("Response recieved is")
            .log("${body}");
        
        from("direct:testTryCache", cachePolicy)
            .process(exchange -> exchange.getIn().setBody("Some data at: " + new Date()));
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new CacheRouteSample());
        main.start();

        main.getCamelTemplate().send(CACHE_RETRIEVER_DIRECT, new DefaultExchange(main.getOrCreateCamelContext()));
        Thread.sleep(1000);
        main.getCamelTemplate().send(CACHE_RETRIEVER_DIRECT, new DefaultExchange(main.getOrCreateCamelContext()));
        Thread.sleep(3000);
        main.getCamelTemplate().send(CACHE_RETRIEVER_DIRECT, new DefaultExchange(main.getOrCreateCamelContext()));
    }
}
