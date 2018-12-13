package hari.lsbimagestego;

//TODO:
//TODO  1)Should store Length of the BinaryString_OF_Message in Image
//TODo  2)Re-Calculate "Whether the Message can be Encoded or Not"
//TODO  3)Make changes to Decoding Algorithm

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.Collections;

public class LSBImageStego {

    private Mat coverImage;
    private int coverImage_rows;
    private int coverImage_cols;
    private String binaryStringMessage;
    private final int MAX_BINARY_PIXEL_INTENSITY_LENGTH = 8;
    private final int NUMBER_OF_BITS_REPLACING = 2;



    public LSBImageStego(Mat coverImage){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;
    }

    public boolean checkEncodePossibility(String message){
        this.binaryStringifyMessage(message);
        if(this.binaryStringMessage.length() > (this.coverImage_rows * this.coverImage_cols * 2)){
            return false;
        }
        return true;
    }

    private void binaryStringifyMessage(String message){
        StringBuilder binaryString = new StringBuilder();
        for(int i=0 ; i<= message.length()-1 ; i++){
            if(Integer.toBinaryString(message.charAt(i)).length() < 7){
//                String.join("", Collections.nCopies(7 -Integer.toBinaryString(message.charAt(i)).length() , "0"))
                binaryString.append(String.join("", Collections.nCopies(7 -Integer.toBinaryString(message.charAt(i)).length() , "0"))+Integer.toBinaryString(message.charAt(i)));
            }else{
                binaryString.append(Integer.toBinaryString(message.charAt(i)));

            }
        }
        System.out.println("Binary String of Message : " + binaryString.toString());
        System.out.println("BINARY MSG LENGTH : " +  binaryString.toString().length());

        this.binaryStringMessage = binaryString.toString();
    }



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


    public Mat encodeImage(String message){
//        double rows = this.coverImage.size().height;
//        double cols = this.coverImage.size().width;

        System.out.println("Number of Rows : " + this.coverImage_rows);
        System.out.println("Number of Columns : " + this.coverImage_cols);


        int messageStringCounter = 0;
        for(int rowCount = 1 ; rowCount <= this.coverImage_rows -1 ; rowCount++){
            for(int colCount = 1; colCount <= this.coverImage_cols -1 ; colCount++){
                try{
//                    System.out.println("("+rowCount+" , "+colCount+" ) => "+this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]) +","+this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[1])+ "," + this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[2]));
                    if(messageStringCounter >= this.binaryStringMessage.length() - NUMBER_OF_BITS_REPLACING - 1){
                        return this.coverImage;
                    }
                    String newLSBBits = this.binaryStringMessage.substring(messageStringCounter , messageStringCounter+NUMBER_OF_BITS_REPLACING);
                    messageStringCounter+=NUMBER_OF_BITS_REPLACING;
                    String modifiedBinaryString = this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).substring(0 , this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length() - NUMBER_OF_BITS_REPLACING);
                    double[] data = new double[3];
                    data[0] = Integer.parseInt(modifiedBinaryString , 2);
                    data[1] = this.coverImage.get(rowCount , colCount)[1];
                    data[2] = this.coverImage.get(rowCount , colCount)[2];

                    this.coverImage.put(rowCount , colCount , data);
                }catch(Exception e){
                    System.out.println("Exception Handled");
                }
            }
        }
        System.out.println("Number of Rows : " + this.coverImage_rows);
        System.out.println("Number of Columns : " + this.coverImage_cols);

        return this.coverImage;
    }


    public String decodeImage(Mat coverImage){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;

        StringBuilder secretMessage = new StringBuilder();

        for(int rowCount = 1 ; rowCount <= this.coverImage_rows -1 ; rowCount++){
            for(int colCount = 1; colCount <= this.coverImage_cols -1 ; colCount++){
                try{
                    secretMessage.append(this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).substring(this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length() - NUMBER_OF_BITS_REPLACING , this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length()));
                }catch(Exception e){
                    System.out.println("Exception Handled");
                }
            }
        }

        return this.decodeMessageFromBinary(secretMessage.toString());

    }

    private String decodeMessageFromBinary(String binary){
        StringBuilder message = new StringBuilder();
        for(int i = 0 ; i<=binary.length()-7; i+=7 ){
            message.append((char) Integer.parseInt(binary.substring(i,i+7) ,2));
        }

        System.out.println("MESSAGE DECODED : " + message.toString());

        return  message.toString();
    }
}
