package apiserver.services.images.services;

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
import apiserver.services.images.gateways.jobs.images.FileInfoJob;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mnimer
 * Date: 9/18/12
 */
public class ImageInfoService
{

    public Object execute(Message<?> message) throws IOException
    {

        FileInfoJob props = (FileInfoJob)message.getPayload();

        Map result = new HashMap();
        result.put(ApiServerConstants.WIDTH, props.getBufferedImage().getWidth());
        result.put(ApiServerConstants.HEIGHT, props.getBufferedImage().getHeight());

        // return results
        MessageBuilder mb = MessageBuilder.withPayload(result);
        mb.copyHeaders(message.getHeaders());
        return mb.build();
    }




}
