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
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * User: mnimer
 * Date: 9/18/12
 */
@Component
@ManagedResource(objectName = "com.apiserver:name=ImageApi")
public class ImageConfigMBeanImpl implements ImageConfigMBean
{
    // Message params Arguments
    public static String FILE = "image";
    public static String MASK_FILE = "__maskfile";
    public static String CONTENT_TYPE = "__contentType";
    public static String ORIGINAL_FILE_NAME = "__originalFileName";
    public static String NAME = "__name";
    public static String SIZE = "__size";
    public static String KEY = "__key";
    public static String MASK_CACHEID = "__maskKey";
    public static String RESULT = "__result";
    public static String TEXT = "text";
    public static String TIME_TO_LIVE = "timeToLive";
    public static String ANGLE = "angle";
    public static String INTERPOLATION = "interpolation";
    public static String SCALE_TO_FIT = "scaleToFit";
    public static String COLOR = "color";
    public static String THICKNESS = "thickness";
    public static String FONT_SIZE = "fontSize";
    public static String FONT_STYLE = "fontStyle";
    public static String X = "x";
    public static String Y = "y";
    public static String DREW_METADATA_EXTRACTOR = "drewMetadataExtractor";


    private String cacheName = "imageApiCache";
    private String metadataLibrary = ImageConfigMBeanImpl.DREW_METADATA_EXTRACTOR;
    private Map<String, Cache> imageApiCache = new HashMap<String, Cache>();


    /**
     * ColdFusion CFC Paths
     */

    //Image Border Info
    private String imageBorderPath = "api-image.cfc?method=addBorder";
    private String imageBorderMethod = "POST";
    //Image Text Info
    private String imageTextPath = "api-image.cfc?method=addText";
    private String imageTextMethod = "POST";
    //Image Resize Info
    private String imageResizePath = "api-image.cfc?method=resizeImage";
    private String imageResizeMethod = "POST";
    //Image Resize Info
    private String imageRotatePath = "api-image.cfc?method=rotateImage";
    private String imageRotateMethod = "POST";



    public ImageConfigMBeanImpl()
    {

    }



    public Cache getCache() throws FactoryException
    {
        if (imageApiCache.get(getCacheName()) == null)
        {
            Cache _cache = new Cache(new CacheConfiguration(getCacheName(), 1000).eternal(true).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.CLOCK));
            _cache.initialise();

            imageApiCache.put(getCacheName(), _cache);
        }
        return imageApiCache.get(getCacheName());
    }


    @ManagedAttribute(description = "get cache name for the system image collection")
    public String getCacheName()
    {
        return cacheName;
    }


    @ManagedAttribute(description = "set cache name for the system image collection, default is: 'imageApiCache'")
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }


    @ManagedAttribute(description = "get metadata library")
    public String getMetadataLibrary()
    {
        return metadataLibrary;
    }


    @ManagedAttribute(description = "set metadata library, possible values are: 'drewMetadataExtractor'", persistPolicy = "OnUpdate")
    public void setMetadataLibrary(String metadataLibrary)
    {
        this.metadataLibrary = metadataLibrary;
    }


    @ManagedAttribute(description = "set Image Border CFC http path")
    public String getImageBorderPath()
    {
        return imageBorderPath;
    }


    @ManagedAttribute(description = "set Image Border CFC http path", persistPolicy = "OnUpdate")
    public void setImageBorderPath(String path)
    {
        imageBorderPath = path;
    }


    @ManagedAttribute(description = "set Image Border CFC http method")
    public String getImageBorderMethod()
    {
        return imageBorderMethod;
    }


    @ManagedAttribute(description = "set Image Border CFC http method", persistPolicy = "OnUpdate")
    public void setImageBorderMethod(String method)
    {
        imageBorderMethod = method;
    }


    @ManagedAttribute(description = "set Image Text CFC http path")
    public String getImageTextPath()
    {
        return imageTextPath;
    }


    @ManagedAttribute(description = "set Image Text CFC http path", persistPolicy = "OnUpdate")
    public void setImageTextPath(String path)
    {
        imageTextPath = path;
    }


    @ManagedAttribute(description = "set Image Text CFC http method")
    public String getImageTextMethod()
    {
        return imageTextMethod;
    }


    @ManagedAttribute(description = "set Image Text CFC http method", persistPolicy = "OnUpdate")
    public void setImageTextMethod(String method)
    {
        imageTextMethod = method;
    }


    @ManagedAttribute(description = "set Image Resize CFC http path")
    public String getImageResizePath()
    {
        return imageResizePath;
    }


    @ManagedAttribute(description = "set Image Resize CFC http path", persistPolicy = "OnUpdate")
    public void setImageResizePath(String path)
    {
        imageResizePath = path;
    }


    @ManagedAttribute(description = "set Image Resize CFC http method")
    public String getImageResizeMethod()
    {
        return imageResizeMethod;
    }


    @ManagedAttribute(description = "set Image Resize CFC http method", persistPolicy = "OnUpdate")
    public void setImageResizeMethod(String method)
    {
        imageResizeMethod = method;
    }


    @ManagedAttribute(description = "set Image Rotate CFC http path")
    public String getImageRotatePath()
    {
        return imageRotatePath;
    }


    @ManagedAttribute(description = "set Image Rotate CFC http path", persistPolicy = "OnUpdate")
    public void setImageRotatePath(String path)
    {
        imageRotatePath = path;
    }


    @ManagedAttribute(description = "set Image Rotate CFC http method")
    public String getImageRotateMethod()
    {
        return imageRotateMethod;
    }


    @ManagedAttribute(description = "set Image Rotate CFC http method", persistPolicy = "OnUpdate")
    public void setImageRotateMethod(String method)
    {
        imageRotateMethod = method;
    }
}
