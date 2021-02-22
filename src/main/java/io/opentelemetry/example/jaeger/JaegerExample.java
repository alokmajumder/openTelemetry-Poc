package io.opentelemetry.example.jaeger;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.example.jaeger.child.ChildClass;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

//@Slf4j
public class JaegerExample {

    private final Tracer tracer;

    public JaegerExample(OpenTelemetry openTelemetry) {
        tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample.test");
    }

    public static void main(String[] args) {
        // Parsing the input
        System.setProperty("otel.resource.attributes", "service.name=OtlpExporterExample");

        if (args.length < 2) {
            System.out.println("Missing [hostname] [port]");
            System.exit(1);
        }
        String jaegerHostName = args[0];
        int jaegerPort = Integer.parseInt(args[1]);

        // it is important to initialize your SDK as early as possible in your application's lifecycle
          OpenTelemetry openTelemetry = JaegerConfiguration.initOpenTelemetry(jaegerHostName, jaegerPort);

       // OpenTelemetry openTelemetry = OtelConfiguration.initOpenTelemetry();


        // Start the example
        JaegerExample example = new JaegerExample(openTelemetry);
        // generate a few sample spans
        for (int i = 0; i < 10; i++) {
            System.out.println("inside main " + i);
            example.myWonderfulUseCase();
        }

        System.out.println("end of trace");
    }

    private void myWonderfulUseCase() {
        // Generate a span
        Span parentSpan = this.tracer.spanBuilder("Start my wonderful use case").startSpan();
        // execute my use case - here we simulate a wait
        //   log.info("Inside Parent log");
        parentSpan.setAttribute("method", "myWonderfulUseCase");

        try (Scope scope = parentSpan.makeCurrent()) {
            parentSpan.addEvent("Event 0");
            System.out.println("inside parent ");

            ChildClass childClass = new ChildClass();
            childClass.doWork(this.tracer);
            parentSpan.addEvent("Event 1");
        } finally {
            parentSpan.end();
        }


    }

    private void doWork(Span parentSpan) {
        try {
            Span childSpan = tracer.spanBuilder("child")
                    .setParent(Context.current().with(parentSpan))
                    .startSpan();
            childSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "PUT");
            childSpan.setAttribute("method", "doWork");


            Thread.sleep(1000);
            childSpan.end();
        } catch (InterruptedException e) {
            // do the right thing here
        }
    }

}
