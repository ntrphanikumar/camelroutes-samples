package learn.quixey.camelroute.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class BeanRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // JndiContext context = new JndiContext();
        // context.bind("bye", new SayService("Good Bye!"));
        // DefaultCamelContext camelContext = (DefaultCamelContext) getContext();
        // camelContext.setJndiContext(context);

        // from("timer://byebean?repeatCount=1").bean(SayService.class);

        from("timer://byebean?repeatCount=1").to("class:learn.quixey.camelroute.component.SayService?method=process").log("${body}");
                // .to("bean:bye")
                // .to("bean:bye?method=message")
                // .beanRef("bye", "message")
                // .beanRef("bye")
                // .bean(new SayService("byeee!!!"))
                // .to("log:some");
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();

        main.addRouteBuilder(new BeanRoute());
        main.run();
    }

}
