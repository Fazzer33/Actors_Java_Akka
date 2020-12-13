package at.fhv.sysarch.lab3.environment;

import at.fhv.sysarch.lab3.INotification;

public class WeatherNotification implements INotification {

    private Weather weather;

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }


}
