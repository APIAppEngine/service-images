package apiserver.services.image.controllers.filters;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.services.image.controllers.FilterTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: mikenimer
 * Date: 7/7/13
 */
public class ImageBumpTests extends FilterTestBase
{

    @Test
    public void testBumpByIdRESTGet() throws Exception
    {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/api/image/filter/" +documentId +"/bump.jpg")
                                .param("edgeAction", "1")
                                .param("useAlpha", "true")
                                .param("matrix", "-1.0,-1.0,0.0,-1.0,1.0,1.0,0.0,1.0,1.0")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(2631227, result.getResponse().getContentLength());
        saveFileToLocalDisk("bump-get.jpg", result.getResponse().getContentAsByteArray());
    }


    @Test
    public void testBumpByIdRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/api/image/filter/bump")
                                .file(file)
                                .param("format", "jpg")
                                .param("edgeAction", "1")
                                .param("useAlpha", "true")
                                .param("matrix", "-1.0,-1.0,0.0,-1.0,1.0,1.0,0.0,1.0,1.0")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(2631227, result.getResponse().getContentLength());
        saveFileToLocalDisk("bump-post.jpg", result.getResponse().getContentAsByteArray());
    }

}
