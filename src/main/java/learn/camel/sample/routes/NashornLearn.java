package learn.camel.sample.routes;

import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;

public class NashornLearn extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:nashornExecute")
            .process(exchange -> {exchange.getIn().setHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRsdi1kZXZuZXQtb25saW5lc3lzdGVtcy1zdGFnZUBxdWl4ZXkuY29tIn0.J7t004tIUZaU7VrTWPCL-Ly-G5l4P30VHy1WPrHedDA");})
            .to("http://app-portal.stage.quixey.com/dvcat/transform")
            .process(exchange -> {
                URL jvnNpmJs = Thread.currentThread().getContextClassLoader().getResource("jvm-npm.js");
                String updatedBody = exchange.getIn().getBody(String.class).replaceAll("jvm-npm.js", jvnNpmJs.toString());
                exchange.getIn().setBody(updatedBody);
            })
            .process(exchange -> {
                ScriptEngineManager scriptEngineManager = new ScriptEngineManager(); 
                ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
                nashorn.eval(exchange.getIn().getBody(String.class));
            });
    }


    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new NashornLearn());
        main.start();
        main.getCamelTemplate().send("direct:nashornExecute", new DefaultExchange(main.getOrCreateCamelContext()));
    }
}
