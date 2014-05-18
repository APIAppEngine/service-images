package filters;

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

import apiserver.ImageMicroServiceApplication;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.cache.DocumentJob;
import apiserver.services.cache.gateway.CacheGateway;
import apiserver.services.cache.gateway.jobs.DeleteDocumentJob;
import apiserver.services.cache.gateway.jobs.UploadDocumentJob;
import apiserver.services.cache.model.Document;
import apiserver.services.images.gateways.filters.ApiImageFilterBoxBlurGateway;
import apiserver.services.images.gateways.jobs.filters.BoxBlurJob;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mikenimer
 * Date: 7/7/13
 */
@IntegrationTest
public class ImageBoxBlurTests extends UnitTestConfiguration
{
    File file = null;
    String documentId = null;
    RestTemplate restTemplate;

    @Before
    public void setup() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        super.setup();
        restTemplate = new TestRestTemplate();
        file = new File(this.getClass().getClassLoader().getResource("IMG_5932.JPG").toURI());

        UploadDocumentJob job = new UploadDocumentJob(file);
        job.setDocument(new Document(file));
        Future<DocumentJob> doc = documentGateway.addDocument(job);
        documentId = ((DocumentJob) doc.get()).getDocument().getId();
    }


    @After
    public void tearDown() throws InterruptedException, ExecutionException
    {
        super.tearDown();
        DeleteDocumentJob job = new DeleteDocumentJob();
        job.setDocumentId(documentId);
        documentDeleteGateway.deleteDocument(job).get();
    }


    @Test
    public void testREST() throws Exception
    {
        Map results = restTemplate.getForObject(rootUrl +"/images/test", Map.class);
        //ResponseEntity testEntity = template.getForEntity(root +"/images/test", Map.class);
        Assert.assertTrue(results.get("status").equals("ok"));
    }


    @Test
    public void testBoxBlurByIdREST() throws Exception
    {
        ResponseEntity entity = restTemplate.getForEntity(rootUrl +"/image/filter/" +documentId +"/boxblur.jpg", byte[].class );
        Assert.assertEquals("Invalid image bytes",  25551255, ((byte[])entity.getBody()).length);

        //FileUtils.writeByteArrayToFile(new File("/Users/mnimer/Desktop/boxblur.jpg"), (byte[])entity.getBody());
    }


    @Test
    public void testBoxBlurById() throws TimeoutException, ExecutionException, InterruptedException, IOException
    {

        BoxBlurJob args = new BoxBlurJob();
        args.setDocumentId(documentId);
        args.setHRadius(2);
        args.setVRadius(2);
        args.setIterations(1);
        args.setPreMultiplyAlpha(true);


        Future<Map> imageFuture = imageBoxBlurFilterGateway.imageBoxBlurFilter(args);
        BoxBlurJob payload = (BoxBlurJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);
        Assert.assertTrue("NULL Payload", payload != null );

        BufferedImage bufferedImage = payload.getBufferedImage();
        Assert.assertTrue("NULL BufferedImage in payload", bufferedImage != null );

        String contentType = payload.getDocument().getContentType().contentType;
        Assert.assertEquals("image/jpeg",contentType);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, Boolean.FALSE);
        Assert.assertEquals("Invalid image bytes",  25551255, result.getBody().length);
    }


    @Test
    public void testBoxBlurBase64ByFile() throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        BoxBlurJob args = new BoxBlurJob();
        args.setDocument(new Document(file));
        args.setHRadius(2);
        args.setVRadius(2);
        args.setIterations(1);
        args.setPreMultiplyAlpha(true);

        Future<Map> imageFuture = imageBoxBlurFilterGateway.imageBoxBlurFilter(args);
        BoxBlurJob payload = (BoxBlurJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);
        Assert.assertTrue("NULL Payload", payload != null );

        BufferedImage bufferedImage = payload.getBufferedImage();
        Assert.assertTrue("NULL BufferedImage in payload", bufferedImage != null );

        String contentType = payload.getDocument().getContentType().contentType;
        Assert.assertEquals("image/jpeg",contentType);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, Boolean.TRUE);
        Assert.assertEquals("Invalid image bytes",  34068340, result.getBody().length);
    }
}
