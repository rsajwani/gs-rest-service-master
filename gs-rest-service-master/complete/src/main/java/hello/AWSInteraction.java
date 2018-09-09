package hello;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AWSInteraction {

    private AWSCredentials credentials;
    public AWSInteraction (){
        credentials = new BasicAWSCredentials("AKIAIGXCYPNXXZCDEP3A", "+75QPTpjYq3LDDo6LOz3idjaEKdc7KlAZqggcptG");

    }

    private AmazonS3 createS3Client () {
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(provider).build();
        return s3Client;
    }

    private static String createKey(String batchId, String stepName){
        return batchId + "/" + stepName ;
    }

    public List<String> listBuckets(){
        try {
            List<String> buckets = new ArrayList<String>();
            AmazonS3 s3Client = createS3Client();

            for (Bucket bucket : s3Client.listBuckets()) {
                buckets.add(bucket.getName());
            }

            return buckets;
        } catch (Exception ex){
            return null;
        }

    }

    public boolean createBucket(String bucketName) {
        AmazonS3 s3Client = createS3Client();

        if (!s3Client.doesBucketExistV2(bucketName)) {
            // Because the CreateBucketRequest object doesn't specify a region, the
            // bucket is created in the region specified in the client.
            Bucket bucket = s3Client.createBucket(new CreateBucketRequest(bucketName));

            return (bucket != null);
            // Verify that the bucket was created by retrieving it and checking its location.
            //String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
            //System.out.println("Bucket location: " + bucketLocation);
        }

        return true;
    }

    public void createFolder(String bucketName, String folderName) {
        AmazonS3 s3Client = createS3Client();

        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                folderName, emptyContent, metadata);
        // send request to S3 to create folder
        PutObjectResult res = s3Client.putObject(putObjectRequest);

    }

    public boolean uploadJsonData (String bucketName, String batchId, String stepName, String jsonString){

        try
        {
            AmazonS3 s3Client = createS3Client();
            String key = createKey(batchId, stepName);
            byte[] contentAsBytes = jsonString.getBytes("UTF-8");
            ByteArrayInputStream contentsAsStream      = new ByteArrayInputStream(contentAsBytes);
            ObjectMetadata md = new ObjectMetadata();
            md.setContentLength(contentAsBytes.length);
            s3Client.putObject(new PutObjectRequest(bucketName, key, contentsAsStream, md));
            return true;
        }
        catch(AmazonServiceException e)
        {
            //log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        catch(Exception ex)
        {
            //log.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    public String downloadJsonData (String bucketName, String batchId, String stepName){

        try
        {
            AmazonS3 s3Client = createS3Client();
            String key = createKey(batchId, stepName);
            S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, key));
            //System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            //System.out.println("Content: ");
            String jsonString = displayTextInputStream(s3object.getObjectContent());
            return jsonString;
        }
        catch(AmazonServiceException e)
        {
            //log.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
        catch(Exception ex)
        {
            //log.log(Level.SEVERE, ex.getMessage(), ex);
            return "";
        }
    }

    public List<String> downloadJsonDataList (String bucketName, String batchId){

        try
        {
            AmazonS3 s3Client = createS3Client();
            List<String> dataList = new ArrayList<String>();
            // maxKeys is set to 2 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(batchId);
            ListObjectsV2Result result;

            do {
                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, objectSummary.getKey()));
                    String jsonString = displayTextInputStream(s3object.getObjectContent());
                    dataList.add(jsonString);
                }
                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                //System.out.println("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());

            //System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            //System.out.println("Content: ");
            //String jsonString = displayTextInputStream(s3object.getObjectContent());

            return dataList;
        }
        catch(AmazonServiceException e)
        {
            //log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        catch(Exception ex)
        {
            //log.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public List<String> downloadJsonDataKeys (String bucketName, String batchId){

        try
        {
            AmazonS3 s3Client = createS3Client();
            List<String> keys = new ArrayList<String>();
            // maxKeys is set to 2 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(batchId);
            ListObjectsV2Result result;

            do {
                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                    keys.add(objectSummary.getKey());
                }
                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                //System.out.println("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());

            //S3Object s3object = s3Client.listBuckets(new GetObjectRequest(bucketName, key));
            //System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            //System.out.println("Content: ");
            //String jsonString = displayTextInputStream(s3object.getObjectContent());

            return keys;
        }
        catch(AmazonServiceException e)
        {
            //log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        catch(Exception ex)
        {
            //log.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public List<String> downloadJsonDataKeysV2 (String bucketName, String batchId, String keyToSearch){

        try
        {
            AmazonS3 s3Client = createS3Client();
            List<String> keys = new ArrayList<String>();
            // maxKeys is set to 2 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            System.out.println("list all the data in batch: " + batchId);
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(batchId);
            ListObjectsV2Result result;

            do {
                result = s3Client.listObjectsV2(req);
                int lookoutKey = Integer.parseInt(keyToSearch);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                    String key = objectSummary.getKey();
                    String[] splittedKey = key.split("/");
                    if (splittedKey.length != 3) return null;
                    String tmpKey = splittedKey[1];
                    String[] tmp = tmpKey.split("-");
                    if (tmp.length != 2) return null;
                    int start = Integer.parseInt(tmp[0]);
                    int end = Integer.parseInt(tmp[1]);
                    if (lookoutKey >= start && lookoutKey <= end){
                        keys.add(objectSummary.getKey());
                    }
                }
                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                //System.out.println("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());

            //S3Object s3object = s3Client.listBuckets(new GetObjectRequest(bucketName, key));
            //System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            //System.out.println("Content: ");
            //String jsonString = displayTextInputStream(s3object.getObjectContent());

            return keys;
        }
        catch(AmazonServiceException e)
        {
            //log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        catch(Exception ex)
        {
            //log.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public List<String> downloadJsonDataListV2 (String bucketName, String batchId, String keyToSearch){

        try
        {
            AmazonS3 s3Client = createS3Client();
            List<String> keys = new ArrayList<String>();
            // maxKeys is set to 2 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            System.out.println("list all the data in batch: " + batchId);
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(batchId);
            ListObjectsV2Result result;

            do {
                result = s3Client.listObjectsV2(req);
                int lookoutKey = Integer.parseInt(keyToSearch);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                    String key = objectSummary.getKey();
                    String[] splittedKey = key.split("/");
                    if (splittedKey.length < 2) return null;
                    String tmpKey = splittedKey[1];
                    String[] tmp = tmpKey.split("-");
                    if (tmp.length != 2) return null;
                    int start = Integer.parseInt(tmp[0]);
                    int end = Integer.parseInt(tmp[1]);
                    if (lookoutKey >= start && lookoutKey <= end){
                        S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, objectSummary.getKey()));
                        String jsonString = displayTextInputStream(s3object.getObjectContent());
                        keys.add(jsonString);
                    }
                }
                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                //System.out.println("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());

            //S3Object s3object = s3Client.listBuckets(new GetObjectRequest(bucketName, key));
            //System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            //System.out.println("Content: ");
            //String jsonString = displayTextInputStream(s3object.getObjectContent());

            return keys;
        }
        catch(AmazonServiceException e)
        {
            //log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        catch(Exception ex)
        {
            //log.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    private static String displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }



}

