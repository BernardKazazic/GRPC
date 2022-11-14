package fer.rassus.lab1.http;

import fer.rassus.lab1.http.DTO.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpClient {

    @POST("registerSensor/")
    Call<RegisterResponseData> register(@Body RegisterData registerData);

    @GET("getClosestSensor/")
    Call<GetClosestResponseData> getClosestSensor(@Query("longitude") double longitude, @Query("latitude") double latitude);

    @POST("saveMeasurement/")
    Call<SaveMeasurementResponseData> saveMeasurement(@Query("sensorId") long sensorId, @Body SaveMeasurementData saveMeasurementData);
}
