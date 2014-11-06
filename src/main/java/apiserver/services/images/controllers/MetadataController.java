package apiserver.services.images.controllers;

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
import apiserver.services.images.gateways.images.ImageInfoGateway;
import apiserver.services.images.gateways.images.ImageMetadataGateway;
import apiserver.services.images.gateways.jobs.images.FileMetadataJob;
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
@Api(value = "/api/image", description = "[IMAGE]")
@RequestMapping("/api/image")
public class MetadataController
{
    @Autowired
    private ImageInfoGateway imageInfoGateway;

    @Autowired
    public ImageMetadataGateway imageMetadataGateway;

    @Autowired
    private FileUploadHelper fileUploadHelper;


    private
    @Value("${defaultReplyTimeout}")
    Integer defaultTimeout;


    /**
     * get embedded metadata
     *
     * @param documentId
     * @return height, width
     */
    @ApiOperation(value = "Get the embedded metadata", response = Map.class)
    @RequestMapping(value = "/info/{documentId}/metadata", method = {RequestMethod.GET})
    public WebAsyncTask<Map> imageMetadataByImage(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
    )
    {
        final String _documentId = documentId;

        Callable<Map> callable = new Callable<Map>()
        {
            @Override
            public Map call() throws Exception
            {
                FileMetadataJob args = new FileMetadataJob();
                args.setDocumentId(_documentId);

                Future<Map> imageFuture = imageMetadataGateway.getMetadata(args);
                FileMetadataJob payload = (FileMetadataJob) imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

                return payload.getMetadata();
            }
        };

        return new WebAsyncTask<Map>(defaultTimeout, callable);
    }


    /**
     * get embedded metadata
     *
     * @param file uploaded image
     * @return height, width
     */
    @ApiOperation(value = "Get the embedded metadata", response = Map.class)
    @RequestMapping(value = "/info/metadata", method = {RequestMethod.POST})
    public ResponseEntity<Map> imageMetadataByImage(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(name = "file", required = true) @RequestParam(value = "file", required = true) MultipartFile file
    ) throws ExecutionException, InterruptedException, TimeoutException, IOException
    {
        Document _file = null;

        MultipartFile _mFile = fileUploadHelper.getFileFromRequest(file, request);
        _file = new Document(_mFile);
        if (!_file.getContentType().isSupportedImage()) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }


        FileMetadataJob job = new FileMetadataJob();
        job.setDocumentId(null);
        job.setDocument(_file);

        Future<Map> imageFuture = imageMetadataGateway.getMetadata(job);
        FileMetadataJob payload = (FileMetadataJob) imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        return new ResponseEntity<>(payload.getMetadata(), HttpStatus.OK);

    }
}