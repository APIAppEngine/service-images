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
 * Date: 7/14/13
 */
public class BoxBlurJob extends ImageDocumentJob
{
    public final Log log = LogFactory.getLog(this.getClass());

    private int hRadius;
    private int vRadius;
    private int iterations;
    private Boolean preMultiplyAlpha;


    public int getHRadius()
    {
        return hRadius;
    }


    public void setHRadius(int hRadius)
    {
        this.hRadius = hRadius;
    }


    public int getVRadius()
    {
        return vRadius;
    }


    public void setVRadius(int vRadius)
    {
        this.vRadius = vRadius;
    }


    public int getIterations()
    {
        return iterations;
    }


    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }


    public Boolean getPreMultiplyAlpha()
    {
        return preMultiplyAlpha;
    }


    public void setPreMultiplyAlpha(Boolean preMultiplyAlpha)
    {
        this.preMultiplyAlpha = preMultiplyAlpha;
    }
}
