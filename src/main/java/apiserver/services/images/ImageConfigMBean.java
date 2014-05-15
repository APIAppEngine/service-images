package apiserver.services.images;

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

import apiserver.exceptions.FactoryException;
import net.sf.ehcache.Cache;

/**
 * User: mnimer
 * Date: 10/19/12
 */
public interface ImageConfigMBean
{
    public Cache getCache() throws FactoryException;

    public String getMetadataLibrary();
    public void setMetadataLibrary(String metadataLibrary);


    public String getImageBorderPath();
    public void setImageBorderPath(String path);

    public String getImageBorderMethod();
    public void setImageBorderMethod(String method);


    public String getImageTextPath();
    public void setImageTextPath(String path);

    public String getImageTextMethod();
    public void setImageTextMethod(String method);


    public String getImageResizePath();
    public void setImageResizePath(String path);

    public String getImageResizeMethod();
    public void setImageResizeMethod(String method);


    public String getImageRotatePath();
    public void setImageRotatePath(String path);

    public String getImageRotateMethod();
    public void setImageRotateMethod(String method);
}
