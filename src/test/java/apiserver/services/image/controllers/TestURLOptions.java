package apiserver.services.image.controllers;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 6/4/14.
 */
public class TestURLOptions extends FilterTestBase
{

    @Before
    public void setup() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        super.setup();
    }

    @After
    public void tearDown() throws InterruptedException, ExecutionException
    {
        super.tearDown();
    }


    @Test
    public void testJpgToJpgRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");


        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/blur.jpg").file(file))
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1169274, result.getResponse().getContentLength());

        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/blur-post.jpg"), result.getResponse().getContentAsByteArray());

    }

    @Test
    public void testJpgToPngRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");


        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/blur.png").file(file))
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/png"))
                .andReturn();

        Assert.assertEquals(28059051, result.getResponse().getContentLength());

        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/blur-post.png"), result.getResponse().getContentAsByteArray());

    }

    @Ignore
    @Test
    public void testJpgToTiffRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");


        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/blur.tiff").file(file))
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/tiff"))
                .andReturn();

        Assert.assertEquals(1169274, result.getResponse().getContentLength());

        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/blur-post.tiff"), result.getResponse().getContentAsByteArray());

    }


    @Ignore
    @Test
    public void testTifToTiffRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");


        MockMultipartFile file = new MockMultipartFile("file", "minecraft-screenshot.tif", "image/tiff", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/blur.tiff").file(file))
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/tiff"))
                .andReturn();

        Assert.assertEquals(1169274, result.getResponse().getContentLength());

        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/blur-post.tiff"), result.getResponse().getContentAsByteArray());

    }

    @Test
    public void testJpgToGifRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");


        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/blur.gif").file(file))
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/gif"))
                .andReturn();

        Assert.assertEquals(2515577, result.getResponse().getContentLength());

        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/blur-post.gif"), result.getResponse().getContentAsByteArray());

    }

    @Test
    public void testJBadContentTypeRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");


        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/blur.mp3").file(file))
                .andExpect(status().is(415))
                .andReturn();

    }

}
