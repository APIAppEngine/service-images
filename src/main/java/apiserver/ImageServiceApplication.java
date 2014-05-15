package apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by mnimer on 5/5/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource( "flows/flow-config.xml" )
public class ImageServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ImageServiceApplication.class, args);
    }

}
