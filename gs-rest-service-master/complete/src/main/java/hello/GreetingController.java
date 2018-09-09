package hello;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private static String BUCKET_NAME = "fuchsia-pakistan-sangla-balletflats";
    private static String BATCH_FOLDER_NAME = "Batch/";

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping("/uploadShoeStep")
    public boolean uploadShoeStep(@RequestParam(value="StepId", defaultValue="no data.") String stepId,
                                    @RequestParam(value="BatchId", defaultValue="no data.") String batchId,
                                    @RequestParam(value="StepName", defaultValue="no data.") String stepName,
                                    @RequestParam(value="Date", defaultValue="no data.") String date,
                                    @RequestParam(value="EstimatedDeliveryDate", defaultValue="no data.") String estimatedDeliveryDate,
                                    @RequestParam(value="ArtisanName", defaultValue="no data.") String artisanName,
                                    @RequestParam(value="Notes", defaultValue="no data.") String notes) {

        AWSInteraction awsInteraction = new AWSInteraction();
        ShoeStepsMetaData metaData = new ShoeStepsMetaData();
        metaData.stepId = stepId;
        metaData.stepName = stepName;
        SimpleDateFormat ft =
                new SimpleDateFormat ("MM.dd.yyyy");
        try {
            metaData.date = ft.parse(date);
            metaData.estimatedDeliveryDate = ft.parse(estimatedDeliveryDate);
        } catch (java.text.ParseException ex){
            metaData.date = null;
        }
        metaData.batchId = batchId;
        metaData.artisanName = artisanName;
        metaData.notes = notes;


        return awsInteraction.uploadJsonData(BUCKET_NAME, batchId, stepName, metaData.toJsonString());
    }

    @RequestMapping("/getShoeSteps")
    public List<String> getShoeSteps(@RequestParam(value="OrderId", defaultValue="no data.") String orderId) {

        AWSInteraction awsInteraction = new AWSInteraction();
        List<String> steps = awsInteraction.downloadJsonDataListV2(BUCKET_NAME, BATCH_FOLDER_NAME, orderId);
        List<ProductionStep> allSteps = new ArrayList<>();
        int i =0;
        if (steps == null) System.out.println("Not able to find data for " + orderId);
        for(String tmp : steps){
            System.out.println("Step #" + i);
            if (tmp == null || tmp.trim().isEmpty()) continue;
            System.out.println(tmp);
            ShoeStepsMetaData metaData = ShoeStepsMetaData.parseJsonString(tmp);
            String artisanString = awsInteraction.downloadJsonData(BUCKET_NAME, "MasterData/Artisans",  metaData.artisanName);
            System.out.println(artisanString);
            Artisan artisan = Artisan.parseJsonString(artisanString);
            String shoeStepString = awsInteraction.downloadJsonData(BUCKET_NAME, "MasterData/Production_Steps",  metaData.stepName);
            System.out.println(shoeStepString);
            ShoeStep step = ShoeStep.parseJsonString(shoeStepString);
            ProductionStep productionStep = getProductionStep(step, metaData, artisan);
            allSteps.add(productionStep);
            System.out.println(productionStep.toJsonString());
            //results.add(productionStep.toJsonString());
            System.out.println("--------------------------------------------------------------------------------------------------------");
            i++;
        }

        allSteps.sort(shoeStepComparator);
        List<String> results = new ArrayList<String>();
        for(ProductionStep s : allSteps) {
            results.add(s.toJsonString());
        }
        return results;
    }

    @RequestMapping("/getShoeStep")
    public String getShoeStep(@RequestParam(value="OrderId", defaultValue="no data.") String orderId,
                                    @RequestParam(value="StepId", defaultValue="no data.") String stepId) {

        AWSInteraction awsInteraction = new AWSInteraction();
        List<String> steps = awsInteraction.downloadJsonDataListV2(BUCKET_NAME, BATCH_FOLDER_NAME, orderId);
        int i =0;
        for(String tmp : steps){
            System.out.println("Step #" + i);
            System.out.println(tmp);
            if (tmp == null || tmp.trim().isEmpty()) continue;
            ShoeStepsMetaData metaData = ShoeStepsMetaData.parseJsonString(tmp);
            if (metaData.stepId.equals(stepId)) {
                System.out.println("Chosen step is " + metaData.stepName);
                String artisanString = awsInteraction.downloadJsonData(BUCKET_NAME, "MasterData/Artisans",  metaData.artisanName);
                System.out.println(artisanString);
                Artisan artisan = Artisan.parseJsonString(artisanString);
                String shoeStepString = awsInteraction.downloadJsonData(BUCKET_NAME, "MasterData/Production_Steps",  metaData.stepName);
                System.out.println(shoeStepString);
                ShoeStep step = ShoeStep.parseJsonString(shoeStepString);
                ProductionStep productionStep = getProductionStep(step, metaData, artisan);
                System.out.println(productionStep.toJsonString());
                System.out.println("--------------------------------------------------------------------------------------------------------");
                return productionStep.toJsonString();
            }
            i++;
        }

        return "";
    }

    @RequestMapping("/getShoeStepsIds")
    public String getShoeStepIds(@RequestParam(value="OrderId", defaultValue="no data.") String orderId) {

        AWSInteraction awsInteraction = new AWSInteraction();
        List<String> data = awsInteraction.downloadJsonDataListV2(BUCKET_NAME, BATCH_FOLDER_NAME, orderId);
        System.out.println("list all the data in batch: " + orderId);
        int i =0;
        HashSet<String> stepsIds = new HashSet<>();
        for(String tmp : data){
            System.out.println("Step #" + i);
            System.out.println(tmp);
            ShoeStepsMetaData metaData2 = ShoeStepsMetaData.parseJsonString(tmp);
            stepsIds.add(metaData2.stepId);
            i++;
        }

        List<ProductionStep> steps = new ArrayList<ProductionStep>();
        String allShoeSteps = awsInteraction.downloadJsonData(BUCKET_NAME, "MasterData/Production_Steps", "Production_Steps");
        System.out.println(allShoeSteps);
        List<ShoeStepWithStatus> list = ShoeStepWithStatus.parseJsonList(allShoeSteps);
        Collections.sort(list, statusComparator);
        for(ShoeStepWithStatus s: list){
            if (stepsIds.contains(s.stepId))
                s.status = "1";
            else
                s.status = "0";
        }

        return ShoeStepWithStatus.toJsonList(list);
    }

    @RequestMapping("/searchBatches")
    public List<String> searchBatches(@RequestParam(value="FromBatch", defaultValue="") String fromBatch,
                                  @RequestParam(value="ToBatch", defaultValue="") String toBatch,
                                  @RequestParam(value="Count", defaultValue="no data.") String count,
                                  @RequestParam(value="Filter", defaultValue="no data.") String filter) {

        AWSInteraction awsInteraction = new AWSInteraction();

        return awsInteraction.searchJsonDataList(BUCKET_NAME, BATCH_FOLDER_NAME, fromBatch, toBatch);
    }

    private static Comparator<ProductionStep> shoeStepComparator = new Comparator<ProductionStep>() {
        @Override
        public int compare(ProductionStep o1, ProductionStep o2) {
            return Integer.parseInt(o1.stepId) - Integer.parseInt(o2.stepId);
        }
    };

    private static Comparator<ShoeStepWithStatus> statusComparator = new Comparator<ShoeStepWithStatus>() {
        @Override
        public int compare(ShoeStepWithStatus o1, ShoeStepWithStatus o2) {
            return Integer.parseInt(o1.stepId) - Integer.parseInt(o2.stepId);
        }
    };

    private static ProductionStep getProductionStep(ShoeStep shoeStep, ShoeStepsMetaData shoeStepsMetaData, Artisan artisan) {

        ProductionStep productionStep = new ProductionStep();
        if (shoeStepsMetaData != null) {
            productionStep.stepId = shoeStepsMetaData.stepId;
            productionStep.batchId = shoeStepsMetaData.batchId;
            productionStep.date = shoeStepsMetaData.date;
            productionStep.notes = shoeStepsMetaData.notes;
            productionStep.estimatedDeliveryDate = shoeStepsMetaData.estimatedDeliveryDate;
        }
        if (shoeStep != null) {
            productionStep.stepName = shoeStep.name;
            productionStep.stepNotes = shoeStep.notes;
            productionStep.stepImgUrl = shoeStep.imgUrl;
            productionStep.rawMaterialImgUrl = shoeStep.rawMaterialImgUrl;
            productionStep.locationImageUrl = shoeStep.locationImageUrl;
        }
        if (artisan != null){
            productionStep.artisanName = artisan.name;
            productionStep.artisanImgUrl = artisan.imgUrl;
            productionStep.artisanNotes = artisan.notes;
        }

        return productionStep;
    }
}
