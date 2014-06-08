package apiserver.services.image.controllers.manipulations;

import apiserver.services.image.controllers.FilterTestBase;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 6/5/14.
 */
public class ImageTextTests extends FilterTestBase
{

    @Test
    public void testTextByIdRESTGet() throws Exception {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/image/modify/" + documentId + "/text.jpg")
                                .param("text", "Hello World!")
                                .param("color", "#ffffff")
                                .param("fontSize", "24px")
                                .param("fontStyle", "bold")
                                .param("angle", "0")
                                .param("x", "50")
                                .param("y", "50")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1169274, result.getResponse().getContentLength());
        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/text-get.jpg"), result.getResponse().getContentAsByteArray());
    }


    @Test
    public void testTextByIdRESTPost() throws Exception {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/modify/text.jpg")
                                .file(file)
                                .param("text", "Hello World!")
                                .param("color", "#ffffff")
                                .param("fontSize", "64px")
                                .param("fontStyle", "italic")
                                .param("angle", "0")
                                .param("x", "200")
                                .param("y", "200")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1512804, result.getResponse().getContentLength());
        saveFileToLocalDisk("text-post.jpg", result.getResponse().getContentAsByteArray());
    }

    @Ignore
    @Test
    public void testTextWithAngleByIdRESTPost() throws Exception {

        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/modify/text.jpg")
                                .file(file)
                                .param("text", "Hello World!")
                                .param("color", "#ffffff")
                                .param("fontSize", "64px")
                                .param("fontStyle", "italic")
                                .param("angle", "90")
                                .param("x", "200")
                                .param("y", "200")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        throw new RuntimeException("Not implemented yet");

        //Assert.assertEquals(1512804, result.getResponse().getContentLength());
        //saveFileToLocalDisk("textWithAngle-post.jpg", result.getResponse().getContentAsByteArray());
    }
}
