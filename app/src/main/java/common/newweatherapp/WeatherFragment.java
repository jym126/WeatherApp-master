package common.newweatherapp;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.redorigami.simpleweather.R;


public class WeatherFragment extends Fragment {
    Typeface weatherFont;
     
    private TextView cityField;
	private TextView weatherIcon;
    private TextView updatedField;
	private TextView currentTemperatureField;
	private TextView detailsField;
	private String descripcion;
	private String descripcion1;
     
    Handler handler;
 
    public WeatherFragment(){   
        handler = new Handler();
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
		cityField = (TextView)rootView.findViewById(R.id.city_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
		detailsField = (TextView)rootView.findViewById(R.id.details_field);
         
        weatherIcon.setTypeface(weatherFont);
        return rootView; 
    }
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  

	    weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
	    updateWeatherData(new CityPreference(getActivity()).getCity());
	}
     
	
	private void updateWeatherData(final String city){
	    new Thread(){
	        public void run(){
	            final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
	            if(json == null){
	                handler.post(new Runnable(){
	                    public void run(){
	                        Toast.makeText(getActivity(), 
	                                getActivity().getString(R.string.place_not_found), 
	                                Toast.LENGTH_LONG).show(); 
	                    }
	                });
	            } else {
	                handler.post(new Runnable(){
	                    public void run(){
	                        renderWeather(json);
	                    }
	                });
	            }               
	        }
	    }.start();
	}
	
	private void renderWeather(JSONObject json){
	    try {
			cityField.setText(String.format("%s, %s", json.getString("name").toUpperCase(Locale.US), json.getJSONObject("sys").getString("country")));

			JSONObject details = json.getJSONArray("weather").getJSONObject(0);
			JSONObject main = json.getJSONObject("main");

			descripcion = details.getString("description");
			descripcion1 = descripcion.toUpperCase(Locale.US);
			if (descripcion1.equals("FEW CLOUDS")){
				descripcion1 = "pocasNubes";}
			else if (descripcion1.equals("CLEAR SKY")){
				descripcion1 = "Cielos Claros";}
			else if (descripcion1.equals("OVERCAST CLOUDS")){
				descripcion1 = "Nublado";}
			else if (descripcion1.equals("LIGHT RAIN")){
				descripcion1 = "Lluvia Ligera";}
			else if (descripcion1.equals("SCATTERED CLOUDS")){
				descripcion1 = "Nubes Dispersas";}
			else if (descripcion1.equals("LIGHT SNOW")){
				descripcion1 = "Nieve Ligera";}


	        detailsField.setText(
					String.format("%s\nHumedad: %s%%\nPresión: %s hPa", descripcion1, main.getString("humidity"), main.getString("pressure")));
	         
	        currentTemperatureField.setText(
					String.format("%s ℃", String.format("%.2f", main.getDouble("temp"))));
	 
	        DateFormat df = DateFormat.getDateTimeInstance();
	        String updatedOn = df.format(new Date(json.getLong("dt")*1000));
	        updatedField.setText(String.format("Actualizado: %s", updatedOn));
	 
	        setWeatherIcon(details.getInt("id"),
	                json.getJSONObject("sys").getLong("sunrise") * 1000,
	                json.getJSONObject("sys").getLong("sunset") * 1000);

	    }catch(Exception e){
	        Log.e("SimpleWeather", "Field not present in JSON Received");
	    }
	}
	
	private void setWeatherIcon(int actualId, long sunrise, long sunset){
	    int id = actualId / 100;
	    String icon = "";
	    if(actualId == 800){
	        long currentTime = new Date().getTime();
	        if(currentTime>=sunrise && currentTime<sunset) {
	            icon = getActivity().getString(R.string.weather_sunny);
	        } else {
	            icon = getActivity().getString(R.string.weather_clear_night);
	        }
	    } else {
	        switch(id) {
	        case 2 : icon = getActivity().getString(R.string.weather_thunder);
	                 break;         
	        case 3 : icon = getActivity().getString(R.string.weather_drizzle);
	                 break;     
	        case 7 : icon = getActivity().getString(R.string.weather_foggy);
	                 break;
	        case 8 : icon = getActivity().getString(R.string.weather_cloudy);
	                 break;
	        case 6 : icon = getActivity().getString(R.string.weather_snowy);
	                 break;
	        case 5 : icon = getActivity().getString(R.string.weather_rainy);
	                 break;
	        }
	    }
	    weatherIcon.setText(icon);
	}
	
	public void changeCity(String city){
	    updateWeatherData(city);
	}
	
}