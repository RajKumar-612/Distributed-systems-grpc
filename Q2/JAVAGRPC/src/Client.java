import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        // Define server container and port
        String serverContainer = "server-container8";
        int serverPort = 7061;
        try (Socket clientSocket = new Socket(serverContainer, serverPort);
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // Prompt user for input
            Scanner userInput = new Scanner(System.in);
            System.out.print("Enter a keyword: ");
            String keyword = userInput.nextLine();

            // Send data to server
            PrintWriter dataSender = new PrintWriter(outputStream, true);
            dataSender.println(keyword);

            // Receive image path and bytes count
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String imagePath = reader.readLine();
            System.out.println("Image path: " + imagePath);

            // Save received image
            String savePath = "received_image.jpg"; // Set desired file name
            FileOutputStream fileOutputStream = new FileOutputStream(savePath);

            // Read image data from the server and save it to file
            byte[] buffer = new byte[4096];
            int bytesRead;
            InputStream inputStream = clientSocket.getInputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            // Close resources
            fileOutputStream.close();
            userInput.close();
            System.out.println("Received and saved image: " + savePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
