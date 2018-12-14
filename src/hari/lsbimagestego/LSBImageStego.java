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
    private int originalMessageBinaryLength;


//    OK TESTED
    public LSBImageStego(Mat coverImage){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;
    }


    public LSBImageStego(Mat coverImage , int originalMessageBinaryLength){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;
        this.originalMessageBinaryLength = originalMessageBinaryLength;
    }

//    OK TESTED
    private void doAdjust(){
        this.originalMessageBinaryLength = this.binaryStringMessage.length();
        if(this.binaryStringMessage.length()%2!=0){
            this.binaryStringMessage = this.binaryStringMessage + "0";
        }
    }

//    OK TESTED
    public boolean checkEncodePossibility(String message){
        this.binaryStringifyMessage(message);
        this.doAdjust();
        if(this.binaryStringMessage.length() > (this.coverImage_rows * this.coverImage_cols * NUMBER_OF_BITS_REPLACING)){
            return false;
        }
        return true;
    }


//    OK TESTED
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

//    OK TESTED
    private String getBeautifiedBinaryString(double value){
        String raw = Integer.toBinaryString((int)value);
        if(raw.length() < MAX_BINARY_PIXEL_INTENSITY_LENGTH){
            return String.join("", Collections.nCopies(MAX_BINARY_PIXEL_INTENSITY_LENGTH - raw.length(), "0")) + raw;
        }else{
            return raw;
        }
    }


//    OK TESTED
    public Mat encodeImage(String message){

//        Mat pic = this.coverImage.clone();
//        double[][][] PixelData = new double[this.coverImage_rows][this.coverImage_cols][3];
//        for(int rowCount = 0; rowCount <= this.coverImage_rows - 1 ; rowCount++){
//            for(int colCount = 0 ; colCount <= this.coverImage_cols - 1; colCount++){
//                PixelData[rowCount][colCount][0] = this.coverImage.get(rowCount , colCount)[0];
//                PixelData[rowCount][colCount][1] = this.coverImage.get(rowCount , colCount)[1];
//                PixelData[rowCount][colCount][2] = this.coverImage.get(rowCount , colCount)[2];
//            }
//        }
//        double rows = this.coverImage.size().height;
//        double cols = this.coverImage.size().width;

        System.out.println("Number of Rows : " + this.coverImage_rows);
        System.out.println("Number of Columns : " + this.coverImage_cols);


        int messageStringCounter = 0;
        for(int rowCount = 0 ; rowCount <= this.coverImage_rows -1 ; rowCount++){
            for(int colCount = 0; colCount <= this.coverImage_cols -1 ; colCount++){
                try{
//                    System.out.println("("+rowCount+" , "+colCount+" ) => "+this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]) +","+this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[1])+ "," + this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[2]));
                    if(messageStringCounter > this.binaryStringMessage.length() - NUMBER_OF_BITS_REPLACING ){
                        return this.coverImage;
                    }
                    String newLSBBits = this.binaryStringMessage.substring(messageStringCounter , messageStringCounter+NUMBER_OF_BITS_REPLACING);
                    System.out.println(">> "+messageStringCounter + " >> " + newLSBBits);
                    messageStringCounter+=NUMBER_OF_BITS_REPLACING;
                    System.out.println("ORIGINAL : "+this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]));
                    String modifiedBinaryString = this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).substring(0 , this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length() - NUMBER_OF_BITS_REPLACING) + newLSBBits;
                    System.out.println("MODIFIED : " + modifiedBinaryString);
                    double[] data = new double[3];
                    data[0] = Integer.parseInt(modifiedBinaryString , 2);
                    data[1] = this.coverImage.get(rowCount , colCount)[1];
                    data[2] = this.coverImage.get(rowCount , colCount)[2];
                    System.out.println("BEFORE : " + this.coverImage.get(rowCount , colCount)[0]);
                    this.coverImage.put(rowCount , colCount , data);
                    System.out.println("AFTER : " + this.coverImage.get(rowCount , colCount)[0]);
                }catch(Exception e){
                    System.out.println("Exception Handled");
                }
            }
        }
        System.out.println("Number of Rows : " + this.coverImage_rows);
        System.out.println("Number of Columns : " + this.coverImage_cols);

        return this.coverImage;
    }


