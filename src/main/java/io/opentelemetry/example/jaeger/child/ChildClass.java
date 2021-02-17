package io.opentelemetry.example.jaeger.child;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChildClass {

    public void doWork(Tracer tracer) {

        Span childSpan = tracer.spanBuilder("child")
                // NOTE: setParent(...) is not required;
                // `Span.current()` is automatically added as the parent
                .startSpan();
        try (Scope scope = childSpan.makeCurrent()) {
            log.info("Inside child log");
            System.out.println("inside child ");
            childSpan.setAttribute("method", "doWork");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            childSpan.end();
        }
    }
}
