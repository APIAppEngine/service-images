package apiserver.services.images.controllers.manipulations;

import apiserver.MimeType;
import apiserver.core.FileUploadHelper;
import apiserver.model.Document;
import apiserver.services.images.gateways.images.ImageResizeGateway;
import apiserver.services.images.gateways.jobs.images.FileResizeJob;
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
import javax.servlet.http.HttpServletResponse;
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
@Api(value = "/api/v1/image", description = "[IMAGE]")
@RequestMapping("/api/v1/image")
public class ResizeController
{
    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private ImageResizeGateway imageResizeGateway;

    @Autowired
    private FileUploadHelper fileUploadHelper;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;






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
    @RequestMapping(value = "/modify/{documentId}/resize.{format}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> resizeImageByImage(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name="width", required = true, defaultValue = "200") @RequestParam(required = true) Integer width
            , @ApiParam(name="height", required = true, defaultValue = "200") @RequestParam(required = true) Integer height
            , @ApiParam(name="interpolation", required = false, defaultValue = "bicubic") @RequestParam(required = false, defaultValue = "bicubic") String interpolation
            , @ApiParam(name="scaleToFit", required = false, defaultValue = "false") @RequestParam(required = false, defaultValue = "false") Boolean scaleToFit
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }


        FileResizeJob job = new FileResizeJob();
        job.setDocumentId(documentId);
        job.setWidth(width);
        job.setHeight(height);
        job.setInterpolation(interpolation.toUpperCase());
        job.setScaleToFit(scaleToFit);

        Future<Map> imageFuture = imageResizeGateway.resizeImage(job);
        FileResizeJob payload = (FileResizeJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }


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
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(name="file", required = false) @RequestParam(value = "file", required = false) MultipartFile file
            , @ApiParam(name="width", required = true, defaultValue = "200") @RequestParam(required = true) Integer width
            , @ApiParam(name="height", required = true, defaultValue = "200") @RequestParam(required = true) Integer height
            , @ApiParam(name="interpolation", required = false, defaultValue = "bicubic") @RequestParam(required = false, defaultValue = "bicubic") String interpolation
            , @ApiParam(name="scaleToFit", required = false, defaultValue = "false") @RequestParam(required = false, defaultValue = "false") Boolean scaleToFit
            , @ApiParam(name = "format", required = false) @RequestParam(value = "format", required = false) String format

    ) throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        Document _file = null;
        MimeType _outputMimeType = null;
        String _outputContentType = null;


        MultipartFile _mFile = fileUploadHelper.getFileFromRequest(file, request);
        _file = new Document(_mFile);
        _outputMimeType = fileUploadHelper.getOutputFileFormat(format, _file.getContentType() );
        _outputContentType = _outputMimeType.contentType;
        if( !_file.getContentType().isSupportedImage() ||  !_outputMimeType.isSupportedImage() )
        {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }


        FileResizeJob job = new FileResizeJob();
        job.setDocumentId(null);
        job.setDocument( _file );
        //job.getDocument().setContentType(MimeType.getMimeType(file.getContentType()) );
        //job.getDocument().setFileName(file.getOriginalFilename());
        job.setWidth(width);
        job.setHeight(height);
        job.setInterpolation(interpolation.toUpperCase());
        job.setScaleToFit(scaleToFit);

        Future<Map> imageFuture = imageResizeGateway.resizeImage(job);
        FileResizeJob payload = (FileResizeJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }


}
