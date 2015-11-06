package learn.quixey.camelroute.component;

import org.apache.camel.Exchange;

public class SayService {
    public void process(Exchange exchange) {
        System.out.println("Why do you think.. I am called for.. ");
    }

    public void processNew(Exchange exchange) {
        exchange.getIn().setBody("Are you here by chance dude?");
    }
}
