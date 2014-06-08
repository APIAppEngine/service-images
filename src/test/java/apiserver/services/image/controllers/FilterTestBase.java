package apiserver.services.image.controllers;

import apiserver.ImageMicroServiceApplication;
import apiserver.services.cache.DocumentJob;
import apiserver.services.cache.gateway.CacheGateway;
import apiserver.services.cache.gateway.jobs.DeleteDocumentJob;
import apiserver.services.cache.gateway.jobs.UploadDocumentJob;
import apiserver.services.cache.model.Document;
import apiserver.services.images.gateways.filters.ApiImageFilterBlurGateway;
import apiserver.services.images.gateways.filters.ApiImageFilterBoxBlurGateway;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mnimer on 5/18/14.
 */
@WebAppConfiguration
@ContextConfiguration(classes = ImageMicroServiceApplication.class)
public abstract class FilterTestBase
{
    public static ConfigurableApplicationContext context;

    @Autowired
    public WebApplicationContext wac;
    public String documentId = null;
    public TestRestTemplate restTemplate;


    //@Value("${server.port}")
    public String port;

    //@Value("${defaultReplyTimeout}")
    public Integer defaultTimeout;

    public String rootUrl;


    //@Qualifier("imageBoxBlurFilterChannelApiGateway")
    public ApiImageFilterBoxBlurGateway imageBoxBlurFilterGateway;

    //@Qualifier("imageBlurFilterChannelApiGateway")
    public ApiImageFilterBlurGateway imageBlurFilterGateway;


    //@Qualifier("documentAddGateway")
    public CacheGateway documentGateway;

    //@Qualifier("documentDeleteGateway")
    public CacheGateway documentDeleteGateway;


    @BeforeClass
    public static void start() throws Exception
    {
        context = SpringApplication.run(ImageMicroServiceApplication.class);
    }


    @AfterClass
    public static void stop()
    {
        if (context != null)
        {
            context.close();
        }
    }


    public void setup() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        // manually load autowired
        //properties
        port = context.getEnvironment().getProperty("server.port");
        defaultTimeout = new Integer(context.getEnvironment().getProperty("defaultReplyTimeout"));
        rootUrl = "http://localhost:" +port;

        // beans
        imageBlurFilterGateway = context.getBean("imageBlurFilterChannelApiGateway", ApiImageFilterBlurGateway.class);
        imageBoxBlurFilterGateway = context.getBean("imageBoxBlurFilterChannelApiGateway", ApiImageFilterBoxBlurGateway.class);
        documentGateway = context.getBean("documentAddGateway", CacheGateway.class);
        documentDeleteGateway = context.getBean("documentDeleteGateway", CacheGateway.class);
    }


    public void tearDown() throws InterruptedException, ExecutionException
    {
    }



    @Before
    public void cacheDocument() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        setup();
        restTemplate = new TestRestTemplate();
        File file = new File(  this.getClass().getClassLoader().getResource("IMG_5932.JPG").toURI()  );

        UploadDocumentJob job = new UploadDocumentJob(file);
        job.setDocument(new Document(file));
        Future<DocumentJob> doc = documentGateway.addDocument(job);
        documentId = ((DocumentJob)doc.get()).getDocument().getId();
    }

    @After
    public void clearCache() throws InterruptedException, ExecutionException
    {
        tearDown();
        DeleteDocumentJob job = new DeleteDocumentJob();
        job.setDocumentId(documentId);
        documentDeleteGateway.deleteDocument(job).get();
    }


    protected void saveFileToLocalDisk(String fileName, byte[] fileBytes) {

        try {

            File file = new File("/Users/mnimer/Desktop/" + fileName);

            if (file.exists()) {
                file.delete();
            }

            FileUtils.writeByteArrayToFile(file, fileBytes);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
