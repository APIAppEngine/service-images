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

/**
 * User: mikenimer
 * Date: 7/14/13
 */
public class MotionBlurJob extends ImageDocumentJob
{

    private float angle;
    private float distance;
    private float rotation;
    private boolean wrapEdges;
    private float zoom;


    public float getAngle()
    {
        return angle;
    }


    public void setAngle(float angle)
    {
        this.angle = angle;
    }


    public float getDistance()
    {
        return distance;
    }


    public void setDistance(float distance)
    {
        this.distance = distance;
    }


    public float getRotation()
    {
        return rotation;
    }


    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }


    public boolean isWrapEdges()
    {
        return wrapEdges;
    }


    public void setWrapEdges(boolean wrapEdges)
    {
        this.wrapEdges = wrapEdges;
    }


    public float getZoom()
    {
        return zoom;
    }


    public void setZoom(float zoom)
    {
        this.zoom = zoom;
    }
}
