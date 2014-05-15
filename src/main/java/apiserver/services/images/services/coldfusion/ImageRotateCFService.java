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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * User: mikenimer
 * Date: 8/26/13
 */
public class ImageRotateCFService
{
    private final Log log = LogFactory.getLog(this.getClass());


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
        FileRotateJob props = (FileRotateJob)message.getPayload();

        try
        {
            cfcPath = imageConfigMBean.getImageRotatePath();
            String method = imageConfigMBean.getImageRotateMethod();

            // extract properties
            Map<String, Object> methodArgs = coldFusionBridge.extractPropertiesFromPayload(props);


            // execute
            Object cfcResult = coldFusionBridge.invoke(cfcPath, method, methodArgs);

            // strip out the base64 string from the json packet
            //ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //String img = mapper.readValue((String)cfcResult, String.class);

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
