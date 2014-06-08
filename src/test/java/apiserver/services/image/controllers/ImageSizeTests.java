package apiserver.services.image.controllers;

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

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


/**
 * User: mikenimer
 * Date: 7/7/13
 */

public class ImageSizeTests extends FilterTestBase
{
    private MockMvc mockMvc;

    @Before
    public void setupMock() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build();
    }

    @Test
    public void testSizeByIdRESTGet() throws Exception
    {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/image/info/" + documentId + "/size"))
                .andExpect(status().is(200))
                .andExpect(request().asyncStarted())
                .andReturn();

        ResponseEntity asyncResult = (ResponseEntity)result.getAsyncResult();
        Assert.assertTrue(asyncResult.getBody() instanceof Map);
        Assert.assertEquals(3000, ((Map)asyncResult.getBody()).get("height") );
        Assert.assertEquals(4000, ((Map)asyncResult.getBody()).get("width") );
    }


    @Test
    public void testSizeByIdRESTPost() throws Exception
    {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");
        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/info/size").file(file))
                .andExpect(status().is(200))
                .andExpect(request().asyncStarted())
                .andReturn();

        Map asyncResult = (Map)result.getAsyncResult();
        Assert.assertTrue(asyncResult instanceof Map);
        Assert.assertEquals(3000, asyncResult.get("height") );
        Assert.assertEquals(4000, asyncResult.get("width") );
    }

}
