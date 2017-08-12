package com.jin.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    private Server server;

    public void start() throws IOException {
        this.server = ServerBuilder.forPort(8899).addService(new StudentServiceImpl()).build().start();

        // 回调钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("jvm 关闭。");
            GrpcServer.this.stop();
        }));

        System.out.println("server start.");
    }

    public void stop()
    {
        if(null != this.server)
        {
            System.out.println("服务器关闭。");
            this.server.shutdown();
        }
    }

    public void awaitTermination() throws InterruptedException
    {
        if(null != this.server)
        {
            this.server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        GrpcServer server = new GrpcServer();

        server.start();

        server.awaitTermination();
    }
}
