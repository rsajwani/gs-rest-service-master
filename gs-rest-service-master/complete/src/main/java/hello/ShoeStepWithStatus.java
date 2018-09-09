package hello;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class ShoeStepWithStatus {
    public String stepName;
    public String stepId;
    public String status;

    public String toJsonString() {

        JSONObject obj = new JSONObject();
        obj.put("StepName", this.stepName);
        obj.put("StepId", this.stepId);
        obj.put("Status", this.status);

        return obj.toJSONString();

    }

    public static String toJsonList(List<ShoeStepWithStatus> list) {

        //Add employees to list
        JSONArray statusList = new JSONArray();

        for(ShoeStepWithStatus s : list){
            JSONObject obj = new JSONObject();
            obj.put("StepName", s.stepName);
            obj.put("StepId", s.stepId);
            obj.put("Status", s.status);

            statusList.add(obj);
        }

        return statusList.toJSONString();
    }

    public static ShoeStepWithStatus parseJsonString(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            ShoeStepWithStatus shoeStepStatus = new ShoeStepWithStatus();
            String stepName = (String) jsonObject.get("StepName");
            shoeStepStatus.stepName = stepName;
            String stepId = (String) jsonObject.get("StepId");
            shoeStepStatus.stepId = stepId;
            String status = (String) jsonObject.get("Status");
            shoeStepStatus.status = status;
            return shoeStepStatus;
        } catch (org.json.simple.parser.ParseException var12) {
            var12.printStackTrace();
            return null;
        }
    }

    public static List<ShoeStepWithStatus> parseJsonList(String jsonString) {
        JSONParser parser = new JSONParser();
        List<ShoeStepWithStatus> list = new ArrayList<>();
        try {
            Object obj = parser.parse(jsonString);


            JSONArray employeeList = (JSONArray) obj;
            System.out.println(employeeList);

            //Iterate over employee array
            //employeeList.forEach( step -> parseJsonString( (JSONObject) step ) );
            for (Object o : employeeList){
                JSONObject status = (JSONObject) o;
                ShoeStepWithStatus tmp = parseJsonString(status.toJSONString());
                list.add(tmp);
            }

            return list;
        } catch (org.json.simple.parser.ParseException var12) {
            var12.printStackTrace();
            return null;
        }
    }

}