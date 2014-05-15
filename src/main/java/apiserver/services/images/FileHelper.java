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

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * User: mnimer
 * Date: 9/29/12
 */
public class FileHelper
{

    public static String fileName ( Object uploadedFile )
    {
        if( uploadedFile instanceof CommonsMultipartFile)
        {
            return ((CommonsMultipartFile)uploadedFile).getOriginalFilename();
        }
        else if( uploadedFile instanceof BufferedImage)
        {
            return null;
        }

        return null;
    }


    public static byte[] fileBytes ( Object uploadedFile ) throws IOException
    {

        if( uploadedFile instanceof CommonsMultipartFile)
        {
            return ((CommonsMultipartFile)uploadedFile).getBytes();
        }
        else if( uploadedFile instanceof BufferedImage)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(  (BufferedImage)uploadedFile, "jpg", baos);
            return baos.toByteArray();
        }

        return null;
    }





}
