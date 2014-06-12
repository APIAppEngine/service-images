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
import apiserver.services.images.gateways.jobs.ImageDocumentJob;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
public class FileBorderJob extends ImageDocumentJob
{

    private String color;
    private Integer thickness;
    private String format;

    private byte[] imageBytes;


    public String getColor()
    {
        return color;
    }


    public void setColor(String color)
    {
        this.color = color;
    }


    public Integer getThickness()
    {
        return thickness;
    }


    public void setThickness(Integer thickness)
    {
        this.thickness = thickness;
    }


    public String getFormat() {
        return format;
    }


    public void setFormat(String format) {
        this.format = format;
    }


    public byte[] getImageBytes() {
        return imageBytes;
    }


    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }


    public Map toMap()
    {
        Map props = new HashMap();
        try {
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //ImageIO.write(getBufferedImage(), "png", baos);
            //props.put(IMAGE, baos.toByteArray());
            props.put(ApiServerConstants.IMAGE, getBufferedImage() );
            props.put(ApiServerConstants.CONTENT_TYPE, getDocument().getContentType() );
            props.put(ApiServerConstants.FILE_NAME, getDocument().getFileName() );
        }catch(IOException e){}
        props.put(ApiServerConstants.COLOR, getColor());
        props.put(ApiServerConstants.THICKNESS, getThickness());
        return props;
    }
}
