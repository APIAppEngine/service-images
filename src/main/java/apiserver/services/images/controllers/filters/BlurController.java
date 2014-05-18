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
import apiserver.services.cache.model.Document;
import apiserver.core.common.ResponseEntityHelper;
import apiserver.services.images.gateways.filters.ApiImageFilterBlurGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: mikenimer
 * Date: 8/29/13
 */
@Controller
@RestController
@Api(value = "/image", description = "[IMAGE]")
@RequestMapping("/image")
public class BlurController
{
    public final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ApiImageFilterBlurGateway imageFilterBlurGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    /**
     * This filter blurs an uploaded image very slightly using a 3x3 blur kernel.
     *
     * @param documentId
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter blurs an image very slightly using a 3x3 blur kernel.")
    @RequestMapping(value = "/filter/{documentId}/blur.{contentType}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imageBlurById(
            @ApiParam(name = "documentId", required = true,  defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA")
            @PathVariable(value = "documentId") String documentId
            , @ApiParam(name = "contentType", required = true, defaultValue = "jpg") @PathVariable(value = "contentType") String contentType
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException, URISyntaxException
    {
        ImageDocumentJob job = new ImageDocumentJob();
        job.setDocumentId(documentId);

        Future<Map> imageFuture = imageFilterBlurGateway.imageBlurFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob) imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //BufferedImage bufferedImage = payload.getBufferedImage();
        //String contentType = payload.getDocument().getContentType().name();
        String _contentType = MimeType.getMimeType(contentType).contentType;
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(payload.getDocument().getFileBytes(), _contentType, false);
        return result;
    }


    @ApiOperation(value = "This filter blurs an image very slightly using a 3x3 blur kernel.")
    @RequestMapping(value = "/filter/blur", method = RequestMethod.POST)
    public ResponseEntity<byte[]> imageBlurByFile(
            @ApiParam(name = "file", required = true)
            @RequestParam(value = "file", required = true) MultipartFile file
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException, URISyntaxException
    {
        ImageDocumentJob job = new ImageDocumentJob();
        job.setDocumentId(null);
        job.setDocument(new Document(file));
        job.getDocument().setContentType(MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName( file.getOriginalFilename() );

        Future<Map> imageFuture = imageFilterBlurGateway.imageBlurFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob) imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        //BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processFile(payload.getDocument().getFileBytes(), contentType, false);
        return result;
    }

}
