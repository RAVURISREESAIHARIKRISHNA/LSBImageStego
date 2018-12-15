# LSBImageStego



[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

```LSBImageStego``` is a Java Library for LSB Image Steganography using *OpenCV*.This replaces 2 bits from LSB of the pixel intensity value of single channel,with that of the Secret Message that has to be encoded.
You can view the process as follows.

![Image Steganography](https://i.ibb.co/HgZYGtt/Screenshot-from-2018-12-15-11-07-53.png)




![LSB Image Steganography](https://i.ibb.co/TgZp4tJ/Screenshot-from-2018-12-15-10-43-43.png)

  

# Usage #

- While Encoding or Decoding you should pass the coverImage(```Mat``` OBject) or the EncodedImage(```Mat``` Object) respectively.

### Encoding ###
1. You should call ```checkEncodePossibility()``` before Encoding the Secret Message.This is *compulsory*.The function ```checkEncodePossibility()``` checks the possibility of encoding the Secret Message in the given Cover Image
2. If encoding is possible,then you have to call ```encodeImage()``` method,which inturn returns the Encoded Image (```Mat``` Object).

### Decoding ###
1. You have to pass the Encoded Image (```Mat``` object) which was encoded using the above Encoding algorithm to the method ```decodeImage()``` ,which returns the corresponding Decoded message.


