package apiserver.services.images.controllers.manipulations;

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
import apiserver.model.Document;
import apiserver.services.images.gateways.images.ImageDrawBorderGateway;
import apiserver.services.images.gateways.jobs.images.FileBorderJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mnimer
 * Date: 9/18/12
 */
@Controller
@RestController
@Api(value = "/api/image", description = "[IMAGE]")
@RequestMapping("/api/image")
public class BorderController
{
    @Qualifier("imageDrawBorderApiGateway")
    @Autowired
    private ImageDrawBorderGateway imageDrawBorderGateway;

    @Autowired
    private FileUploadHelper fileUploadHelper;


    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;



    /**
     * Draw a border around an image
     *
     * @param documentId
     * @param color
     * @param thickness
     * @param format
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/modify/{documentId}/border.{format}", method = {RequestMethod.GET})
    public ResponseEntity<byte[]> drawBorderByImage(
            @ApiParam(name = "documentId", required = true) @PathVariable(value = "documentId") String documentId
            , @ApiParam(name="color", required = true) @RequestParam(required = true) String color
            , @ApiParam(name="thickness", required = true) @RequestParam(required = true) Integer thickness
            , @ApiParam(name = "format", required = true, defaultValue = "jpg") @PathVariable(value = "format") String format

    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        String _contentType = MimeType.getMimeType(format).contentType;
        if( !MimeType.getMimeType(format).isSupportedImage() )
        {
            return new ResponseEntity<byte[]>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }


        FileBorderJob args = new FileBorderJob();
        args.setDocumentId(documentId);
        args.setColor(color);
        args.setThickness(thickness);
        args.setFormat(format);

        Future<Map> imageFuture = imageDrawBorderGateway.imageDrawBorderFilter(args);
        FileBorderJob payload = (FileBorderJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //pass CF Response back to the client
        return payload.getHttpResponse();
    }


    /**
     * Draw a border around an image
     *
     * @param file
     * @param color
     * @param thickness
     * @param format
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/modify/border", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> drawBorderByImage(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(name = "file", required = false) @RequestParam(value = "file", required = false) MultipartFile file
            , @ApiParam(name="color", required = true) @RequestParam(required = true) String color
            , @ApiParam(name="thickness", required = true) @RequestParam(required = true) Integer thickness
            , @ApiParam(name = "format", required = false) @RequestParam(value = "format", required = false) String format

    ) throws InterruptedException, ExecutionException, TimeoutException, IOException
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



        FileBorderJob job = new FileBorderJob();
        job.setDocumentId(null);
        job.setDocument(_file);
        job.setColor(color);
        job.setThickness(thickness);
        job.setFormat(format);

        Future<Map> imageFuture = imageDrawBorderGateway.imageDrawBorderFilter(job);
        FileBorderJob payload = (FileBorderJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);


        //pass CF Response back to the client
        return payload.getHttpResponse();
    }
}
