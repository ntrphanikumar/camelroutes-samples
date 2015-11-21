package learn.camel.sample.routes;

import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;

import learn.camel.sample.CachePolicy;
import learn.camel.sample.CustomCamelContext;
import learn.camel.sample.CustomRouteBuilder;

public class CacheRouteSample extends CustomRouteBuilder {

    private static final String TEST_CACHE_DIRECT = "direct:testCache";
    private static final String CACHE_RETRIEVER_DIRECT = "direct:cacheRetriever";

    @Override
    public void configure() throws Exception {
        CachePolicy cachePolicy = CachePolicy.newPolicy().cacheBody(true).withBodyInKey(true).liveFor(3).build();

        from(CACHE_RETRIEVER_DIRECT)
            .to(TEST_CACHE_DIRECT)
            .log("Response recieved is")
            .log("${body}");
        
        from(TEST_CACHE_DIRECT, cachePolicy)
            .process(exchange -> exchange.getIn().setBody("Some data at: " + new Date()));
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main() {
            @Override
            protected CamelContext createContext() {
                return new CustomCamelContext();
            }
        };
        main.enableHangupSupport();
        main.addRouteBuilder(new CacheRouteSample());
        main.start();

        main.getCamelTemplate().send(CACHE_RETRIEVER_DIRECT, new DefaultExchange(main.getOrCreateCamelContext()));
        Thread.sleep(1000);
        main.getCamelTemplate().send(CACHE_RETRIEVER_DIRECT, new DefaultExchange(main.getOrCreateCamelContext()));
        Thread.sleep(2000);
        main.getCamelTemplate().send(CACHE_RETRIEVER_DIRECT, new DefaultExchange(main.getOrCreateCamelContext()));
    }
}
