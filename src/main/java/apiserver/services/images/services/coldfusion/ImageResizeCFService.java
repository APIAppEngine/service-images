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
import apiserver.services.images.gateways.jobs.images.FileResizeJob;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * User: mnimer
 * Date: 9/18/12
 */
public class ImageResizeCFService
{

    private static String cfcPath;

    @Autowired
    private ImageConfigMBean imageConfigMBean;

    @Autowired
    public IColdFusionBridge coldFusionBridge;

    public void setColdFusionBridge(IColdFusionBridge coldFusionBridge)
    {
        this.coldFusionBridge = coldFusionBridge;
    }


    public Object execute(Message<?> message) throws ColdFusionException
    {
        FileResizeJob props = (FileResizeJob)message.getPayload();

        try
        {
            cfcPath = imageConfigMBean.getImageResizePath();
            String method = imageConfigMBean.getImageResizeMethod();
            String arguments = "";
            // extract properties
            Map<String, Object> methodArgs = coldFusionBridge.extractPropertiesFromPayload(props);


            // execute
            Object cfcResult = coldFusionBridge.invoke(cfcPath, method, methodArgs);

            if( cfcResult instanceof byte[] )
            {
                // convert base64 back to buffered image
                byte[] bytes = Base64.decodeBase64( new String((byte[])cfcResult) );
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
                props.setBufferedImage( bi );
            }
            else
            {
                throw new NotImplementedException();
            }


            return props;

        }
        catch (Throwable e)
        {
            e.printStackTrace(); //todo use logging library
            throw new RuntimeException(e);
        }
        finally
        {

        }
    }
}
