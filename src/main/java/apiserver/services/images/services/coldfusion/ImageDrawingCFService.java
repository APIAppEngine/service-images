package apiserver.services.images.services.coldfusion;

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

import apiserver.exceptions.ColdFusionException;
import apiserver.services.images.gateways.jobs.images.FileBorderJob;
import apiserver.services.images.gateways.jobs.images.FileTextJob;
import apiserver.services.images.services.grid.GridService;
import apiserver.workers.coldfusion.services.images.ImageBorderCallable;
import apiserver.workers.coldfusion.services.images.ImageTextCallable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: mnimer
 * Date: 9/18/12
 */
public class ImageDrawingCFService implements Serializable
{
    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired private GridService gridService;

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;

    public Object imageBorderHandler(Message<?> message) throws ColdFusionException
    {
        FileBorderJob props = (FileBorderJob)message.getPayload();

        try
        {

            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ExecutorService exec = gridService.getColdFusionExecutor();

            byte[] imageBytes = props.getDocument().getFileBytes();

            Future<byte[]> future = exec.submit(
                    new ImageBorderCallable(imageBytes, props.getFormat(), props.getColor(), props.getThickness())
            );

            byte[] _result = future.get(defaultTimeout, TimeUnit.SECONDS);
            props.setImageBytes(_result);
            return props;
        }
        catch(Exception ge){
            throw new RuntimeException(ge);
        }
    }


    public Object imageDrawTextHandler(Message<?> message) throws ColdFusionException
    {
        FileTextJob props = (FileTextJob)message.getPayload();


        try
        {

            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ExecutorService exec = gridService.getColdFusionExecutor();

            byte[] imageBytes = props.getDocument().getFileBytes();

            Future<byte[]> future = exec.submit(
                    new ImageTextCallable(imageBytes, props.getFormat(), props.getText(), props.getColor(), props.getFontSize(), props.getFontStyle(), props.getAngle(), props.getX(), props.getY())
            );

            byte[] _result = future.get(defaultTimeout, TimeUnit.SECONDS);
            props.setImageBytes(_result);
            return props;
        }
        catch(Exception ge){
            throw new RuntimeException(ge);
        }


    }
}
