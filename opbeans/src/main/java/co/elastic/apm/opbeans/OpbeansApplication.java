/*-
 * #%L
 * Opbeans Java Demo Application
 * %%
 * Copyright (C) 2018 the original author or authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package co.elastic.apm.opbeans;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpbeansApplication {

    public static void main(String[] args) {

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    // ignored
                }

                Tracer tracer = GlobalOpenTelemetry.get().getTracer("co.elastic.apm:opbeans");

                Span span = tracer.spanBuilder("fake HTTP transaction")
                        .startSpan()
                        .setAttribute("http.method", "GET")
                        .setAttribute("http.status_code", 200);

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    // ignored
                }

                span.end();

            }
        };

        t.start();

        SpringApplication.run(OpbeansApplication.class, args);
    }
}
