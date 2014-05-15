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

import apiserver.apis.v1_0.documents.DocumentJob;
import apiserver.apis.v1_0.documents.gateway.DocumentGateway;
import apiserver.apis.v1_0.documents.gateway.jobs.DeleteDocumentJob;
import apiserver.apis.v1_0.documents.gateway.jobs.UploadDocumentJob;
import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.filters.ApiImageFilterBumpGateway;
import apiserver.services.images.gateways.jobs.filters.BumpJob;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ImportResource("flows/filters/filterBump-flow.xml")
public class ImageBumpTests
{

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;

    @Autowired
    private ApiImageFilterBumpGateway imageBumpFilterGateway;


    @Qualifier("documentAddGateway")
    @Autowired(required = false)
    private DocumentGateway documentGateway;


    @Qualifier("documentDeleteGateway")
    @Autowired(required = false)
    private DocumentGateway documentDeleteGateway;


    String documentId = null;

    @Before
    public void setup() throws URISyntaxException, IOException, InterruptedException, ExecutionException
    {
        File file = new File(  ImageMotionBlurTests.class.getClassLoader().getResource("IMG_5932.JPG").toURI()  );

        UploadDocumentJob job = new UploadDocumentJob(file);
        job.setDocument(new Document(file));
        Future<DocumentJob> doc = documentGateway.addDocument(job);
        documentId = ((DocumentJob)doc.get()).getDocument().getId();
    }

    @After
    public void tearDown() throws InterruptedException, ExecutionException
    {
        DeleteDocumentJob job = new DeleteDocumentJob();
        job.setDocumentId(documentId);
        documentDeleteGateway.deleteDocument(job).get();
    }




    @Test
    public void testBumpByFile() throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        float[] matrix = new float[]{-1.0F,-1.0F,0.0F,-1.0F,1.0F,1.0F,0.0F,1.0F,1.0F};

        BumpJob args = new BumpJob();
        args.setDocumentId(documentId);
        args.setEdgeAction(1);
        args.setUseAlpha(true);
        args.setMatrix(matrix);

        Future<Map> imageFuture = imageBumpFilterGateway.imageBumpFilter(args);

        BumpJob payload = (BumpJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);
        Assert.assertTrue("NULL Payload", payload != null );

        BufferedImage bufferedImage = payload.getBufferedImage();
        Assert.assertTrue("NULL BufferedImage in payload", bufferedImage != null );

        String contentType = payload.getDocument().getContentType().contentType;
        Assert.assertEquals("image/jpeg",contentType);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, Boolean.FALSE);
        Assert.assertEquals("Invalid image bytes",  33697810, result.getBody().length);
    }


    @Test
    public void testBumpBase64ByFile() throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        float[] matrix = new float[]{-1.0F,-1.0F,0.0F,-1.0F,1.0F,1.0F,0.0F,1.0F,1.0F};

        BumpJob args = new BumpJob();
        args.setDocumentId(documentId);
        args.setEdgeAction(1);
        args.setUseAlpha(true);
        args.setMatrix(matrix);

        Future<Map> imageFuture = imageBumpFilterGateway.imageBumpFilter(args);

        BumpJob payload = (BumpJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);
        Assert.assertTrue("NULL Payload", payload != null );

        BufferedImage bufferedImage = payload.getBufferedImage();
        Assert.assertTrue("NULL BufferedImage in payload", bufferedImage != null );

        String contentType = payload.getDocument().getContentType().contentType;
        Assert.assertEquals("image/jpeg",contentType);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, Boolean.TRUE);
        Assert.assertEquals("Invalid image bytes",  44930416, result.getBody().length);
    }
}
