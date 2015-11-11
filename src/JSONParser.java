import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public abstract class JSONParser {

    public static Set<String[]> parse(InputStream inputStream) {
        Set<String[]> set = new HashSet<>();

        JsonElement jsonElement = new JsonObject();

        jsonElement = new Gson().fromJson(new InputStreamReader(inputStream), jsonElement.getClass());

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
