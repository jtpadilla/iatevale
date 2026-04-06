package io.github.jtpadilla.product.simpleagent;

import com.google.lf.a2a.v1.A2A;
import io.github.jtpadilla.a2a.core.server.A2AService;
import io.github.jtpadilla.a2a.core.server.http.WellKnownHandler;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Services;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.grpc.GrpcReflectionFeature;
import io.helidon.webserver.grpc.GrpcRouting;
import io.helidon.webserver.http.HttpRouting;

@Service.Singleton
public class SimpleAgent {

    public static void main(String[] args) {
        Services.get(SimpleAgent.class).launch();
    }

    final private WellKnownHandler wellKnownHandler;
    final private A2AService a2aService;

    @Service.Inject
    public SimpleAgent(WellKnownHandler wellKnownHandler, A2AService a2aService) {
        this.wellKnownHandler = wellKnownHandler;
        this.a2aService = a2aService;
    }

    private void launch() {

        final HttpRouting.Builder httpRouting = HttpRouting.builder()
                .register("/.well-known", wellKnownHandler);

        final GrpcRouting.Builder grpcRouting = GrpcRouting.builder()
                .service(A2A.getDescriptor().getFile(), a2aService);

        final WebServer server = WebServer.builder()
                .port(8080)
                .addRouting(httpRouting)
                .addRouting(grpcRouting)
                .addFeature(GrpcReflectionFeature.create(cfg -> cfg.enabled(true)))
                .build()
                .start();

    }

}
