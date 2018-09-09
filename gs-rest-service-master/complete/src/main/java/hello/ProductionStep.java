package hello;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductionStep {
    public String stepId;
    public Date date;
    public String batchId;
    public String stepName;
    public String artisanName;
    public String artisanImgUrl;
    public String stepImgUrl;
    public String rawMaterialImgUrl;
    public String locationImageUrl;
    public String artisanNotes;
    public String stepNotes;
    public String notes;
    public Date estimatedDeliveryDate;


    public String toJsonString() {

        JSONObject obj = new JSONObject();

        obj.put("StepId", this.stepId);
        SimpleDateFormat ft =
                new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        obj.put("Date", ft.format(this.date));
        obj.put("EstimatedDeliveryDate", ft.format(this.estimatedDeliveryDate));
        obj.put("BatchId", this.batchId);
        obj.put("StepName", this.stepName);
        obj.put("ArtisanName", this.artisanName);
        obj.put("ArtisanImgUrl", this.artisanImgUrl);
        obj.put("StepImgUrl", this.stepImgUrl);
        obj.put("RawMaterialImgUrl", this.rawMaterialImgUrl);
        obj.put("LocationImageUrl", this.locationImageUrl);
        obj.put("ArtisanNotes", this.artisanNotes);
        obj.put("StepNotes", this.stepNotes);
        obj.put("Notes", this.notes);

        return obj.toJSONString();

    }

    public static ProductionStep parseJsonString(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            ProductionStep prodStep = new ProductionStep();
            prodStep.stepId = (String) jsonObject.get("StepId");
            prodStep.batchId = (String) jsonObject.get("BatchId");
            prodStep.stepName = (String) jsonObject.get("StepName");
            prodStep.artisanName = (String) jsonObject.get("ArtisanName");
            prodStep.artisanImgUrl = (String) jsonObject.get("ArtisanImgUrl");
            prodStep.stepImgUrl = (String) jsonObject.get("StepImgUrl");
            prodStep.rawMaterialImgUrl = (String) jsonObject.get("RawMaterialImgUrl");
            prodStep.locationImageUrl = (String) jsonObject.get("LocationImageUrl");
            prodStep.artisanNotes = (String) jsonObject.get("ArtisanNotes");
            prodStep.stepNotes = (String) jsonObject.get("StepNotes");
            prodStep.notes = (String) jsonObject.get("Notes");

            String date = (String) jsonObject.get("Date");
            String estimatedDeliveryDate = (String) jsonObject.get("EstimatedDeliveryDate");
            SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

            try {
                prodStep.date = ft.parse(date);
                prodStep.estimatedDeliveryDate = ft.parse(estimatedDeliveryDate);
            } catch (ParseException var11) {
                prodStep.date = null;
            }


            return prodStep;
        } catch (org.json.simple.parser.ParseException var12) {
            var12.printStackTrace();
            return null;
        }
    }
}