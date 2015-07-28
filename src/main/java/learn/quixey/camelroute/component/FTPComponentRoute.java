/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package learn.quixey.camelroute.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

/**
 * Main class that can download files from an existing FTP server.
 */
public final class FTPComponentRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("ftp://192.168.242.101:2121/DCIM/Camera?fileName=13.jpg&move=done").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange);
            }
        }).to("file:///D:/CamelTest/");
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new FTPComponentRoute());
        main.run();
    }

}