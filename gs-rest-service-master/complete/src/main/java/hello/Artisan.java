package hello;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Artisan {
    public String name;
    public String imgUrl;
    public String notes;

    public String toJsonString() {

        JSONObject obj = new JSONObject();
        obj.put("Name", this.name);
        obj.put("ImgUrl", this.imgUrl);
        obj.put("Notes", this.notes);


        return obj.toJSONString();

    }

    public static Artisan parseJsonString(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            Artisan artisan = new Artisan();
            String name = (String) jsonObject.get("Name");
            artisan.name = name;
            String imgUrl = (String) jsonObject.get("ImgUrl");
            artisan.imgUrl = imgUrl;
            String notes = (String) jsonObject.get("Notes");
            artisan.notes = notes;
            return artisan;
        } catch (org.json.simple.parser.ParseException var12) {
            var12.printStackTrace();
            return null;
        }
    }
}