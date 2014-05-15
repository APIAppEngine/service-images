package apiserver.services.images.gateways.jobs.filters;

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

import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: mikenimer
 * Date: 9/22/13
 */
public class RaysJob extends ImageDocumentJob
{
    private final Log log = LogFactory.getLog(this.getClass());

    private Float opacity;
    private Float strength;
    private Float threshold;
    private Float angle;
    private Float centerX;
    private Float centerY;
    private Float distance;
    private Float rotation;
    private Float zoom;
    private Boolean raysOnly = false;
    private String colorMapType;
    private int[] gradientColors;
    private int[] xKnots;
    private int[] yKnots;
    private int[] arrayColors;
    private Integer linearColor1;
    private Integer linearColor2;


    public Float getOpacity()
    {
        return opacity;
    }


    public void setOpacity(Float opacity)
    {
        this.opacity = opacity;
    }


    public Float getStrength()
    {
        return strength;
    }


    public void setStrength(Float strength)
    {
        this.strength = strength;
    }


    public Float getThreshold()
    {
        return threshold;
    }


    public void setThreshold(Float threshold)
    {
        this.threshold = threshold;
    }


    public Float getAngle()
    {
        return angle;
    }


    public void setAngle(Float angle)
    {
        this.angle = angle;
    }


    public Float getCenterX()
    {
        return centerX;
    }


    public void setCenterX(Float centerX)
    {
        this.centerX = centerX;
    }


    public Float getCenterY()
    {
        return centerY;
    }


    public void setCenterY(Float centerY)
    {
        this.centerY = centerY;
    }


    public Float getDistance()
    {
        return distance;
    }


    public void setDistance(Float distance)
    {
        this.distance = distance;
    }


    public Float getRotation()
    {
        return rotation;
    }


    public void setRotation(Float rotation)
    {
        this.rotation = rotation;
    }


    public Float getZoom()
    {
        return zoom;
    }


    public void setZoom(Float zoom)
    {
        this.zoom = zoom;
    }


    public Boolean getRaysOnly()
    {
        return raysOnly;
    }


    public void setRaysOnly(Boolean raysOnly)
    {
        this.raysOnly = raysOnly;
    }


    public String getColorMapType()
    {
        return colorMapType;
    }


    public void setColorMapType(String colorMapType)
    {
        this.colorMapType = colorMapType;
    }


    public int[] getGradientColors()
    {
        return gradientColors;
    }


    public void setGradientColors(int[] gradientColors)
    {
        this.gradientColors = gradientColors;
    }


    public int[] getxKnots()
    {
        return xKnots;
    }


    public void setxKnots(int[] xKnots)
    {
        this.xKnots = xKnots;
    }


    public int[] getyKnots()
    {
        return yKnots;
    }


    public void setyKnots(int[] yKnots)
    {
        this.yKnots = yKnots;
    }


    public int[] getArrayColors()
    {
        return arrayColors;
    }


    public void setArrayColors(int[] arrayColors)
    {
        this.arrayColors = arrayColors;
    }


    public Integer getLinearColor1()
    {
        return linearColor1;
    }


    public void setLinearColor1(Integer linearColor1)
    {
        this.linearColor1 = linearColor1;
    }


    public Integer getLinearColor2()
    {
        return linearColor2;
    }


    public void setLinearColor2(Integer linearColor2)
    {
        this.linearColor2 = linearColor2;
    }
}
