
# Project Title
Image Processing 
<br>
## Description

The provided java code takes an image, finds the average color of given size of pixels square and replaces it in real time.
<br>
<br>
## Dependencies

 - Java Development Kit (JDK)
 - Java Advanced Imaging (JAI) library (for advanced image processing features)
 - Swing
<br><br>
## How to run
You may use any IDE, as arguments enter the image file name, pixel size, and threading option (S for single thread, M for the number of available cores) in the following order given. 
<br><br>
## Project Structure
ImageProcessing.java: The main Java file containing the image processing logic, should be in the main package.
<br><br>
## How it works
The application processes the input image by dividing it into blocks, calculating the average color of each block, and replacing the pixels in that block with the calculated average color. The process is done concurrently using multiple threads for improved performance.
<br>Two functions are used:<br>
   - processImageSection - threads call this function for processing the image, it extracts image colors, find their averages and replaces them.
   - scaleImage - images which exceed the screen size are formatted on the height or width
   - saveOriginal - if the image size is downscaled for visualizing reasons, the function saves the image based on unscaled size


