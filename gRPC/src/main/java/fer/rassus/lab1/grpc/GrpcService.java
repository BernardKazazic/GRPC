package fer.rassus.lab1.grpc;

import fer.rassus.lab1.MeasurementGrpc;
import fer.rassus.lab1.RequestResponseMessage;
import fer.rassus.lab1.SensorGrpc;
import io.grpc.stub.StreamObserver;

public class GrpcService extends SensorGrpc.SensorImplBase  {

    private MeasurementGrpc currentMeasurement;

    public GrpcService() {
        this.currentMeasurement = null;
    }

    @Override
    public void getMeasurement(RequestResponseMessage request, StreamObserver<MeasurementGrpc> responseObserver) {
        MeasurementGrpc response = currentMeasurement;
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sendMeasurement(MeasurementGrpc request, StreamObserver<RequestResponseMessage> responseObserver) {
        currentMeasurement = request;
        RequestResponseMessage response = RequestResponseMessage.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
