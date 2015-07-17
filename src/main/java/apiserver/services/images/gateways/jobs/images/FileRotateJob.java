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
public class FileRotateJob extends ImageDocumentJob implements IProxyJob
{
    private Integer angle = 0;
    private String format;

    private ResponseEntity CFResponse;


    public Integer getAngle()
    {
        return angle;
    }

    public void setAngle(Integer angle)
    {
        this.angle = angle;
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


    public Map getArguments()
    {
        Map props = new HashMap();

        props.put(ApiServerConstants.IMAGE, getDocument() );
        props.put(ApiServerConstants.CONTENT_TYPE, getDocument().getContentType() );
        props.put(ApiServerConstants.FILE_NAME, getDocument().getFileName() );
        props.put(ApiServerConstants.FORMAT, getFormat());
        props.put( ApiServerConstants.ANGLE, getAngle() );
        return props;
    }
}
