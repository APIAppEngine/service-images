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
import apiserver.services.images.gateways.filters.ApiImageFilterLensBlurGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import apiserver.services.images.gateways.jobs.filters.LensBlurJob;
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
public class LensBlurController
{
    public final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ApiImageFilterLensBlurGateway imageFilterLensBlurGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * This filter simulates the blurring caused by a camera lens. You can change the aperture size and shape and also specify blooming of the uploaded image. This filter is very slow.
     *
     * @param documentId
     * @param radius
     * @param sides
     * @param bloom
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter simulates the blurring caused by a camera lens. You can change the aperture size and shape and also specify blooming of the image. This filter is very slow.")
    @RequestMapping(value = "/filter/{documentId}/lensblur", method = {RequestMethod.GET})
    public ResponseEntity<byte[]> imageLensBlurByFile(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name = "radius", required = false, defaultValue = "10") @RequestParam(value = "radius", required = false, defaultValue = "10") float radius
            , @ApiParam(name = "sides", required = false, defaultValue = "5") @RequestParam(value = "sides", required = false, defaultValue = "5") int sides
            , @ApiParam(name = "bloom", required = false, defaultValue = "2") @RequestParam(value = "bloom", required = false, defaultValue = "2") float bloom
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        LensBlurJob args = new LensBlurJob();
        args.setDocumentId(documentId);
        args.setRadius(radius);
        args.setSides(sides);
        args.setBloom(bloom);

        Future<Map> imageFuture = imageFilterLensBlurGateway.imageLensBlurFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, false );
        return result;
    }



    /**
     * This filter simulates the blurring caused by a camera lens. You can change the aperture size and shape and also specify blooming of the uploaded image. This filter is very slow.
     *
     * @param file
     * @param radius
     * @param sides
     * @param bloom
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter simulates the blurring caused by a camera lens. You can change the aperture size and shape and also specify blooming of the image. This filter is very slow.")
    @RequestMapping(value = "/filter/lensblur", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> imageLensBlurByFile(
            @ApiParam(name = "file", required = true) @RequestParam(value = "file", required = true) MultipartFile file
            , @ApiParam(name = "radius", required = false, defaultValue = "10") @RequestParam(value = "radius", required = false, defaultValue = "10") float radius
            , @ApiParam(name = "sides", required = false, defaultValue = "5") @RequestParam(value = "sides", required = false, defaultValue = "5") int sides
            , @ApiParam(name = "bloom", required = false, defaultValue = "2") @RequestParam(value = "bloom", required = false, defaultValue = "2") float bloom
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        LensBlurJob job = new LensBlurJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType(MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName(file.getOriginalFilename());
        job.setRadius(radius);
        job.setSides(sides);
        job.setBloom(bloom);

        Future<Map> imageFuture = imageFilterLensBlurGateway.imageLensBlurFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, false );
        return result;
    }


}
