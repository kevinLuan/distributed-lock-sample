package com.lyh.grpc.server;

import java.io.IOException;
import java.util.logging.Logger;
import com.lyh.grpc.server.api.EchoServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

/**
 * A simple server that like {@link io.grpc.examples.helloworld.HelloWorldServer}. You can get and
 * response any header in {@link io.grpc.examples.header.HeaderServerInterceptor}.
 */
public class CustomHeaderServer {
  private static final Logger logger = Logger.getLogger(CustomHeaderServer.class.getName());

  /* The port on which the server should run */
  private static final int port = 50051;
  private Server server;

  private void start() throws IOException {
    server = ServerBuilder.forPort(port)
        .addService(
            ServerInterceptors.intercept(new EchoServiceImpl(), new HeaderServerInterceptor()))
        .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        CustomHeaderServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final CustomHeaderServer server = new CustomHeaderServer();
    server.start();
    server.blockUntilShutdown();
  }
}
