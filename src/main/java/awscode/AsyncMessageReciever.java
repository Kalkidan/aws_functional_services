package awscode;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.profile.path.cred.CredentialsDefaultLocationProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;

import javax.jms.*;
import java.util.concurrent.TimeUnit;

public class AsyncMessageReciever implements MessageListener{


    public void startRecievingMessage(String[] args) throws JMSException, InterruptedException {

        //ExampleConfiguration config = ExampleConfiguration.parseConfig("AsyncMessageReceiver", args);

        //ExampleCommon.setupLogging();

        // Create the connection factory based on the config
      /*  SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard().withRegion(String.valueOf(Region.getRegion(Regions.US_EAST_2)))
                        .withCredentials(new ClasspathPropertiesFileCredentialsProvider())
        );

        // Create the connection
        SQSConnection connection = connectionFactory.createConnection();

        // Create the queue if needed
        //ExampleCommon.ensureQueueExists(connection, config.getQueueName());

        // Create the session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer( session.createQueue( "kal-second-queue" ) );

        ReceiverCallback callback = new ReceiverCallback();
        consumer.setMessageListener( callback );

        // No messages are processed until this is called
        connection.start();

        callback.waitForOneMinuteOfSilence();
        System.out.println( "Returning after one minute of silence" );

        // Close the connection. This closes the session automatically
        connection.close();
        System.out.println( "Connection closed" );*/


        // Create the connection factory based on the config

        /*AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();
        BasicAWSCredentials creds = new BasicAWSCredentials(credentialsProvider.getCredentials().getAWSAccessKeyId(),
                credentialsProvider.getCredentials().getAWSSecretKey());

        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard()
                        .withRegion(amazonS3.getRegionName())
                        .withCredentials(new AWSStaticCredentialsProvider(creds))
        );

        // Create the connection
        SQSConnection connection = connectionFactory.createConnection();

        // Create the queue if needed
        //ExampleCommon.ensureQueueExists(connection, config.getQueueName());

        // Create the session
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer( session.createQueue( "kat" ) );

        ReceiverCallback callback = new ReceiverCallback();
        consumer.setMessageListener( callback );

        // No messages are processed until this is called
        connection.start();

        callback.waitForOneMinuteOfSilence();
        System.out.println( "Returning after one minute of silence" );

        // Close the connection. This closes the session automatically
        connection.close();
        System.out.println( "Connection closed" );*/

        AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();

        BasicAWSCredentials creds = new BasicAWSCredentials(credentialsProvider.getCredentials().getAWSAccessKeyId(),
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
       //Create an SQS queue named MyQueue, if it doesn't already exist

        boolean isQ = client.queueExists("MyQueue");
        if (isQ) {
            createQueueResult = client.createQueue("MyQueue");
        }

        if(createQueueResult != null){}



    }

    @Override
    public void onMessage(Message message) {

    }

    private static class ReceiverCallback implements MessageListener {
        // Used to listen for message silence
        private volatile long timeOfLastMessage = System.nanoTime();

        public void waitForOneMinuteOfSilence() throws InterruptedException {
            for(;;) {
                long timeSinceLastMessage = System.nanoTime() - timeOfLastMessage;
                long remainingTillOneMinuteOfSilence =
                        TimeUnit.MINUTES.toNanos(1) - timeSinceLastMessage;
                if( remainingTillOneMinuteOfSilence < 0 ) {
                    break;
                }
                TimeUnit.NANOSECONDS.sleep(remainingTillOneMinuteOfSilence);
            }
        }


        @Override
        public void onMessage(Message message) {
            try {
                ExampleCommon.handleMessage(message);
                message.acknowledge();
                System.out.println( "Acknowledged message " + message.getJMSMessageID() );
                timeOfLastMessage = System.nanoTime();
            } catch (JMSException e) {
                System.err.println( "Error processing message: " + e.getMessage() );
                e.printStackTrace();
            }
        }
    }
}
