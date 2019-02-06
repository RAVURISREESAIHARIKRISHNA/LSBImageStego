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

        Mat mat = imageCodecs.imread("C:\\Users\\HARI\\Desktop\\test.png");

        LSBImageStego obj = new LSBImageStego(mat);
        String message = "flhfjdslghljsfkghkhj;ksfmb;lDSBJOGlsdkbn:>";
        if(obj.checkEncodePossibility(message)){
            System.out.println("OK ENCODABLE");
        }else{
            System.out.println("NO");
        }
        Mat encodedImage = obj.encodeImage(message).clone();
        imageCodecs.imwrite("C:\\Users\\HARI\\Desktop\\test_ENCODED.png" ,encodedImage );


        Mat mat2 = imageCodecs.imread("C:\\Users\\HARI\\Desktop\\test_ENCODED.png");
//        LSBImageStego obj2 = new LSBImageStego(mat2);
//        obj.debug();
        obj.decodeImage(mat2);

    }

}
