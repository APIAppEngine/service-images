package apiserver.services.image.controllers.manipulations;

import apiserver.services.image.controllers.FilterTestBase;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 6/5/14.
 */
public class ImageResizeTests extends FilterTestBase
{

    @Test
    public void testResizeByIdRESTGet() throws Exception {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/image/modify/" + documentId + "/resize.jpg")
                                .param("width", "500")
                                .param("height", "300")
                                .param("interpolation", "bicubic")
                                .param("scaleToFit", "false")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(45318, result.getResponse().getContentLength());
        saveFileToLocalDisk("resize-500x300-get.jpg", result.getResponse().getContentAsByteArray());
    }


    @Test
    public void testResizeByIdRESTPost() throws Exception {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/modify/resize.jpg")
                                .file(file)
                                .param("width", "500")
                                .param("height", "300")
                                .param("interpolation", "bicubic")
                                .param("scaleToFit", "false")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(45318, result.getResponse().getContentLength());
        saveFileToLocalDisk("resize-500x300-post.jpg", result.getResponse().getContentAsByteArray());
    }

}
