package learn.quixey.camelroute.component;

import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.WaitForTaskToComplete;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class SedaRoute extends RouteBuilder {
    private static final String SEDA_WAIT_TEST = "seda:waittest";

    @Override
    public void configure() throws Exception {
        int timeout = 3000, sleepTime = 5000;
        
        from("timer://sedatester?repeatCount=1")
            .onException(ExchangeTimedOutException.class).process(exchange -> {log.error("Seda I am upset with your response time !!!! pchhh");}).handled(true).end()
            .to(SEDA_WAIT_TEST + "?timeout=" + timeout + "&waitForTaskToComplete=" + WaitForTaskToComplete.Always)
            .process(exchange -> {
                log.info("Hurray!!!! Seda responded... Let me see what he has got for me ..");
                log.info(exchange.getIn().getBody(String.class));
            });
        
         from(SEDA_WAIT_TEST)
             .process(exchange -> sleepFor(sleepTime))
             .setBody(constant("Hmmm waited for a long time through !!!"));
    }
    
    private void sleepFor(long timeInMillis) throws InterruptedException {
        log.info("Feeling sleepy... Let me sleep before going back for millis: " + timeInMillis);
        Thread.sleep(timeInMillis);
        log.info("Hawwww... I am back.. Hope still seda is waiting for me");
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new SedaRoute());
        main.run();
    }
}
