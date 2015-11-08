import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public abstract class JSONParser {

    public static Set<String[]> parse(String filePath) {
        Set<String[]> set = new HashSet<>();

        JsonElement jsonElement = new JsonObject();

        try {
            jsonElement = new Gson().fromJson(Files.newBufferedReader(Paths.get(filePath)), jsonElement.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonElement = jsonElement.getAsJsonObject().get("pairs");

        for (JsonElement e : jsonElement.getAsJsonArray()) {
            JsonObject jObject = e.getAsJsonObject();

            String[] object = {
                    jObject.get("keyCode").getAsString(),
                    jObject.get("tosKey").getAsString(),
                    jObject.get("display").getAsString()
            };

            set.add(object);
        }

        return set;
    }

}
