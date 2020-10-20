package ies.weather;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import weather.ipma_client.IpmaCityForecast;
import weather.ipma_client.IpmaService;

//import java.util.logging.Logger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
/**
 * demonstrates the use of the IPMA API for weather forecast
 */
public class WeatherStarter {

    private static final int CITY_ID_AVEIRO = 1010500;

    /*
    loggers provide a better alternative to System.out.println
    https://rules.sonarsource.com/java/tag/bad-practice/RSPEC-106
     */

    //private static final Logger logger = Logger.getLogger(WeatherStarter.class.getName());
    private static final Logger logger = LogManager.getLogger(WeatherStarter.class.getName());

    public static void  main(String[] args ) {
        int cityCode = CITY_ID_AVEIRO;

        HashMap<String, Integer> cidades = new HashMap<String, Integer>();
        cidades.put("Aveiro", 1010500);
        cidades.put("Beja", 1020500);
        cidades.put("Braga", 1030300);
        cidades.put("Lisboa", 1110600);
        cidades.put("Faro", 1080500);
        cidades.put("Porto", 1131200);

        try {
            cityCode = Integer.parseInt(args[0]);
        }catch(Exception ex){
            try {
                if(cidades.keySet().contains(args[0])){
                    cityCode = cidades.get(args[0]);
                }
            }catch(Exception e){

            }
        }

        /*
        get a retrofit instance, loaded with the GSon lib to convert JSON into objects
         */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ipma.pt/open-data/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IpmaService service = retrofit.create(IpmaService.class);
        Call<IpmaCityForecast> callSync = service.getForecastForACity(cityCode);

        try {
            Response<IpmaCityForecast> apiResponse = callSync.execute();
            IpmaCityForecast forecast = apiResponse.body();

            if (forecast != null) {
                String weatherInfo = "for "+cityCode+"\n";
                for(int x = 0; x <5; x++) {
                    weatherInfo += "Date: " + forecast.getData().listIterator(x).next().getForecastDate() +
                                    " | Max temp: " + forecast.getData().listIterator(x).next().getTMax() +
                                    " | Min temp: " + forecast.getData().listIterator(x).next().getTMin() +
                                    " | Chance of rain: " + forecast.getData().listIterator(x).next().getPrecipitaProb() +"%\n";
                }
                logger.info(weatherInfo);
                System.exit(0);
            } else {
                logger.info( "No results!");
                System.exit(-1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
