package apiserver.services.images.controllers.filters;

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

import apiserver.MimeType;
import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.filters.ApiImageFilterBumpGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import apiserver.services.images.gateways.jobs.filters.BumpJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller
@RestController
@Api(value = "/image", description = "[IMAGE]")
@RequestMapping("/image")
public class BumpController
{
    @Autowired
    private ApiImageFilterBumpGateway imageFilterBumpGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * This filter does a simple convolution which emphasises edges in an uploaded image.
     *
     * @param documentId
     * @param edgeAction
     * @param useAlpha
     * @param matrix
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter does a simple convolution which emphasises edges in an image.")
    @RequestMapping(value = "/filter/{documentId}/bump", method = {RequestMethod.GET})
    public ResponseEntity<byte[]> imageBumpByFile(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name = "edgeAction", required = false, defaultValue = "1") @RequestParam(value = "edgeAction", required = false, defaultValue = "1") int edgeAction
            , @ApiParam(name = "useAlpha", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "useAlpha", required = false, defaultValue = "true") boolean useAlpha
            , @ApiParam(name = "matrix", required = false, defaultValue = "-1.0,-1.0,0.0,-1.0,1.0,1.0,0.0,1.0,1.0") @RequestParam(value = "matrix", required = false, defaultValue = "-1.0,-1.0,0.0,-1.0,1.0,1.0,0.0,1.0,1.0") String matrix
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        // convert string array into float array
        String[] matrixStrings = matrix.split(",");
        float[] matrixValues = new float[matrixStrings.length];
        for (int i = 0; i < matrixStrings.length; i++)
        {
            String s = matrixStrings[i];
            matrixValues[i] = Float.parseFloat(s);
        }

        BumpJob args = new BumpJob();
        args.setDocumentId(documentId);
        args.setEdgeAction(edgeAction);
        args.setUseAlpha(useAlpha);
        args.setMatrix(matrixValues);


        Future<Map> imageFuture = imageFilterBumpGateway.imageBumpFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob) imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, false);
        return result;
    }


    /**
     * This filter does a simple convolution which emphasises edges in an uploaded image.
     *
     * @param file
     * @param edgeAction
     * @param useAlpha
     * @param matrix
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter does a simple convolution which emphasises edges in an image.")
    @RequestMapping(value = "/filter/bump", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> imageBumpByFile(
            @ApiParam(name = "file", required = true) @RequestParam(value = "file", required = true) MultipartFile file
            , @ApiParam(name = "edgeAction", required = false, defaultValue = "1") @RequestParam(value = "edgeAction", required = false, defaultValue = "1") int edgeAction
            , @ApiParam(name = "useAlpha", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "useAlpha", required = false, defaultValue = "true") boolean useAlpha
            , @ApiParam(name = "matrix", required = false, defaultValue = "-1.0,-1.0,0.0,-1.0,1.0,1.0,0.0,1.0,1.0") @RequestParam(value = "matrix", required = false, defaultValue = "-1.0,-1.0,0.0,-1.0,1.0,1.0,0.0,1.0,1.0") String matrix
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        // convert string array into float array
        String[] matrixStrings = matrix.split(",");
        float[] matrixValues = new float[matrixStrings.length];
        for (int i = 0; i < matrixStrings.length; i++)
        {
            String s = matrixStrings[i];
            matrixValues[i] = Float.parseFloat(s);
        }

        BumpJob job = new BumpJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType( MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName( file.getOriginalFilename() );

        job.setEdgeAction(edgeAction);
        job.setUseAlpha(useAlpha);
        job.setMatrix(matrixValues);


        Future<Map> imageFuture = imageFilterBumpGateway.imageBumpFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob) imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(bufferedImage, contentType, false);
        return result;
    }
}