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
import apiserver.core.FileUploadHelper;
import apiserver.services.cache.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.filters.ApiImageFilterMedianGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@Api(value = "/api/image", description = "[IMAGE]")
@RequestMapping("/api/image")
public class MedianController
{
    public final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ApiImageFilterMedianGateway imageFilterMedianGateway;

    @Autowired
    private FileUploadHelper fileUploadHelper;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.
     *
     * @param documentId
     * @param format
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.")
    @RequestMapping(value = "/filter/{documentId}/median.{format}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> imageMedianByFile(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        ImageDocumentJob args = new ImageDocumentJob();
        args.setDocumentId(documentId);

        Future<Map> imageFuture = imageFilterMedianGateway.imageMedianFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( payload.getBufferedImage(), _contentType, false );
        return result;
    }



    /**
     * This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.
     *
     * @param file
     * @param format
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.")
    @RequestMapping(value = "/filter/median", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<byte[]> imageMedianByFile(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(name = "file", required = false) @RequestParam(value = "file", required = false) MultipartFile file
            , @ApiParam(name = "format", required = false) @RequestParam(value = "format", required = false) String format

    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
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

        ImageDocumentJob job = new ImageDocumentJob();
        job.setDocumentId(null);
        job.setDocument(new Document(file));


        Future<Map> imageFuture = imageFilterMedianGateway.imageMedianFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( payload.getBufferedImage(), _outputContentType, false );
        return result;
    }



}
