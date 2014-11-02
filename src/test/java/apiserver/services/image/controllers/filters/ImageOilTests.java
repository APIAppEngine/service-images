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

import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.cache.DocumentJob;
import apiserver.services.cache.gateway.CacheGateway;
import apiserver.services.cache.gateway.jobs.DeleteDocumentJob;
import apiserver.services.cache.gateway.jobs.UploadDocumentJob;
import apiserver.services.cache.model.Document;
import apiserver.services.image.controllers.FilterTestBase;
import apiserver.services.images.gateways.filters.ApiImageFilterOilGateway;
import apiserver.services.images.gateways.jobs.filters.OilJob;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
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

public class ImageOilTests extends FilterTestBase
{

    @Test
    public void testOilByIdRESTGet() throws Exception {
        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(get(rootUrl + "/image/filter/" + documentId + "/motionblur.jpg")
                                .param("level", "3")
                                .param("range", "256")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1507837, result.getResponse().getContentLength());
        saveFileToLocalDisk("oil-get.jpg", result.getResponse().getContentAsByteArray());
    }


    @Test
    public void testOilByIdRESTPost() throws Exception {
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("IMG_5932.JPG");

        MockMultipartFile file = new MockMultipartFile("file", "IMG_5932.JPG", "image/jpeg", fileStream);

        MvcResult result = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build()
                .perform(fileUpload(rootUrl + "/image/filter/motionblur")
                                .file(file)
                                .param("format", "jpg")
                                .param("level", "3")
                                .param("range", "256")
                )
                .andExpect(status().is(200))
                .andExpect(content().contentType("image/jpeg"))
                .andReturn();

        Assert.assertEquals(1507837, result.getResponse().getContentLength());
        saveFileToLocalDisk("oil-post.jpg", result.getResponse().getContentAsByteArray());
    }
}
