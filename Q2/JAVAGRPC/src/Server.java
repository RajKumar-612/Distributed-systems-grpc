import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    public static void main(String[] args) {
        // Define port number
        int portNumber = 7061;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ImageRequestHandler(clientSocket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class ImageRequestHandler implements Runnable {
    private final Socket clientSocket;

    public ImageRequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream writer = clientSocket.getOutputStream();

            // Receive keyword from client
            String keyword = reader.readLine();
            System.out.println("Received keyword from client: " + keyword);

            // Get list of image paths based on keyword
            List<String> imagePaths = Files.list(Paths.get("/app/Images"+ keyword))
                    .map(Path::toString)
                    .collect(Collectors.toList());

            // Check if image paths list is not empty, then send a random image to client
            if (!imagePaths.isEmpty()) {
                int randomIndex = new Random().nextInt(imagePaths.size());
                Path selectedImagePath = Paths.get(imagePaths.get(randomIndex));
                byte[] imageData = Files.readAllBytes(selectedImagePath);

                // Send image path and data to client
                writer.write(selectedImagePath.toString().getBytes());
                writer.write(imageData);
                writer.flush();

                // Log selected image file name
                System.out.println("Sent image: " + selectedImagePath.getFileName());

            } else {
                System.err.println("No images found for the keyword: " + keyword);
            }

            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
