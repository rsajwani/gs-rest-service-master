package hello;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShoeStep {
    public String name;
    public String imgUrl;
    public String rawMaterialImgUrl;
    public String locationImageUrl;
    public String notes;
    public String artisanName;

    public String toJsonString() {

        JSONObject obj = new JSONObject();
        obj.put("Name", this.name);
        obj.put("ImgUrl", this.imgUrl);
        obj.put("RawMaterialImgUrl", this.rawMaterialImgUrl);
        obj.put("LocationImageUrl", this.locationImageUrl);
        obj.put("ArtisanName", this.artisanName);
        obj.put("Notes", this.notes);

        return obj.toJSONString();

    }

    public static ShoeStep parseJsonString(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            ShoeStep shoeStep = new ShoeStep();
            String stepName = (String) jsonObject.get("Name");
            shoeStep.name = stepName;
            String imgUrl = (String) jsonObject.get("ImgUrl");
            shoeStep.imgUrl = imgUrl;
            String rawMaterialImgUrl = (String) jsonObject.get("RawMaterialImgUrl");
            shoeStep.rawMaterialImgUrl = rawMaterialImgUrl;
            String locationImageUrl = (String) jsonObject.get("LocationImageUrl");
            shoeStep.locationImageUrl = locationImageUrl;
            String artisanName = (String) jsonObject.get("ArtisanName");
            shoeStep.artisanName = artisanName;
            String notes = (String) jsonObject.get("Notes");
            shoeStep.notes = notes;
            return shoeStep;
        } catch (org.json.simple.parser.ParseException var12) {
            var12.printStackTrace();
            return null;
        }
    }
}