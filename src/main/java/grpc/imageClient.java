package grpc;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import protoOut.PlotRequest;
import protoOut.PlotResponse;
import protoOut.PlotServiceGrpc;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class imageClient {

    static byte[] getImage(String address, int port, long[] x, double[] y) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port ).usePlaintext().build();
        PlotServiceGrpc.PlotServiceStub serverStub = PlotServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        final byte[][] imageBytes = new byte[1][];

        PlotRequest.Builder requestBuilder = PlotRequest.newBuilder();
        int i = 0;
        for (long value : x) {
            requestBuilder.setX(i,value);
        }
        i = 0;
        for (double value : y) {
            requestBuilder.setY(i,value);
        }
        PlotRequest request = requestBuilder.build();

        StreamObserver<PlotResponse> responseStreamObserver = new StreamObserver<PlotResponse>() {
            @Override
            public void onNext(PlotResponse plot) {
                imageBytes[0] = plot.getImage().toByteArray();
            }

            @Override
        public void onError(Throwable t) {

        }
        @Override
        public void onCompleted() {
                latch.countDown();
        }
        };
        serverStub.generatePlot(request,responseStreamObserver);

        try {
            latch.await();
            channel.shutdown();
            channel.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return imageBytes[0];
    };

    static void getImage(long[] x, double[] y) {
        getImage("localhost",8080,x,y);
    }

}
