package co.elastic.apm.opbeans.logic;

import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class MyBusinessLogic {
    private final Tracer tracer = new ElasticApmTracer();

    public void myVeryImportantMethod(String someInfo) {
        Span transaction = tracer.activeSpan();
        Scope scope = tracer.buildSpan("Main-business-logic")
                .asChildOf(transaction).startActive(true);
        try {
            scope.span().setTag("info", someInfo);

            // My important logic comes here
            Thread.sleep(1000);

            scope.span().setTag("status", "Success");
        } catch (InterruptedException e) {
            scope.span().setTag("status", "Failure: " + e.getMessage());
        } finally {
            scope.close();
        }
    }
}
