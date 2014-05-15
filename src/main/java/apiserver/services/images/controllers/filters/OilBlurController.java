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
import apiserver.services.images.gateways.filters.ApiImageFilterOilGateway;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import apiserver.services.images.gateways.jobs.filters.OilJob;
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
import org.springframework.web.bind.annotation.ResponseBody;
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
public class OilBlurController
{
    public final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ApiImageFilterOilGateway imageFilterOilGateway;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;




    /**
     * This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.
     *
     * @param documentId
     * @param level
     * @param range
     * @param returnAsBase64
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter produces an oil painting effect as described in the book \"Beyond Photography - The Digital Darkroom\". You can specify the smearing radius. It's quite a slow filter especially with a large radius.")
    @RequestMapping(value = "/filter/{documentId}/oil", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> imageOilBlurByFile(
            @ApiParam(name = "documentId", required = true, defaultValue = "8D981024-A297-4169-8603-E503CC38EEDA") @PathVariable(value = "documentId") String documentId
            , @ApiParam(name="level", required = true, defaultValue = "3")  @RequestParam(value="angle", required = false, defaultValue="3") int level
            , @ApiParam(name="range", required = true, defaultValue = "256")  @RequestParam(value="range", required = false,  defaultValue="256") int range
            , @ApiParam(name = "returnAsBase64", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "returnAsBase64", required = false, defaultValue = "false") Boolean returnAsBase64
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        OilJob args = new OilJob();
        args.setDocumentId(documentId);
        args.setLevels(level);
        args.setRange(range);


        Future<Map> imageFuture = imageFilterOilGateway.imageOilFilter(args);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, returnAsBase64 );
        return result;
    }




    /**
     * This filter replaces each pixel by the median of the input pixel and its eight neighbours. Each of the RGB channels is considered separately.
     *
     * @param file
     * @param level
     * @param range
     * @param returnAsBase64
     * @return
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @ApiOperation(value = "This filter produces an oil painting effect as described in the book \"Beyond Photography - The Digital Darkroom\". You can specify the smearing radius. It's quite a slow filter especially with a large radius.")
    @RequestMapping(value = "/filter/oil", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<byte[]> imageOilBlurByFile(
            @ApiParam(name = "file", required = true) @RequestParam(value = "file", required = true) MultipartFile file
            , @ApiParam(name="level", required = true, defaultValue = "3")  @RequestParam(value="angle", required = false, defaultValue="3") int level
            , @ApiParam(name="range", required = true, defaultValue = "256")  @RequestParam(value="range", required = false,  defaultValue="256") int range
            , @ApiParam(name = "returnAsBase64", required = false, defaultValue = "true", allowableValues = "true,false") @RequestParam(value = "returnAsBase64", required = false, defaultValue = "false") Boolean returnAsBase64
    ) throws TimeoutException, ExecutionException, InterruptedException, IOException
    {
        OilJob job = new OilJob();
        job.setDocumentId(null);
        job.setDocument( new Document(file) );
        job.getDocument().setContentType( MimeType.getMimeType(file.getContentType()) );
        job.getDocument().setFileName(file.getOriginalFilename());
        job.setLevels(level);
        job.setRange(range);


        Future<Map> imageFuture = imageFilterOilGateway.imageOilFilter(job);
        ImageDocumentJob payload = (ImageDocumentJob)imageFuture.get(defaultTimeout, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = payload.getBufferedImage();
        String contentType = payload.getDocument().getContentType().name();
        ResponseEntity<byte[]> result = ResponseEntityHelper.processImage( bufferedImage, contentType, returnAsBase64 );
        return result;
    }

}
