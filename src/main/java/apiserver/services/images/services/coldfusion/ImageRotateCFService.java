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
import apiserver.exceptions.NotImplementedException;
import apiserver.services.images.ImageConfigMBean;
import apiserver.services.images.gateways.jobs.images.FileRotateJob;
import apiserver.services.images.services.grid.GridService;
import apiserver.workers.coldfusion.services.images.ImageResizeCallable;
import apiserver.workers.coldfusion.services.images.ImageRotateCallable;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridgain.grid.Grid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: mikenimer
 * Date: 8/26/13
 */
public class ImageRotateCFService extends GridService implements Serializable
{
    private final Log log = LogFactory.getLog(this.getClass());

    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;



    public Object execute(Message<?> message) throws ColdFusionException
    {
        FileRotateJob props = (FileRotateJob)message.getPayload();

        try
        {
            Grid grid = verifyGridConnection();
            ExecutorService exec = getColdFusionExecutor();

            byte[] imageBytes = props.getDocument().getFileBytes();

            Future<byte[]> future = exec.submit(
                    new ImageRotateCallable(imageBytes, props.getFormat(), props.getAngle() )
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
