package at.fhv.sysarch.lab3;

import at.fhv.sysarch.lab3.environment.Weather;

public class ActorStates {
    private boolean isAcOn = false;
    private boolean isMediaStationOn = false;
    private boolean areBlindsClosed = false;
    private Double currentTemperature = 0.0d;
    private Weather weather;

    public boolean isAcOn() {
        return isAcOn;
    }

    public void setAcOn(boolean acOn) {
        isAcOn = acOn;
    }

    public boolean isMediaStationOn() {
        return isMediaStationOn;
    }

    public void setMediaStationOn(boolean mediaStationOn) {
        isMediaStationOn = mediaStationOn;
    }

    public boolean isAreBlindsClosed() {
        return areBlindsClosed;
    }

    public void setAreBlindsClosed(boolean areBlindsClosed) {
        this.areBlindsClosed = areBlindsClosed;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
