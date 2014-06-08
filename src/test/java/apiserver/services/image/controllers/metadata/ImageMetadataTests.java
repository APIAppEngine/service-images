package apiserver.services.image.controllers.metadata;

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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: mnimer
 * Date: 11/4/12
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class ImageMetadataTests
{

    @Test
    public void testGetMetadataByIdRESTGet() throws Exception {
        throw new RuntimeException("Not implemented yet");
    }


    @Test
    public void testGetMetadataByIdRESTPost() throws Exception {
        throw new RuntimeException("Not implemented yet");
    }


    @Test
    public void testStripMetadataByIdRESTGet() throws Exception {
        throw new RuntimeException("Not implemented yet");
    }


    @Test
    public void testStripMetadataByIdRESTPost() throws Exception {
        throw new RuntimeException("Not implemented yet");
    }
}
