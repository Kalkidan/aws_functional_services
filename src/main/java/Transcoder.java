
import awscode.AsyncMessageReciever;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClientBuilder;
import com.amazonaws.services.elastictranscoder.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.http.util.TextUtils;
import javax.jms.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Transcoder implements MessageListener{

    public static final String LOGGER = Transcoder.class.getSimpleName();

    public void launchTranscoder(String[] args) throws JMSException, InterruptedException {

          /************************************************************
           *  FIRST STEP IN USING ASW ELASTIC TRANS CODER
           *  Remember to set up credentials @AWSCredentials.properties
           *  Just an extra stage to demonstrate AWS functionality not necessarily
           *  a "MUST" follow step.
           ************************************************************/
        AWSCredentialsProvider credentialsProvider =    new ClasspathPropertiesFileCredentialsProvider("config/AwsCredentials.properties");
        //Get the creds path and values
        Logger.getLogger(LOGGER).info(credentialsProvider.toString());
        Logger.getLogger(LOGGER).info(credentialsProvider.getCredentials().getAWSAccessKeyId());
        Logger.getLogger(LOGGER).info(credentialsProvider.getCredentials().getAWSSecretKey());

        //Get the S3 instance
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();

         //See if the bucket exists and create one if it doesn't exist
        //Please refer to definition of "Key" from AWS
        if(TextUtils.isEmpty(amazonS3.
                getObject("trial-input", "testfolder/2_source_43410_121536.mp4").getBucketName())){

              /************************************************
               *****STEP TWO--SEE IF YOU HAVE THE BUCKETS*****
               *********************************************/
            //Step 1. Create input bucket
            amazonS3.createBucket("trial-input");
            //Step 2. Create an output bucket
            amazonS3.createBucket("trial-output");
            //Step 3. Upload your file to the S3 bucket
            amazonS3.putObject(
                    new PutObjectRequest("trial-input", "testfolder/2_source_43410_121536.mp4",
                            new File("/Users/kaltadesse/Downloads/2_source_43410_121536.mp4")).
                            withCannedAcl(CannedAccessControlList.PublicReadWrite));

        } else { }


        // AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.defaultClient();

          /*****************************************************************
           * ****************** STEP THREE -- CREATE THE PIPELINE **********
           ***************************************************************/
        AmazonElasticTranscoder elasticTranscoder =
                AmazonElasticTranscoderClientBuilder.standard().withCredentials(
                        new ClasspathPropertiesFileCredentialsProvider()).build();

        //elasticTranscoder.
        //See if our pipeline exists
        for(Pipeline ppl: elasticTranscoder.listPipelines().getPipelines()) {
            if ("kal-new-pipeline".equalsIgnoreCase(ppl.getName())) {
                //Pipe line exists
                continue;
            } else {
                //This means the pipe line exists and we dont need to re-create it.
                CreatePipelineRequest createPipelineRequest = new CreatePipelineRequest();
                //
                createPipelineRequest.setName("kal-new-pipeline");

                //Creating notifications
                Notifications notifications = new Notifications();
                notifications.setCompleted("arn:aws:sns:us-east-1:848878432980:Completed");
                notifications.setError("arn:aws:sns:us-east-1:848878432980:Errors");
                notifications.setProgressing("arn:aws:sns:us-east-1:848878432980:Progressing");
                notifications.setWarning("arn:aws:sns:us-east-1:848878432980:Warning");
                //
                createPipelineRequest.setNotifications(notifications);
                //
                createPipelineRequest.setInputBucket("trial-input");
                //
                createPipelineRequest.setOutputBucket("trial-output");
                //
                createPipelineRequest.setRole("arn:aws:iam::848878432980:role/Elastic_Transcoder_Default_Role");
                AmazonElasticTranscoderClientBuilder.defaultClient().createPipeline(createPipelineRequest);
                break;
            }
        }

           /*********************************************
            * ****STEP FOUR -- CREATE THE PRESETS*******
            *******************************************/
        //A new list to carry the new or presets that have not been created yet
        ListPresetsRequest listPresetsRequest = new ListPresetsRequest();

        //This gives all the paginated presets
        listPresetsRequest.setPageToken(elasticTranscoder.listPresets().getNextPageToken());
        List<Preset> unfilteredList = elasticTranscoder.listPresets(listPresetsRequest).getPresets();

        //This gives the first 50 lists
        unfilteredList.addAll(elasticTranscoder.listPresets().getPresets());
        //
        List<Preset> filteredList = new ArrayList<>();

        //Filter through the already existing presets
        for(Preset customPreset: Presets.getCustomPresets()){
            filteredList.add(unfilteredList.stream().filter(preset
                    -> preset.getName().equals(customPreset.getName())).findAny().orElse(customPreset));
        }

        //TODO:: this can be part of the above for loop, just to be more clear--let it be here, Change as you desire.
        for(Preset customPreset : filteredList){
            switch (customPreset.getName().toString()){
                //See if we have the preset already created
                case Constants.FIRST_PRESET_NAME:
                case Constants.SECOND_PRESET_NAME:
                case Constants.THIRD_PRESET_NAME:
                    Logger.getLogger(LOGGER).info("You already created \t \t :" + customPreset.getName());
                    break;
                    default:
                        Logger.getLogger(LOGGER).info("Creating....\t \t " + customPreset.getName());
                        createNewPreset(customPreset);
                        break;
            }
        }
      /*  BasicAWSCredentials creds = new BasicAWSCredentials(credentialsProvider.getCredentials().getAWSAccessKeyId(),
                credentialsProvider.getCredentials().getAWSSecretKey());

        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard().withRegion(amazonS3.getRegionName())
                        .withCredentials(new AWSStaticCredentialsProvider(creds)));

        // Create SQS connection
        SQSConnection sqsConnection = connectionFactory.createConnection();
        Session session = sqsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //session.createQueue("kal-");
        //ueue queue = session.createQueue("arn:aws:sqs:us-east-1:848878432980:kal-new");
        //Start listening for message.


        AmazonSQSMessagingClientWrapper client = sqsConnection.getWrappedAmazonSQSClient();

        CreateQueueResult createQueueResult = null;
       // Create an SQS queue named MyQueue, if it doesn't already exist

        boolean isQ = client.queueExists("MyQueue");
        if (isQ) {
            createQueueResult = client.createQueue("MyQueue");
        }

        if(createQueueResult != null){}*/

        new AsyncMessageReciever().startRecievingMessage(args);
    }


    @Override
    public void onMessage(Message message) {

    }

    private static void createNewPreset(Preset customPreset) {
        //Create new set of presets
        //Presets.createPresets(filteredList);
        CreatePresetRequest createPresetRequest = new CreatePresetRequest();
        createPresetRequest.setName(customPreset.getName());
        //createPresetRequest.set(filteredList.get(0).getId());
        createPresetRequest.setContainer(customPreset.getContainer());
        createPresetRequest.setDescription(customPreset.getDescription());
        createPresetRequest.setAudio(customPreset.getAudio());
        createPresetRequest.setVideo(customPreset.getVideo());
        createPresetRequest.setThumbnails(customPreset.getThumbnails());
        //new CreatePresetRequest().setAudio();
        //AmazonElasticTranscoderClientBuilder.defaultClient().createPreset(createPresetRequest);
    }
}
