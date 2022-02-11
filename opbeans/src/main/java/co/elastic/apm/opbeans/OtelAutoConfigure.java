package co.elastic.apm.opbeans;

import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OtelAutoConfigure {

    @Value("${otel.autoconfigure}")
    private String autoConfigure;

    @PostConstruct
    public void init() {
        if (Boolean.parseBoolean(autoConfigure)) {
            AutoConfiguredOpenTelemetrySdk.initialize();
        }
    }
}
