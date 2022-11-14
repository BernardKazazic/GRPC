package fer.rassus.lab1.Server.rest.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RegisterDTO {

    @NotNull(message = "Latitude is required.")
    Double latitude;

    @NotNull(message = "Longitude is required.")
    Double longitude;

    @NotEmpty(message = "Ip is required")
    String ip;

    @NotEmpty(message = "Port is required")
    String port;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
