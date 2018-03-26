import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClientBuilder;
import com.amazonaws.services.elastictranscoder.model.*;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.http.util.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Transcoder {

    public static final String LOGGER = Transcoder.class.getSimpleName();

    public static void launchTranscoder(){

        /************************************************************
         * FIRST STEP IN USING ASW ELASTIC TRANS CODER
         * Remember to set up credentials @AWSCredentials.properties
         ************************************************************/
        AWSCredentialsProvider credentialsProvider =    new ClasspathPropertiesFileCredentialsProvider();
        //Get the creds path and values
        Logger.getLogger(LOGGER).info(credentialsProvider.toString());
        Logger.getLogger(LOGGER).info(credentialsProvider.getCredentials().getAWSAccessKeyId());
        Logger.getLogger(LOGGER).info(credentialsProvider.getCredentials().getAWSSecretKey());

         //See if the bucket exists and create one if it doesn't exist
        //Please refer to definition of "Key" from AWS
        if(TextUtils.isEmpty(AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build().
                getObject("trial-input", "testfolder/2_source_43410_121536.mp4").getBucketName())){

            /************************************************
             *****STEP TWO--SEE IF YOU HAVE THE BUCKETS*****
             *********************************************/
            //Step 1. Create input bucket
            AmazonS3ClientBuilder.defaultClient().createBucket("trial-input");
            //Step 2. Create an output bucket
            AmazonS3ClientBuilder.defaultClient().createBucket("trial-output");
            //Step 3. Upload your file to the S3 bucket
            AmazonS3ClientBuilder.defaultClient().putObject(
                    new PutObjectRequest("trial-input", "testfolder/2_source_43410_121536.mp4",
                            new File("/Users/kaltadesse/Downloads/2_source_43410_121536.mp4")).
                            withCannedAcl(CannedAccessControlList.PublicReadWrite));

        } else { }

        /*****************************************************************
         * ******************STEP THREE -- CREATE THE PIPELINE **********
         ***************************************************************/
        //See if our pipeline exists
        for(Pipeline ppl: AmazonElasticTranscoderClientBuilder.defaultClient().listPipelines().getPipelines()) {
            if ("new-pipeline".equalsIgnoreCase(ppl.getName())) {
                //Pipe line exists
                break;
            } else {
                //This means the pipe line exists and we dont need to re-create it.
                CreatePipelineRequest createPipelineRequest = new CreatePipelineRequest();
                createPipelineRequest.setName("ENTER YOUR NAME");
                createPipelineRequest.setInputBucket("trial-input");
                createPipelineRequest.setOutputBucket("trial-output");
                createPipelineRequest.setRole("THIS IS DIFFERENT FOR USERS MAKE SURE YOU ENTER A CORRECT ONE");
                //AmazonElasticTranscoderClientBuilder.defaultClient().createPipeline(createPipelineRequest);
            }
        }

        /*********************************************
         * ****STEP FOUR -- CREATE THE PRESETS*******
         *******************************************/
        //A new list to carry the new or presets that have not been created yet
        ListPresetsRequest listPresetsRequest = new ListPresetsRequest();

        //This gives all the paginated presets
        listPresetsRequest.setPageToken(AmazonElasticTranscoderClientBuilder.defaultClient().listPresets().getNextPageToken());
        List<Preset> unfilteredList = AmazonElasticTranscoderClientBuilder.defaultClient().listPresets(listPresetsRequest).getPresets();

        //This gives the first 50 lists
        unfilteredList.addAll(AmazonElasticTranscoderClientBuilder.defaultClient().listPresets().getPresets());
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
