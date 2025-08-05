package grpcJava;

import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import protoOut.PlotRequest;
import protoOut.PlotResponse;
import protoOut.PlotServiceGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ImageClient {

    public static byte[] getImage(String address, int port, long[] x, double[] y) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(address, port ).usePlaintext().build();
        PlotServiceGrpc.PlotServiceStub serverStub = PlotServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        final byte[][] imageBytes = new byte[1][];

        PlotRequest.Builder requestBuilder = PlotRequest.newBuilder();
        int i = 0;
        for (long millis : x) {
            long seconds = millis / 1000;
            int nanos = (int) ((millis % 1000) * 1_000_000);
            Timestamp ts = Timestamp.newBuilder()
                    .setSeconds(seconds)
                    .setNanos(nanos)
                    .build();
            requestBuilder.addX(ts);
        }
        i = 0;
        for (double value : y) {
            requestBuilder.addY(value);
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

    public static void getImage(long[] x, double[] y) {
        getImage("localhost",8080,x,y);
    }

}
