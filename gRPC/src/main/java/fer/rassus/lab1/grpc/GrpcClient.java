package fer.rassus.lab1.grpc;

import com.google.protobuf.NullValue;
import fer.rassus.lab1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
    private ManagedChannel localChannel;
    private ManagedChannel otherChannel;

    private SensorGrpc.SensorBlockingStub localBlockingStub;
    private SensorGrpc.SensorBlockingStub otherBlockingStub;

    public GrpcClient(String host, int port) {
        this.localChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.localBlockingStub = SensorGrpc.newBlockingStub(localChannel);
        this.otherChannel = null;
        this.otherBlockingStub = null;
    }

    public void createOtherConnection(String host, int port) {
        this.otherChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.otherBlockingStub = SensorGrpc.newBlockingStub(otherChannel);
    }

    public boolean hasOtherConnection() {
        return otherBlockingStub != null;
    }

    public void stop() throws InterruptedException {
        localChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        if(otherChannel != null) {
            otherChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public Measurement getMeasurement() {
        Measurement measurement = null;
        if(otherBlockingStub != null) {
            RequestResponseMessage request = RequestResponseMessage.newBuilder().build();
            measurement = new Measurement(otherBlockingStub.getMeasurement(request));
            logger.info("GRPC Client: Recieved measurement from closest sensor");
        }
        else {
            logger.info("GRPC Client: There is no connection to closest sensor");
        }
        return measurement;
    }

    public void sendMeasurement(Measurement measurement) {
        logger.info("GRPC Client: Sending measurement to GRPC service");
        MeasurementGrpc.Builder builder = MeasurementGrpc.newBuilder()
                .setTemperature(measurement.getTemperature())
                .setPressure(measurement.getPressure())
                .setHumidity(measurement.getHumidity());

        if(measurement.coExists()) {
            builder.setCo(Co.newBuilder().setCo(measurement.getCo()).build());
        }
        else {
            builder.setCo(Co.newBuilder().setNull(NullValue.NULL_VALUE).build());
        }

        if(measurement.so2Exists()) {
            builder.setSo2(So2.newBuilder().setSo2(measurement.getSo2()).build());
        }
        else {
            builder.setSo2(So2.newBuilder().setNull(NullValue.NULL_VALUE).build());
        }

        if(measurement.no2Exists()) {
            builder.setNo2(No2.newBuilder().setNo2(measurement.getNo2()).build());
        }
        else {
            builder.setNo2(No2.newBuilder().setNull(NullValue.NULL_VALUE).build());
        }

        MeasurementGrpc request = builder.build();

        localBlockingStub.sendMeasurement(request);
    }
}