//    OK TESTED
    private String getBinaryMessageFromImage(){


        StringBuilder secretMessage = new StringBuilder();

        int count = 0;

        for(int rowCount = 0 ; rowCount <= this.coverImage_rows -1 ; rowCount++){
            for(int colCount = 0 ; colCount <= this.coverImage_cols -1 ; colCount++){
                try{

                    if(this.originalMessageBinaryLength %2 ==0 && count==this.originalMessageBinaryLength){
                        System.out.println("DECODED MESSAGE @ getBinaryMessageFromImage() : " + secretMessage.toString());
                        return secretMessage.toString();
                    }
                    if(this.originalMessageBinaryLength %2 != 0 && count > (this.originalMessageBinaryLength - 1)){
                        System.out.println("DECODED MESSAGE @ getBinaryMessageFromImage() : " + secretMessage.toString());
                        System.out.println("DECODE MESSAGE @ getBinaryMessageDromImage() LENGTH : " + secretMessage.toString().length());
                        return secretMessage.toString();
                    }

                    System.out.println("GOT : " + this.coverImage.get(rowCount , colCount)[0]);
                    System.out.println("GOT STRING : " + this.getBeautifiedBinaryString(
                            this.coverImage.get(rowCount , colCount)[0]
                    ));

                    System.out.println("EXTRACTED : " + this.getBeautifiedBinaryString(
                            this.coverImage.get(rowCount , colCount)[0]
                    ).substring(
                            this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length() - NUMBER_OF_BITS_REPLACING  ,
                            this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length()
                    ));

                    secretMessage.append(
                            this.getBeautifiedBinaryString(
                                    this.coverImage.get(rowCount , colCount)[0]
                            ).substring(
                                         this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length() - NUMBER_OF_BITS_REPLACING  ,
                                         this.getBeautifiedBinaryString(this.coverImage.get(rowCount , colCount)[0]).length()
                            )
                    );
                    System.out.println("=> "+count+" => "+secretMessage.toString());
                    count += NUMBER_OF_BITS_REPLACING;


                }catch(Exception e){
                    System.out.println("Exception Handled");
                }
            }
        }

        System.out.println("DECODED MESSAGE @ getBinaryMessageFromImage() && : " + secretMessage.toString());
        return secretMessage.toString();

    }

    public void debug(){
        int i = 1;
        for(int rowCount = 0 ; rowCount <= this.coverImage_rows -1 ; rowCount++){
            for(int colCount = 0; colCount <= this.coverImage_cols -1 ; colCount++){
                try{
                    System.out.println(this.coverImage.get(rowCount , colCount)[0]);
                }catch(Exception e){
                    System.out.println("Exception Handled");
                }
            }
        }


    }

    public String decodeImage(Mat coverImage){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;

        String decodedBinaryMessage = this.getBinaryMessageFromImage();
//        decodedBinaryMessage = "10010001100101110110011011001101111010000010101111101111111001011011001100100";
        System.out.println("DEBUG COMPLETE");
        String originalMessage;

        if(this.originalMessageBinaryLength %2 ==0){
            System.out.println("DECODED BINARY MESSAGE : " + decodedBinaryMessage);
            originalMessage = this.decodeMessageFromBinary(decodedBinaryMessage);
        }else{
            System.out.println("DECODED BINARY MESSAGE : " + decodedBinaryMessage.substring(0,this.originalMessageBinaryLength - 7) + decodedBinaryMessage.substring(decodedBinaryMessage.length()-8 , decodedBinaryMessage.length() -1) );
            originalMessage = this.decodeMessageFromBinary(
              decodedBinaryMessage.substring(0,this.originalMessageBinaryLength - 7)
            );
            originalMessage  = originalMessage + this.decodeMessageFromBinary(
                    decodedBinaryMessage.substring(decodedBinaryMessage.length()-8 , decodedBinaryMessage.length() -1)
            );
        }

        System.out.println("DECODED MESSAGE ^: " + originalMessage);
        return originalMessage;
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
