package apiserver.services.images.controllers.info;

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
import apiserver.core.common.ResponseEntityHelper;
import apiserver.model.Document;
import apiserver.services.images.gateways.images.ImageInfoGateway;
import apiserver.services.images.gateways.images.ImageMetadataGateway;
import apiserver.services.images.gateways.jobs.images.FileInfoJob;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mnimer
 * Date: 9/15/12
 */
@Controller
@RestController
@Api(value = "/api/v1/image", description = "[IMAGE]")
@RequestMapping("/api/v1/image")
public class SizeController
{
    @Autowired
    private ImageInfoGateway gateway;

    @Autowired
    public ImageMetadataGateway imageMetadataGateway;

    @Autowired
    private FileUploadHelper fileUploadHelper;

    private
    @Value("${defaultReplyTimeout}")
    Integer defaultTimeout;


    /**
     * get basic info about image.
     *
     * @param documentId cache id
     * @return height, width, pixel size, transparency
     * , @RequestPart("meta-data") Object metadata
     * <p/>
     * , @RequestParam MultipartFile file
     */
    @ApiOperation(value = "Get the height and width for the image", response = Map.class)
    @RequestMapping(value = "/info/{documentId}/size", method = {RequestMethod.GET})
    public WebAsyncTask<ResponseEntity<Map>> imageInfoByImageAsync(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA")
            @PathVariable(value = "documentId") String documentId
    ) throws ExecutionException, TimeoutException, InterruptedException
    {
        final String _documentId = documentId;

        Callable<ResponseEntity<Map>> callable = new Callable<ResponseEntity<Map>>()
        {
            @Override
            public ResponseEntity<Map> call() throws Exception
            {
                FileInfoJob args = new FileInfoJob();
                args.setDocumentId(_documentId);

                Future<Map> imageFuture = gateway.imageSize(args);
                Map payload = imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ResponseEntity<Map> result = new ResponseEntity<Map>(payload, headers, HttpStatus.OK);
                return result;
            }
        };

        return new WebAsyncTask<ResponseEntity<Map>>(defaultTimeout, callable);
    }


    /**
     * get basic info about image.
     *
     * @param file
     * @return
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     * @throws InterruptedException
     */
    @ApiOperation(value = "Get the height and width for the image")
    @RequestMapping(value = "/info/size", method = {RequestMethod.POST})
    public ResponseEntity<Map> imageInfoByImageAsync(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(name = "file", required = false) @RequestParam(value = "file", required = false) MultipartFile file
    ) throws ExecutionException, TimeoutException, InterruptedException, IOException
    {
        Document _file = null;
        MimeType _fileMimeType = null;


        MultipartFile _mFile = fileUploadHelper.getFileFromRequest(file, request);
        _file = new Document(_mFile);
        _fileMimeType = _file.getContentType();
        if (!_fileMimeType.isSupportedImage()) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }


        FileInfoJob job = new FileInfoJob();
        job.setDocumentId(null);
        job.setDocument(_file);

        Future<Map> imageFuture = gateway.imageSize(job);
        Map payload = imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        ResponseEntity<Map> result = new ResponseEntity(payload, HttpStatus.OK);
        return result;
    }


}
