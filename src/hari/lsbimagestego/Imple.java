package hari.lsbimagestego;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Imple {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args){
        Imgcodecs imageCodecs = new Imgcodecs();

        Mat mat = imageCodecs.imread("C:\\Users\\HARI\\Desktop\\test.jpg");

        LSBImageStego obj = new LSBImageStego();
        obj.encodeImage(mat , "Hello World");
    }

}
