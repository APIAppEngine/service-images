package filters;

import apiserver.ImageMicroServiceApplication;
import apiserver.services.cache.gateway.CacheGateway;
import apiserver.services.images.gateways.filters.ApiImageFilterBlurGateway;
import apiserver.services.images.gateways.filters.ApiImageFilterBoxBlurGateway;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Created by mnimer on 5/18/14.
 */
public abstract class UnitTestConfiguration
{
    private static ConfigurableApplicationContext context;

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
}
