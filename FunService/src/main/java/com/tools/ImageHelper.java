package com.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: lancepoehler
 * Date: 3/20/13
 * Time: 11:39 PM
 * This is a tool for images
 */
public class ImageHelper {

    /**
     * save uploaded file to server location
     */
    public static void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {

        try {
            FileOutputStream out;

            int read;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
