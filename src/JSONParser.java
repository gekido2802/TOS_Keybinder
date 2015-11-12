import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class JSONParser {

    private static Map<String, String> keys;

    // PARSE THE KEYCODE JSON FILE
    public static Set<String[]> parseKeyCode(InputStream inputStream) {
        Set<String[]> set = new HashSet<>();

        JsonElement jsonElement = new JsonArray();

        jsonElement = new Gson().fromJson(new InputStreamReader(inputStream), jsonElement.getClass());

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

    // PARSE THE TOS KEY ID JSON FILE
    public static void parseTOSKeyId(InputStream inputStream) {
        keys = new HashMap<>();

        JsonElement jsonElement = new JsonArray();

        jsonElement = new Gson().fromJson(new InputStreamReader(inputStream), jsonElement.getClass());

        for (JsonElement e : jsonElement.getAsJsonArray()) {
            JsonObject object = e.getAsJsonObject();

            keys.put(object.get("tosId").getAsString(), object.get("display").getAsString());
        }
    }

    // RETURN ALL THE TOS KEY ID
    public static Map<String, String> getKeys() {
        return keys;
    }
}
