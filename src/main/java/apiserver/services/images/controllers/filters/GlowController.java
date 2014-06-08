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
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.cache.model.Document;
import apiserver.services.images.gateways.filters.ApiImageFilterGlowGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import apiserver.services.images.gateways.jobs.filters.GlowJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
public class GlowController
{
    public final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ApiImageFilterGlowGateway imageFilterGlowGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * This filter produces a glowing effect on an uploaded image by adding a blurred version of the image to subtracted from the original image.
     *
     * @param documentId
     * @param amount
     * @param format
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter produces a glowing effect on an image by adding a blurred version of the image to subtracted from the original image.")
    @RequestMapping(value = "/filter/{documentId}/glow.{format}", method = {RequestMethod.GET})
    public ResponseEntity<byte[]> imageGlowByFile(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name = "amount", required = true, defaultValue = "2") @RequestParam(required = false, defaultValue = "2") int amount
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        GlowJob args = new GlowJob();
        args.setDocumentId(documentId);
        args.setAmount(amount);

        Future<Map> imageFuture = imageFilterGlowGateway.imageGlowFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( payload.getBufferedImage(), _contentType, false );
        return result;
    }



    /**
     * This filter produces a glowing effect on an uploaded image by adding a blurred version of the image to subtracted from the original image.
     *
     * @param file
     * @param amount
     * @param format
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter produces a glowing effect on an image by adding a blurred version of the image to subtracted from the original image.")
    @RequestMapping(value = "/filter/glow.{format}", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> imageGlowByFile(
            @ApiParam(name = "file", required = true) @RequestParam(value = "file", required = true) MultipartFile file
            , @ApiParam(name = "amount", required = true, defaultValue = "2") @RequestParam(required = false, defaultValue = "2") int amount
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        GlowJob job = new GlowJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType( MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName( file.getOriginalFilename() );
        job.setAmount(amount);

        Future<Map> imageFuture = imageFilterGlowGateway.imageGlowFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( payload.getBufferedImage(), _contentType, false );
        return result;
    }


}
