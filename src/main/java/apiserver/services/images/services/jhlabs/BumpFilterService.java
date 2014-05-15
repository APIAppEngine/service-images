package apiserver.services.images.services.jhlabs;


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

import apiserver.exceptions.MessageConfigException;
import apiserver.services.images.gateways.jobs.filters.BumpJob;
import com.jhlabs.image.BumpFilter;
import org.apache.log4j.Logger;
import org.springframework.messaging.Message;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * This filter does a simple convolution which emphasises edges in an image.
 *
 * User: mnimer
 * Date: 10/31/12
 */
public class BumpFilterService
{

    public Object doFilter(Message<?> message) throws MessageConfigException
    {
        BumpJob props = (BumpJob) message.getPayload();

        int edgeAction = props.getEdgeAction();
        boolean useAlpha = props.isUseAlpha();
        float[] embossMatrix = props.getMatrix();

        try
        {

            // calculate
            int rows = new Double(Math.sqrt( new Integer(embossMatrix.length).doubleValue() )).intValue();
            int cols = new Double(Math.sqrt( new Integer(embossMatrix.length).doubleValue() )).intValue();

            //run filter
            BumpFilter filter = new BumpFilter();
            filter.setEdgeAction(edgeAction);
            filter.setUseAlpha(useAlpha);
            filter.setKernel( new Kernel(rows, cols, embossMatrix));

            BufferedImage bufferedImage = props.getBufferedImage();
            if( bufferedImage == null )
            {
                throw new MessageConfigException(MessageConfigException.MISSING_PROPERTY);
            }

            BufferedImage outFile = filter.filter( bufferedImage, null );

            props.setBufferedImage(outFile);
            return message;
        }
        catch (Throwable e)
        {
            //log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
