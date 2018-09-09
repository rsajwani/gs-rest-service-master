package hello;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShoeStepsMetaData {
    public String stepId;
    public String batchId;
    public String stepName;
    public Date date;
    public String artisanName;
    public String notes;
    public Date estimatedDeliveryDate;

    public String toJsonString() {

        JSONObject obj = new JSONObject();
        obj.put("StepId", this.stepId);
        obj.put("StepName", this.stepName);
        obj.put("BatchId", this.batchId);
        SimpleDateFormat ft =
                new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        obj.put("Date", ft.format(this.date));
        obj.put("EstimatedDeliveryDate", ft.format(this.estimatedDeliveryDate));
        obj.put("ArtisanName", this.artisanName);
        obj.put("Notes", this.notes);

        return obj.toJSONString();
    }

    public static ShoeStepsMetaData parseJsonString(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            ShoeStepsMetaData stepsMetaData = new ShoeStepsMetaData();
            String stepId = (String) jsonObject.get("StepId");
            stepsMetaData.stepId = stepId;
            String stepName = (String) jsonObject.get("StepName");
            stepsMetaData.stepName = stepName;
            String batchId = (String) jsonObject.get("BatchId");
            stepsMetaData.batchId = batchId;
            String date = (String) jsonObject.get("Date");
            String estimatedDeliveryDate = (String) jsonObject.get("EstimatedDeliveryDate");
            SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

            try {
                stepsMetaData.date = ft.parse(date);
                stepsMetaData.estimatedDeliveryDate = ft.parse(estimatedDeliveryDate);
            } catch (ParseException var11) {
                stepsMetaData.date = null;
            }

            String artisanName = (String) jsonObject.get("ArtisanName");
            stepsMetaData.artisanName = artisanName;
            String notes = (String) jsonObject.get("Notes");
            stepsMetaData.notes = notes;
            return stepsMetaData;
        } catch (org.json.simple.parser.ParseException var12) {
            var12.printStackTrace();
            return null;
        }
    }
}
