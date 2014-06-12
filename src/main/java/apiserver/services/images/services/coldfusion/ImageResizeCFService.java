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

import apiserver.core.connectors.coldfusion.IColdFusionBridge;
import apiserver.exceptions.ColdFusionException;
import apiserver.services.images.ImageConfigMBean;
import apiserver.services.images.gateways.jobs.images.FileResizeJob;
import apiserver.services.images.services.grid.GridService;
import apiserver.workers.coldfusion.services.images.ImageResizeCallable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridgain.grid.Grid;
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
public class ImageResizeCFService extends GridService implements Serializable
{
    private final Log log = LogFactory.getLog(this.getClass());

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;


    public Object execute(Message<?> message) throws ColdFusionException
    {
        FileResizeJob props = (FileResizeJob)message.getPayload();

        try
        {
            Grid grid = verifyGridConnection();

            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ExecutorService exec = getColdFusionExecutor();

            byte[] imageBytes = props.getDocument().getFileBytes();

            Future<byte[]> future = exec.submit(
                    new ImageResizeCallable(imageBytes, props.getFormat(), props.getWidth(), props.getHeight(), props.getInterpolation(), props.getScaleToFit() )
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
