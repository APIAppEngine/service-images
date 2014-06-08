package apiserver.services.images.gateways.jobs;

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

import apiserver.services.cache.gateway.jobs.GetDocumentJob;
import apiserver.services.cache.model.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 *
 * User: mikenimer
 * Date: 10/24/13
 */
public class ImageDocumentJob extends GetDocumentJob
{
    public final Log log = LogFactory.getLog(this.getClass());

    private BufferedImage bufferedImage;



    /**
     * Convert the internal byte Array back into a BufferedImage file.
     * @return BufferedImage
     * @throws java.io.IOException if the bytes are not a valid image.
     */
    public BufferedImage getBufferedImage() throws IOException
    {
        if( bufferedImage != null )
        {
            return bufferedImage;
        }

        bufferedImage = ImageIO.read(new ByteArrayInputStream(this.getDocument().getFileBytes()));
        return bufferedImage;
    }


    public void setBufferedImage(BufferedImage bufferedImage) throws IOException
    {
        this.bufferedImage = bufferedImage;

        //update file bytes
        ((Document)this.getDocument()).setFile(bufferedImage);
    }

}
