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
import apiserver.services.images.gateways.jobs.filters.RaysJob;
import com.jhlabs.image.ArrayColormap;
import com.jhlabs.image.Colormap;
import com.jhlabs.image.Gradient;
import com.jhlabs.image.GrayscaleColormap;
import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.RaysFilter;
import com.jhlabs.image.SpectrumColormap;
import com.jhlabs.image.SplineColormap;
import org.apache.log4j.Logger;
import org.springframework.messaging.Message;

import java.awt.image.BufferedImage;

/**
 * User: mnimer
 * Date: 11/3/12
 */
public class RaysFilterService
{

    public Object doFilter(Message<?> message) throws MessageConfigException
    {
        RaysJob props = (RaysJob) message.getPayload();      //todo replace with real Model class
        float opacity = props.getOpacity();
        float strength = props.getStrength();
        float threshold = props.getThreshold();
        float angle = props.getAngle();
        float centerX = props.getCenterX();
        float centerY = props.getCenterY();
        float distance = props.getDistance();
        float rotation = props.getRotation();
        float zoom = props.getZoom();
        boolean raysOnly = props.getRaysOnly();

        String colorMapType = props.getColorMapType(); //gradient,grayscale,array,linear,spectrum,spline
        int[] gradientColors = props.getGradientColors();
        int[] xKnots = props.getxKnots();
        int[] yKnots = props.getyKnots();
        int[] arrayColors = props.getArrayColors();
        int linearColor1 = props.getLinearColor1();
        int linearColor2 = props.getLinearColor2();

        try
        {
            Colormap colorMap = null;
            if( colorMapType.equalsIgnoreCase("gradient"))
            {
                colorMap = new Gradient(gradientColors);
            }
            else if( colorMapType.equalsIgnoreCase("grayscale"))
            {
                colorMap = new GrayscaleColormap();
            }
            else if( colorMapType.equalsIgnoreCase("array"))
            {
                colorMap = new ArrayColormap(arrayColors);
            }
            else if( colorMapType.equalsIgnoreCase("linear"))
            {
                colorMap = new LinearColormap(linearColor1, linearColor2);
            }
            else if( colorMapType.equalsIgnoreCase("spectrum"))
            {
                colorMap = new SpectrumColormap();
            }
            else if( colorMapType.equalsIgnoreCase("spline"))
            {
                colorMap = new SplineColormap(xKnots, yKnots);
            }



            //run filter
            RaysFilter filter = new RaysFilter();
            filter.setOpacity(opacity);
            filter.setRaysOnly(raysOnly);
            filter.setStrength(strength);
            filter.setThreshold(threshold);
            filter.setAngle(angle);
            filter.setCentreX(centerX);
            filter.setCentreY(centerY);
            filter.setDistance(distance);
            filter.setRotation(rotation);
            filter.setZoom(zoom);
            filter.setColormap(colorMap);
            BufferedImage bufferedImage = props.getBufferedImage();
            BufferedImage outFile = filter.filter(bufferedImage, null);

            props.setBufferedImage(outFile);
            return props;
        }
        catch (Throwable e)
        {
            //log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
