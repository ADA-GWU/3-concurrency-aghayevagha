package main;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class ImageProcessing {
    public static void main(String[] args) {
        try {
            // Load the input variables
            //file name , pixel size, and S or M
            Scanner sc=new Scanner(System.in);

            String fileName=sc.nextLine();

            // Define the square size
            int squareSize = sc.nextInt();
            //initialize a new scanner to avoid problems for console input
            sc=new Scanner(System.in);

            //Number of threads
            String th=sc.nextLine();

            //load the image
            BufferedImage inputImage = ImageIO.read(new File(fileName));

            int originalWidth = inputImage.getWidth();
            int originalHeight = inputImage.getHeight();

            // Set the maximum width and height for display
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidthForDisplay = (int) screenSize.getWidth();
            int maxHeightForDisplay = (int) screenSize.getHeight();


            // Scale down the image if it exceeds the maximum display dimensions
            // the image can exceed the screen either on height or width or both, change which exceeds
            BufferedImage displayImage;
            if(originalHeight>maxHeightForDisplay && originalWidth> maxWidthForDisplay){
                displayImage = scaleImage(inputImage, maxWidthForDisplay, maxHeightForDisplay);}
            else if(originalHeight>maxHeightForDisplay ){
                 displayImage = scaleImage(inputImage, originalWidth, maxHeightForDisplay);}
            else if(originalWidth> maxWidthForDisplay){
                displayImage = scaleImage(inputImage, maxWidthForDisplay, originalHeight);}
            else { displayImage=inputImage;}

            // Create a BufferedImage for the output image
            BufferedImage outputImage=displayImage;
            // Get the dimensions of the input image
            int width = outputImage.getWidth();
            int height = outputImage.getHeight();


            // Create a Swing frame for displaying images
            JFrame frame = new JFrame("Average Color Image");
            JLabel label = new JLabel(new ImageIcon(outputImage));
            frame.add(label);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            // Specify the number of cores
            int numCores;
            if(th.equals("S")) numCores=1;
            else numCores= Runtime.getRuntime().availableProcessors();


            // Create an ExecutorService with the specified number of threads
            ExecutorService executorService = Executors.newFixedThreadPool(numCores);

            // Calculate the block size for each core
            int blockSize = height / numCores;

            // Iterate through the pixels in the input image
            for (int y = 0; y < height; y += blockSize) {
                final int startY = y;
                executorService.submit(() -> processImageSection(displayImage, outputImage, startY, squareSize, width, blockSize, label));
            }

            // Shut down the executor service and wait for all tasks to complete
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // Save the final output image
            File outputImageFile = new File("result.jpg");
            boolean b=ImageIO.write(outputImage, "jpg", outputImageFile);
            if(b) System.out.println("saved");
            else System.out.println("couldn't be saved");
        } catch ( IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    //create a function for replacing the pixels
    private static void processImageSection(BufferedImage inputImage, BufferedImage outputImage, int startY, int squareSize, int width, int blockSize, JLabel label) {
        for (int y = startY; y < startY + blockSize && y < inputImage.getHeight(); y += squareSize) {
            for (int x = 0; x < width; x += squareSize) {

                int avgRed = 0, avgGreen = 0, avgBlue = 0;
                int pixelCount = 0;
                //sum the colors
                for (int i = x; i < x + squareSize && i < width; i++) {
                    for (int j = y; j < y + squareSize && j < inputImage.getHeight(); j++) {
                        int rgb = inputImage.getRGB(i, j);
                        //by shifting and using AND operator we extract the colors
                        avgRed += (rgb >> 16) & 0xFF;
                        avgGreen += (rgb >> 8) & 0xFF;
                        avgBlue += rgb & 0xFF;
                        pixelCount++;
                    }
                }
                //find averages by normalizing
                avgRed /= pixelCount;
                avgGreen /= pixelCount;
                avgBlue /= pixelCount;

                int avgColor = (avgRed << 16) | (avgGreen << 8) | avgBlue;
                //replace the pixels with average color
                for (int i = x; i < x + squareSize && i < width; i++) {
                    for (int j = y; j < y + squareSize && j < inputImage.getHeight(); j++) {
                        outputImage.setRGB(i, j, avgColor);
                    }
                }

                // Update the GUI to show the updated image
                label.setIcon(new ImageIcon(outputImage));

                // Sleep for a short duration to slow down the process
                try {
                    Thread.sleep(50); // Sleep for 50 milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // Determine the image type; use TYPE_INT_ARGB if the original type is 0 (undefined)
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        // Scale the original image
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, type);

        // Create a Graphics2D object for drawing on the new BufferedImage
        Graphics2D g2d = scaledImage.createGraphics();

        // Draw the scaled image onto the new BufferedImage
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return scaledImage;
    }
}
