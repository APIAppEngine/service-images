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
import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mnimer on 6/5/14.
 */
public class ImageBorderTests extends FilterTestBase implements Serializable
{

    @Test
    public void testBorderByIdRESTGet() throws Exception {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/image/modify/" + documentId + "/border.jpg")
                                .param("color", "blue")
                                .param("thickness", "50")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1507837, result.getResponse().getContentLength());
        FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/border-get.jpg"), result.getResponse().getContentAsByteArray());
        saveFileToLocalDisk("border-get.jpg", result.getResponse().getContentAsByteArray());
    }


    @Test
    public void testBorderByIdRESTPost() throws Exception {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/modify/border.jpg")
                                .file(file)
                                .param("color", "red")
                                .param("thickness", "50")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1487047, result.getResponse().getContentLength());
        saveFileToLocalDisk("border-post.jpg", result.getResponse().getContentAsByteArray());
    }
}
