package apiserver.services.images.controllers.manipulations;

import apiserver.MimeType;
import apiserver.apis.v1_0.documents.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.images.ImageResizeGateway;
import apiserver.services.images.gateways.images.ImageRotateGateway;
import apiserver.services.images.gateways.jobs.images.FileResizeJob;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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


//TODO: Add ImageNegative
//TODO: Add ImageOverlay
//TODO: Add ImageFlip
//TODO: Add ImageSharpen
//TODO: Add ImageShear, ImageShearDrawingAxis
//TODO: Add ImageRotateDrawingAxis
//TODO: Add Image Drawing Support (array of actions; ImageDrawLine, ImageDrawOval, ImageDrawPoint, ImageDrawQuadraticCurve

/**
 * User: mnimer
 * Date: 9/15/12
 */
@Controller
@RestController
@Api(value = "/image", description = "[IMAGE]")
@RequestMapping("/image")
public class ResizeController
{
    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private ImageResizeGateway imageResizeGateway;

    @Autowired
    private ImageRotateGateway imageRotateGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;



    /**
     * Resize an image
     *
     * @param file
     * @param interpolation - highestQuality,highQuality,mediumQuality,highestPerformance,highPerformance,mediumPerformance,nearest,bilinear,bicubic,bessel,blackman,hamming,hanning,hermite,lanczos,mitchell,quadratic
     * @param width
     * @param height
     * @return
     */
    @ApiOperation(value="Resize an uploaded image")
    @RequestMapping(value = "/modify/resize", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<byte[]> resizeImageByImage(
            @ApiParam(name="file", required = true) @RequestParam MultipartFile file
            , @ApiParam(name="width", required = true, defaultValue = "200") @RequestParam(required = true) Integer width
            , @ApiParam(name="height", required = true, defaultValue = "200") @RequestParam(required = true) Integer height
            , @ApiParam(name="interpolation", required = false, defaultValue = "bicubic") @RequestParam(required = false, defaultValue = "bicubic") String interpolation
            , @ApiParam(name="scaleToFit", required = false, defaultValue = "false") @RequestParam(required = false, defaultValue = "false") Boolean scaleToFit
            , @ApiParam(name = "returnAsBase64", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "returnAsBase64", required = false, defaultValue = "false") Boolean returnAsBase64
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        FileResizeJob job = new FileResizeJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType(MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName(file.getOriginalFilename());
        job.setWidth(width);
        job.setHeight(height);
        job.setInterpolation(interpolation.toUpperCase());
        job.setScaleToFit(scaleToFit);

        Future<Map> imageFuture = imageResizeGateway.resizeImage(job);
        FileResizeJob payload = (FileResizeJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, returnAsBase64 );
        return result;
    }




    /**
     * Resize an image
     *
     * @param documentId
     * @param interpolation - highestQuality,highQuality,mediumQuality,highestPerformance,highPerformance,mediumPerformance,nearest,bilinear,bicubic,bessel,blackman,hamming,hanning,hermite,lanczos,mitchell,quadratic
     * @param width
     * @param height
     * @return
     */
    @ApiOperation(value="Resize an uploaded image")
    @RequestMapping(value = "/modify/{documentId}/resize", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> resizeImageByImage(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name="width", required = true, defaultValue = "200") @RequestParam(required = true) Integer width
            , @ApiParam(name="height", required = true, defaultValue = "200") @RequestParam(required = true) Integer height
            , @ApiParam(name="interpolation", required = false, defaultValue = "bicubic") @RequestParam(required = false, defaultValue = "bicubic") String interpolation
            , @ApiParam(name="scaleToFit", required = false, defaultValue = "false") @RequestParam(required = false, defaultValue = "false") Boolean scaleToFit
            , @ApiParam(name = "returnAsBase64", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "returnAsBase64", required = false, defaultValue = "false") Boolean returnAsBase64
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        FileResizeJob job = new FileResizeJob();
        job.setDocumentId(documentId);
        job.setWidth(width);
        job.setHeight(height);
        job.setInterpolation(interpolation.toUpperCase());
        job.setScaleToFit(scaleToFit);

        Future<Map> imageFuture = imageResizeGateway.resizeImage(job);
        FileResizeJob payload = (FileResizeJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, returnAsBase64 );
        return result;
    }



}
