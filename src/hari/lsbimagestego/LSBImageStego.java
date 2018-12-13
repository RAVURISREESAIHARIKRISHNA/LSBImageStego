package hari.lsbimagestego;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.Collections;

public class LSBImageStego {

    private final int MAX_BINARY_PIXEL_INTENSITY_LENGTH = 8;

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private String getBeautifiedBinaryString(double value){
        String raw = Integer.toBinaryString((int)value);
        if(raw.length() < MAX_BINARY_PIXEL_INTENSITY_LENGTH){
            return String.join("", Collections.nCopies(MAX_BINARY_PIXEL_INTENSITY_LENGTH - raw.length(), "0")) + raw;
        }else{
            return raw;
        }
    }


    public boolean encodeImage(Mat coverImage , String message){
        double rows = coverImage.size().height;
        double cols = coverImage.size().width;

        System.out.println("Number of Rows : " + rows);
        System.out.println("Number of Columns : " + cols);



        for(int rowCount = 1 ; rowCount <= rows -1 ; rowCount++){
            for(int colCount = 1; colCount <= cols -1 ; colCount++){
                try{
                    System.out.println("("+rowCount+" , "+colCount+" ) => "+this.getBeautifiedBinaryString(coverImage.get(rowCount , colCount)[0]) +","+this.getBeautifiedBinaryString(coverImage.get(rowCount , colCount)[1])+ "," + this.getBeautifiedBinaryString(coverImage.get(rowCount , colCount)[2]));

                }catch(Exception e){
                    System.out.println("Exception Handled");
                }
            }
        }
        System.out.println("Number of Rows : " + rows);
        System.out.println("Number of Columns : " + cols);

        return true;
    }
}
