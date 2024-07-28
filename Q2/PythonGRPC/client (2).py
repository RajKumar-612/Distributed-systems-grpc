import grpc
import Image_pb2
import Image_pb2_grpc

def retrieve_and_save_image():

    # Prompt user to input keyword for image retrieval
    user_keyword = input("Enter the keyword: ")

    # Establish connection with server container and port
    server_channel = grpc.insecure_channel('server-container:50051')

    # Create stub for Image service
    image_stub = Image_pb2_grpc.ImageServiceStub(server_channel)

    try:
        # Create request using user-provided keyword
        image_request = Image_pb2.ImageRequest(keyword=user_keyword)
        
        # Retrieve a random image based on the keyword
        image_response = image_stub.GetRandomImage(image_request)
        
        if image_response.image_data:
            # Save the received image in the container's folder
            save_path = "/app/received_image.jpg"
            with open(save_path, 'wb') as image_file:
                image_file.write(image_response.image_data)
            print("Image received and saved as 'received_image.jpg'")
        else:
            print("Image not found")
    except grpc.RpcError as rpc_error:
        if rpc_error.code() == grpc.StatusCode.NOT_FOUND:
            print(f"Image retrieval failed: {rpc_error.details()}")
        else:
            print(f"gRPC Error: {rpc_error.details()}")

if __name__ == '__main__':
    retrieve_and_save_image()
