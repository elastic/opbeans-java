package co.elastic.apm.opbeans.logic;

import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;

public class MyImportantBusinessLogic {
    @CaptureSpan(value = "Main-business-logic", type = "custom")
    public void myVeryImportantMethod(String someInfo) {
        Span span = ElasticApm.currentSpan();
        span.addTag("info", someInfo);

        // My important logic comes here
        try {
            Thread.sleep(1000);
            span.addTag("status", "Success");
        } catch (Exception e) {
            span.addTag("status", "Failure");
            span.captureException(e);
        }
    }
}
