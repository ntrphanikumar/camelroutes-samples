package learn.quixey.camelroute.component;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.codehaus.jackson.map.ObjectMapper;

public class ChoiceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://weather?repeatCount=1")
            .to("http://api.openweathermap.org/data/2.5/weather?q=Hyderabad,In&units=metric")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                        String weather = exchange.getIn().getBody(String.class);
                        ObjectMapper mapper = new ObjectMapper();
                        Map readValue = mapper.readValue(weather, Map.class);
                        exchange.getIn().setHeader("temp", ((Map) readValue.get("main")).get("temp"));
                }
            })
            .transform(simple("${header[temp]}"))
            .choice()
                .when(header("temp").isGreaterThan(25))
                    .log("Getting hotter")
                .when(header("temp").isGreaterThan(10))
                    .log("Good weather")
                .otherwise()
                    .log("Coooolllllll");
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();

        main.addRouteBuilder(new ChoiceRoute());
        main.run();
    }

}
