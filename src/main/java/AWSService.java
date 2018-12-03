import javax.jms.JMSException;

/**
 * This work is based on research that the aws java sdk documetation
 * presents. And may not be the only way to do it.

 */

public class AWSService {

    public static void main(String[] args) throws JMSException, InterruptedException {
        //Start Transcoding Job
        new Transcoder().launchTranscoder(args);
    }
}