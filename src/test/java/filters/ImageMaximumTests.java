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

import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.filters.ApiImageFilterMaximumGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@ImportResource("flows/filters/filterMaximum-flow.xml")
public class ImageMaximumTests
{

    @Autowired
    private ApiImageFilterMaximumGateway imageMaximumFilterGateway;

    static File file = null;

    @BeforeClass
    public static void setup() throws URISyntaxException
    {
        file = new File(  ImageMaximumTests.class.getClassLoader().getResource("IMG_5932.JPG").toURI()  );

    }



    @Test
    public void testMaximumByFile() throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        ImageDocumentJob args = new ImageDocumentJob();
        args.setDocument(new Document(file));

        Future<Map> imageFuture = imageMaximumFilterGateway.imageMaximumFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue("NULL Payload", payload != null );

        BufferedImage bufferedImage = payload.getBufferedImage();
        Assert.assertTrue("NULL BufferedImage in payload", bufferedImage != null );

        String contentType = payload.getDocument().getContentType().contentType;
        Assert.assertEquals("image/jpeg",contentType);

        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, Boolean.FALSE);
        Assert.assertEquals("Invalid image bytes",  19686188, result.getBody().length);
    }


    @Test
    public void testMaximumBase64ByFile() throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        ImageDocumentJob args = new ImageDocumentJob();
        args.setDocument(new Document(file));

        Future<Map> imageFuture = imageMaximumFilterGateway.imageMaximumFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue("NULL Payload", payload != null );

        BufferedImage bufferedImage = (BufferedImage)payload.getBufferedImage();
        Assert.assertTrue("NULL BufferedImage in payload", bufferedImage != null );


        String contentType = (String)payload.getDocument().getContentType().contentType;
        Assert.assertTrue("image/jpeg", contentType != null );


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, Boolean.TRUE);
        Assert.assertEquals("Invalid image bytes",  26248252, result.getBody().length);
    }
}
