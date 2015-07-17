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

import apiserver.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.image.controllers.FilterTestBase;
import apiserver.services.images.gateways.filters.ApiImageFilterMedianGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: mikenimer
 * Date: 7/7/13
 */
public class ImageMedianTests extends FilterTestBase
{

    @Test
    public void testMedianByIdRESTGet() throws Exception {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/api/image/filter/" + documentId + "/median.jpg"))
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1186725, result.getResponse().getContentLength());
        saveFileToLocalDisk("median-get.jpg", result.getResponse().getContentAsByteArray());
    }


    @Test
    public void testMedianByIdRESTPost() throws Exception {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/api/image/filter/median")
                                .file(file)
                                .param("format", "jpg")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1186725, result.getResponse().getContentLength());
        saveFileToLocalDisk("median-post.jpg", result.getResponse().getContentAsByteArray());
    }
}
