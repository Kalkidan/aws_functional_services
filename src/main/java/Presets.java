import com.amazonaws.services.elastictranscoder.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Presets {


    public static List<Preset> getCustomPresets(){

        List<Preset> presetList = new ArrayList<>();
        presetList.add(firstPreset());
        //add(secondPreset());
        //add(thirdPreset());
        return presetList;
    }

    private static Preset firstPreset() {
        Preset firstPreset = new Preset();
        //
        firstPreset.setId("123456-jgjg5");
        //TODO:: we will have to find a way for this to make sense when filtering
        firstPreset.setName(Constants.FIRST_PRESET_NAME);
        //
        firstPreset.setDescription("This is a description for the first preset!!");
        //
        firstPreset.setContainer("mp4");
        //
        firstPreset.setVideo(firsVideoParameters());
        //
        firstPreset.setAudio(firstAudioParameters());
        //
        firstPreset.setThumbnails(thumbNailParameter());
        return firstPreset;
    }

    private static Thumbnails thumbNailParameter(){
        Thumbnails thumbnails = new Thumbnails();
        //thumbnails.setAspectRatio("16:9");
        thumbnails.setFormat("png");
        //
        thumbnails.setInterval("60");
        //
        thumbnails.setSizingPolicy("ShrinkToFit");
        //
        thumbnails.setPaddingPolicy("NoPad");
        //
        thumbnails.setMaxWidth("192");
        //
        thumbnails.setMaxHeight("144");
        return thumbnails;
    }

    private static AudioParameters firstAudioParameters() {
        AudioParameters audioParameters = new AudioParameters();
        //
        audioParameters.setBitRate("128");
        //
        audioParameters.setCodec("AAC");
        //
        audioParameters.setSampleRate("44100");
        //
        audioParameters.setChannels("2");

        //audio codec start
        AudioCodecOptions audioCodecOptions = new AudioCodecOptions();
        //audioCodecOptions.setBitDepth("");
        //audioCodecOptions.setBitOrder("");
        audioCodecOptions.setProfile("AAC-LC");
        //audioCodecOptions.setSigned();
        audioParameters.setCodecOptions(audioCodecOptions);
        return audioParameters;
    }

    private static VideoParameters firsVideoParameters() {
        //
        VideoParameters videoParameters = new VideoParameters();
        //
        videoParameters.setCodec("H.264");
        //
        videoParameters.setCodecOptions(firstVideoCodecOptions());
        //
        videoParameters.setDisplayAspectRatio("16:9");
        //
        videoParameters.setBitRate("600");
        //
        videoParameters.setMaxHeight("1280");
        //
        videoParameters.setMaxWidth("720");
        //
        videoParameters.setKeyframesMaxDist("90");
        //
        videoParameters.setFrameRate("30");
        //
        videoParameters.setSizingPolicy("Fill");
        //
        videoParameters.setPaddingPolicy("NoPad");
        //
        videoParameters.setFixedGOP("true");
        //
        return videoParameters;
    }

    private static Map<String,String> firstVideoCodecOptions() {
        /***
         * "CodecOptions":{
         "            Profile":"baseline|main|high|0|1|2|3",
         "            Level":"1|1b|1.1|1.2|1.3|2|2.1|2.2|3|3.1|3.2|4|4.1",
         "            MaxReferenceFrames":"maximum number of reference frames",
         "            MaxBitRate":"maximum bit rate",
         "            BufferSize":"maximum buffer size",
         "            InterlacedMode":"Progressive|TopFirst|BottomFirst|Auto",
         "            ColorSpaceConversion":"None|Bt709ToBt601|Bt601ToBt709|Auto",
         "            ChromaSubsampling":"yuv420p|yuv422p",
         "            LoopCount":"Infinite|[0,100]"
         },
         *
         * */
        return new HashMap<String, String>(){{

            //For our purposes, this is what we need
            put("Profile", "baseline");
            //
            put("Level", "3");
            //
            put("MaxReferenceFrames", "3");
            //
            put("MaxBitRate", "600");
            //
            put("BufferSize", "3600");
            //
            put("InterlacedMode", "Progressive");
            //
            put("ColorSpaceConversion", "None");


        }};
    }

    private static Preset secondPreset(){
        Preset secondPreset = new Preset();
        //
        secondPreset.setVideo(secondVideoParameters());
        //
        secondPreset.setAudio(secondAudioParameters());
        //
        secondPreset.setThumbnails(thumbNailParameter());
        return secondPreset;
    }

    private static VideoParameters secondVideoParameters(){
        VideoParameters videoParameters = new VideoParameters();
        //
        videoParameters.setAspectRatio("");
        //
        videoParameters.setBitRate("");
        //
        videoParameters.setCodec("");
        //
        videoParameters.setMaxHeight("");
        //
        videoParameters.setMaxWidth("");
        //
        return videoParameters;
    }

    private static AudioParameters secondAudioParameters() {
        AudioParameters audioParameters = new AudioParameters();
        //
        audioParameters.setAudioPackingMode("");
        //
        audioParameters.setBitRate("");
        //
        audioParameters.setCodec("");
        //
        audioParameters.setSampleRate("");
        //
        audioParameters.setChannels("");
        //
        return audioParameters;
    }

    private static Preset thirdPreset(){
        Preset thirdPreset = new Preset();
        //
        thirdPreset.setVideo(thirdVideoParameters());
        //
        thirdPreset.setAudio(thirdAudioParameters());
        //
        thirdPreset.setThumbnails(thumbNailParameter());
        //
        return thirdPreset;

    }

    private static AudioParameters thirdAudioParameters() {
        AudioParameters audioParameters = new AudioParameters();
        audioParameters.setAudioPackingMode("");
        audioParameters.setBitRate("");
        audioParameters.setCodec("");
        audioParameters.setSampleRate("");
        audioParameters.setChannels("");
        return audioParameters;
    }

    private static VideoParameters thirdVideoParameters() {
        VideoParameters videoParameters = new VideoParameters();
        videoParameters.setAspectRatio("");
        videoParameters.setBitRate("");
        videoParameters.setCodec("");
        videoParameters.setMaxHeight("");
        videoParameters.setMaxWidth("");
        return videoParameters;
    }
}
