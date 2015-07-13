package apiserver;


import apiserver.filters.MashapeAuthFilter;
import apiserver.filters.MetricsFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.util.EnumSet;

/**
 * Created by mnimer on 5/5/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableWebMvc
@ImportResource( {"image-flow-config.xml","cache-flow-config.xml"} )
public class ImageMicroServiceApplication extends DelegatingWebMvcConfiguration
{

    public static void main(String[] args)
    {
        SpringApplication.run(ImageMicroServiceApplication.class, args);
    }

    @Override
    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


/**
 
    @Bean
    public ServletContextTemplateResolver templateResolver() {
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
        resolver.setPrefix("/resources/views/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(false);
        return resolver;

    }

    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        return engine;
    }
 **/



    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("100MB");
        factory.setMaxRequestSize("110MB");
        return factory.createMultipartConfig();
    }



/**
    @Value("${mashape.key}")
    private String mashapeKey = null;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MashapeAuthFilter(mashapeKey));
        registration.setFilter(new MetricsFilter());

        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }
    **/
}
