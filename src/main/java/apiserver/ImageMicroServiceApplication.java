package apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by mnimer on 5/5/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableWebMvc
@ImportResource( "image-flow-config.xml" )
public class ImageMicroServiceApplication implements EmbeddedServletContainerCustomizer
{

    public static void main(String[] args)
    {
        SpringApplication.run(ImageMicroServiceApplication.class, args);
    }



    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer)
    {
        configurableEmbeddedServletContainer.setPort(9002);
    }
}
