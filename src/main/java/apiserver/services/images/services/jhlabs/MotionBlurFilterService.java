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
import apiserver.services.images.gateways.jobs.filters.MotionBlurJob;
import com.jhlabs.image.MotionBlurFilter;
import org.apache.log4j.Logger;
import org.springframework.messaging.Message;

import java.awt.image.BufferedImage;

/**
 * User: mnimer
 * Date: 11/2/12
 */
public class MotionBlurFilterService
{

    public Object doFilter(Message<?> message) throws MessageConfigException
    {
        MotionBlurJob props = (MotionBlurJob) message.getPayload();

        float angle = props.getAngle();
        float distance = props.getDistance();
        float rotation = props.getRotation();
        boolean wrapEdges = props.isWrapEdges();
        float zoom = props.getZoom();

        try
        {

            //run filter
            MotionBlurFilter filter = new MotionBlurFilter();
            filter.setAngle(angle);
            filter.setDistance(distance);
            filter.setRotation(rotation);
            filter.setWrapEdges(wrapEdges);
            filter.setZoom(zoom);


            BufferedImage bufferedImage = props.getBufferedImage();
            if( bufferedImage == null )
            {
                throw new MessageConfigException(MessageConfigException.MISSING_PROPERTY);
            }

            BufferedImage outFile = filter.filter(bufferedImage, null);

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
