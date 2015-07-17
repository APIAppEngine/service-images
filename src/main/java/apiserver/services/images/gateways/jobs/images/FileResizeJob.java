package apiserver.services.images.gateways.jobs.images;

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

import apiserver.ApiServerConstants;
import apiserver.jobs.IProxyJob;
import apiserver.services.images.gateways.jobs.ImageDocumentJob;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mikenimer
 * Date: 7/21/13
 */
public class FileResizeJob extends ImageDocumentJob implements IProxyJob
{
    private Integer width;
    private Integer height;
    private String interpolation = "bicubic";
    private Boolean scaleToFit = false;
    private Boolean returnAsBase64 = false;
    private String format;

    private ResponseEntity CFResponse;


    public Integer getWidth()
    {
        return width;
    }


    public void setWidth(Integer width)
    {
        this.width = width;
    }


    public Integer getHeight()
    {
        return height;
    }


    public void setHeight(Integer height)
    {
        this.height = height;
    }


    public String getInterpolation()
    {
        return interpolation;
    }


    public void setInterpolation(String interpolation)
    {
        this.interpolation = interpolation;
    }


    public Boolean getScaleToFit()
    {
        return scaleToFit;
    }


    public void setScaleToFit(Boolean scaleToFit)
    {
        this.scaleToFit = scaleToFit;
    }


    public String getFormat() {
        return format;
    }


    public void setFormat(String format) {
        this.format = format;
    }


    @Override public ResponseEntity getHttpResponse()
    {
        return CFResponse;
    }


    @Override public void setHttpResponse(ResponseEntity CFResponse)
    {
        this.CFResponse = CFResponse;
    }


    @Override public Map getArguments()
    {
        Map props = new HashMap();

        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //ImageIO.write(getBufferedImage(), "png", baos);
        //props.put(IMAGE, baos.toByteArray());
        props.put(ApiServerConstants.IMAGE, getDocument() );
        props.put(ApiServerConstants.CONTENT_TYPE, getDocument().getContentType() );
        props.put(ApiServerConstants.FILE_NAME, getDocument().getFileName() );
        props.put(ApiServerConstants.FORMAT, this.getFormat());
        props.put(ApiServerConstants.SCALE_TO_FIT, getScaleToFit() );
        props.put(ApiServerConstants.INTERPOLATION, getInterpolation());
        props.put(ApiServerConstants.HEIGHT, getHeight());
        props.put(ApiServerConstants.WIDTH, getWidth());
        return props;
    }


}
