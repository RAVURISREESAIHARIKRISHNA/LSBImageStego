package hari.lsbimagestego;



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

    /**
     *
     * @param coverImage This is a Mat Object.
     *                   DO NOT USE JPEG(because it uses Lossy Compression)
     *                   USE PNG or BITMAP
     *
     * @description This sets the desired Parameters
     */
    public LSBImageStego(Mat coverImage){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;
    }

    /**
     *
     * @param coverImage This is a Mat Object
     * @param originalMessageBinaryLength Included for Debugging Purposes
     *@deprecated ******* !!! ONLY DEBUGGING !!! *************
     */
    public LSBImageStego(Mat coverImage , int originalMessageBinaryLength){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;
        this.originalMessageBinaryLength = originalMessageBinaryLength;
    }

//    OK TESTED

    /**
     * @description This method adjusts the Binary string of the Message, such that it will
     * have an even length.It adjusts and stores it as a member of the class.
     */
    private void doAdjust(){
        this.originalMessageBinaryLength = this.binaryStringMessage.length();
        if(this.binaryStringMessage.length()%2!=0){
            this.binaryStringMessage = this.binaryStringMessage + "0";
        }
    }


    /**
     * @description This Encodes the Binary of the Original Message Length in the Image
     */
    private void encodeOriginalMessageBinaryLength(){
        String binary_of_originalMessageBinaryLength = Integer.toBinaryString(this.originalMessageBinaryLength);
        if(binary_of_originalMessageBinaryLength.length()%2 !=0){
            binary_of_originalMessageBinaryLength = "0" + binary_of_originalMessageBinaryLength;
        }
        int remaining = binary_of_originalMessageBinaryLength.length();
        String newLsbBits;
        for(int col = this.coverImage_cols -1 ; col >=0 ; col--){
            if(remaining > 0){
                newLsbBits = binary_of_originalMessageBinaryLength.substring( remaining - NUMBER_OF_BITS_REPLACING , remaining );
                remaining -= NUMBER_OF_BITS_REPLACING;
//            System.out.println("ORIGINAL : "+this.getBeautifiedBinaryString(this.coverImage.get( this.coverImage_rows -1,col )[0]));

            }else{
                newLsbBits = String.join("", Collections.nCopies(NUMBER_OF_BITS_REPLACING, "0"));
            }
            String modifiedBinaryString = this.getBeautifiedBinaryString(this.coverImage.get( this.coverImage_rows -1,col )[0]).substring(0 , this.getBeautifiedBinaryString(this.coverImage.get( this.coverImage_rows -1,col )[0]).length() - NUMBER_OF_BITS_REPLACING) + newLsbBits;
//            System.out.println("");
            double[] data = new double[3];
            data[0] = Integer.parseInt(modifiedBinaryString , 2);
            data[1] = this.coverImage.get( this.coverImage_rows -1,col )[1];
            data[2] = this.coverImage.get( this.coverImage_rows -1,col )[2];
//                System.out.println("BEFORE : " + this.coverImage.get( this.coverImage_rows -1,col )[0]);
            this.coverImage.put( this.coverImage_rows -1,col  , data);

        }
    }

    /**
     * @description This EXTRACTS the OriginalMessageBinaryLength from Image and sets the Property of the Mat Object
     */
    private void setOriginalMessageBinaryLength(){
        StringBuilder sb = new StringBuilder();

        for(int col = 0 ; col <= this.coverImage_cols - 1 ; col++){
            sb.append(
                    this.getBeautifiedBinaryString(
                            this.coverImage.get(this.coverImage_rows -1 , col)[0]
                    ).substring(
                            this.getBeautifiedBinaryString(this.coverImage.get(this.coverImage_rows -1 , col)[0]).length() - NUMBER_OF_BITS_REPLACING  ,
                            this.getBeautifiedBinaryString(this.coverImage.get(this.coverImage_rows -1 , col)[0]).length()
                    )
            );
        }
        this.originalMessageBinaryLength = Integer.parseInt(sb.toString() , 2);
    }

//    OK TESTED

    /**
     *
     * @param message This is the Secret Message which has to be encoded.
     *
     *
     * @return It returns a boolean value indicating the feasibility of encoding
     * it using the given Cover Image
     *
     * @description This Method Has to be Called COMPULSORILY Before calling the method to Encode
     */
    public boolean checkEncodePossibility(String message){
        this.binaryStringifyMessage(message);
        this.doAdjust();
        if(this.binaryStringMessage.length() > ((this.coverImage_rows - 1 )* this.coverImage_cols * NUMBER_OF_BITS_REPLACING)){
            return false;
        }
        return true;
    }


//    OK TESTED

    /**
     *
     * @param message This is the Secret Message String which has to be Encoded.
     *
     *
     * @description This method converts the message into equivalent binary string and
     * stores it as a member of the class.This uses default ASCII codes and makes
     * binary strings of each character of length 7
     */
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


    /**
     * Loading OpenCV
     */
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

//    OK TESTED

    /**
     *
     * @param value This is normal Double value of Pixel Intensity
     * @return It returns a binary String which is beautified(will have length 8 for Sure)
     */
    private String getBeautifiedBinaryString(double value){
        String raw = Integer.toBinaryString((int)value);
        if(raw.length() < MAX_BINARY_PIXEL_INTENSITY_LENGTH){
            return String.join("", Collections.nCopies(MAX_BINARY_PIXEL_INTENSITY_LENGTH - raw.length(), "0")) + raw;
        }else{
            return raw;
        }
    }


//    OK TESTED

    /**
     *
     * @param message This is the Secret Message which has to be encoded.
     *
     * @return  Returns Mat Object of the Encoded Image.
     *
     * @description Call this Method to Encode Secret Message in the Cover Image.
     *
     * @dependency checkEncodePossibility(String message) HAS TO BE CALLED BEFORE CALLING THIS METHOD
     */
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

        this.encodeOriginalMessageBinaryLength();

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

    /**
     *
     * @return It returns message as BinaryString from the Encoded Image
     */
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

    /**
     * @deprecated This is used for Debugging Purpose.
     */
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

    /**
     *
     * @param coverImage It takes the Encoded Image
     * @return It returns String of the Secret Message
     *
     * @description Call this method to Decode the Encoded Image
     */
    public String decodeImage(Mat coverImage){
        this.coverImage = coverImage;
        this.coverImage_rows = (int)coverImage.size().height;
        this.coverImage_cols = (int)coverImage.size().width;

        this.setOriginalMessageBinaryLength();

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

    /**
     *
     * @param binary It takes BinaryString of the Secret Message
     * @return  It returns the Secret Message String
     */
    private String decodeMessageFromBinary(String binary){

        StringBuilder message = new StringBuilder();
//        7 is the Length of the Binary String of any ASCII Character
        for(int i = 0 ; i<=binary.length()-7; i+=7 ){
            message.append((char) Integer.parseInt(binary.substring(i,i+7) ,2));
        }

        System.out.println("MESSAGE DECODED : " + message.toString());

        return  message.toString();
    }
}
