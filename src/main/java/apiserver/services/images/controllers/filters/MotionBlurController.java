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
import apiserver.services.images.gateways.filters.ApiImageFilterMotionBlurGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import apiserver.services.images.gateways.jobs.filters.MotionBlurJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
@Controller
@RestController
@Api(value = "/image", description = "[IMAGE]")
@RequestMapping("/image")
public class MotionBlurController
{
    public final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ApiImageFilterMotionBlurGateway imageFilterMotionBlurGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.
     * @param documentId
     * @param angle
     * @param distance
     * @param rotation
     * @param wrapEdges
     * @param zoom
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter simulates motion blur on an image. You specify a combination of angle/distance for linear motion blur, a rotaiton angle for spin blur or a zoom factor for zoom blur. You can combine these in any proportions you want to get effects like spiral blurs.")
    @RequestMapping(value = "/filter/{documentId}/motionblur", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> imageMotionBlurByFile(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name="angle", required = true, defaultValue = "0")  @RequestParam(value="angle", required = false, defaultValue="0") float angle
            , @ApiParam(name="distance", required = true, defaultValue = "1")  @RequestParam(value="distance", required = false,  defaultValue="0") float distance
            , @ApiParam(name="rotation", required = true, defaultValue = "0")  @RequestParam(value="rotation", required = false,  defaultValue="0") float rotation
            , @ApiParam(name="wrapEdges", required = true, defaultValue = "false")  @RequestParam(value="wrapEdges", required = false,  defaultValue="false") boolean wrapEdges
            , @ApiParam(name="zoom", required = true, defaultValue = "0")  @RequestParam(value="zoom", required = false,  defaultValue="0") float zoom
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        MotionBlurJob args = new MotionBlurJob();
        args.setDocumentId(documentId);
        args.setAngle(angle);
        args.setDistance(distance);
        args.setRotation(rotation);
        args.setWrapEdges(wrapEdges);
        args.setZoom(zoom);


        Future<Map> imageFuture = imageFilterMotionBlurGateway.imageMotionBlurFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, false );
        return result;
    }



    /**
     * This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.
     * @param file
     * @param angle
     * @param distance
     * @param rotation
     * @param wrapEdges
     * @param zoom
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter simulates motion blur on an image. You specify a combination of angle/distance for linear motion blur, a rotaiton angle for spin blur or a zoom factor for zoom blur. You can combine these in any proportions you want to get effects like spiral blurs.")
    @RequestMapping(value = "/filter/motionblur", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<byte[]> imageMotionBlurByFile(
            @ApiParam(name = "file", required = true) @RequestParam(value = "file", required = true) MultipartFile file
            , @ApiParam(name="angle", required = true, defaultValue = "0")  @RequestParam(value="angle", required = false, defaultValue="0") float angle
            , @ApiParam(name="distance", required = true, defaultValue = "1")  @RequestParam(value="distance", required = false,  defaultValue="0") float distance
            , @ApiParam(name="rotation", required = true, defaultValue = "0")  @RequestParam(value="rotation", required = false,  defaultValue="0") float rotation
            , @ApiParam(name="wrapEdges", required = true, defaultValue = "false")  @RequestParam(value="wrapEdges", required = false,  defaultValue="false") boolean wrapEdges
            , @ApiParam(name="zoom", required = true, defaultValue = "0")  @RequestParam(value="zoom", required = false,  defaultValue="0") float zoom
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        MotionBlurJob job = new MotionBlurJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType(MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName(file.getOriginalFilename());
        job.setAngle(angle);
        job.setDistance(distance);
        job.setRotation(rotation);
        job.setWrapEdges(wrapEdges);
        job.setZoom(zoom);


        Future<Map> imageFuture = imageFilterMotionBlurGateway.imageMotionBlurFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, false );
        return result;
    }



}
