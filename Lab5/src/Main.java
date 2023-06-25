import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) {
        try {
            // Create a JSON parser object
            JSONParser parser = new JSONParser();

            // Read the JSON file
            FileReader fileReader = new FileReader("weather.json");

            // Parse the JSON file into a JSON object
            JSONObject weatherData = (JSONObject) parser.parse(fileReader);

            JSONArray daily = (JSONArray) weatherData.get("daily");

            JSONObject output = new JSONObject();
            output.put("lat", weatherData.get("lat"));
            output.put("lon", weatherData.get("lon"));
            output.put("timezone", weatherData.get("timezone"));
            output.put("timezone_offset", weatherData.get("timezone_offset"));

            JSONArray filteredData = new JSONArray();

            for (int i = 0; i < daily.size(); i++) {
                JSONObject dayData = (JSONObject) daily.get(i);
                JSONObject feelsLike = (JSONObject) dayData.get("feels_like");
                if(Float.parseFloat(feelsLike.get("day").toString()) > 15) {
                    filteredData.add(dayData);
                }
            }

            output.put("daily", filteredData);

            // Write the JSON object to the file
            FileWriter fileWriter = new FileWriter("./summer_weather.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            fileWriter.write(gson.toJson(output));
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}