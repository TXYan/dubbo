package com.alibaba.dubbo.rpc.benchmark;

public class BenchmarkServer extends AbstractBenchmarkServer {

    public static void main(String[] args) throws Exception {
        args = new String[5];
        args[0] = "10080";
        args[1] = "200";
        args[2] = "1000";
        args[3] = "netty";
        args[4] = "hessian2";

        new BenchmarkServer().run(args);
        synchronized (BenchmarkServer.class) {
            BenchmarkServer.class.wait();
        }
    }
}
