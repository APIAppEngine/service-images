package apiserver.services.images.controllers.manipulations;

import apiserver.MimeType;
import apiserver.services.cache.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.images.ImageResizeGateway;
import apiserver.services.images.gateways.images.ImageRotateGateway;
import apiserver.services.images.gateways.jobs.images.FileRotateJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
public class RotateController
{
    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private ImageResizeGateway imageResizeGateway;

    @Autowired
    private ImageRotateGateway imageRotateGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;





    /**
     * rotate an image
     *
     * @param file
     * @param angle
     * @return
     */
    @ApiOperation(value="Rotate an uploaded image")
    @RequestMapping(value = "/modify/rotate.{format}", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<byte[]> rotateImageByImage(
            @ApiParam(name="file", required = true) @RequestParam MultipartFile file
            , @ApiParam(name="angle", required = true, defaultValue = "90") @RequestParam(required = true, defaultValue = "90") Integer angle
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        FileRotateJob job = new FileRotateJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType( MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName(file.getOriginalFilename());
        job.setAngle(angle);

        Future<Map> imageFuture = imageRotateGateway.rotateImage(job);
        FileRotateJob payload = (FileRotateJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(payload.getBufferedImage(), _contentType, false);
        return result;
    }


    /**
     * rotate an image
     *
     * @param documentId
     * @param angle
     * @return
     */
    @ApiOperation(value="Rotate an uploaded image")
    @RequestMapping(value = "/modify/{documentId}/rotate.{format}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> rotateImageByImage(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name="angle", required = true, defaultValue = "90") @RequestParam(required = true, defaultValue = "90") Integer angle
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        FileRotateJob job = new FileRotateJob();
        job.setDocumentId(documentId);
        job.setAngle(angle);

        Future<Map> imageFuture = imageRotateGateway.rotateImage(job);
        FileRotateJob payload = (FileRotateJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage(payload.getBufferedImage(), _contentType, false);
        return result;
    }


}
