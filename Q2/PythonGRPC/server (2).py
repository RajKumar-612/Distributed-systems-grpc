import grpc
import Image_pb2
import Image_pb2_grpc
import os
import random
import threading

from concurrent import futures

class ImageService(Image_pb2_grpc.ImageServiceServicer):
    
    # Function to retrieve a random image based on keyword
    def GetRandomImage(self, request, context):
        keyword = request.keyword
        keyword_folder_path = os.path.join('Images', keyword)

        if not os.path.exists(keyword_folder_path) or not os.path.isdir(keyword_folder_path):
            context.set_code(grpc.StatusCode.NOT_FOUND)
            context.set_details(f"Folder '{keyword}' not found")
            return Image_pb2.ImageResponse(image_data=b"")
            
        # Retrieve the list of image files associated with the provided keyword
        image_files = [image_file for image_file in os.listdir(keyword_folder_path) if os.path.isfile(os.path.join(keyword_folder_path, image_file))]
        
        # Select a random image from the list
        if image_files:
            selected_image = random.choice(image_files)
            
            # Read the selected image file and return its data
            with open(os.path.join(keyword_folder_path, selected_image), 'rb') as image_file:
                image_data = image_file.read()
            return Image_pb2.ImageResponse(image_data=image_data)
        else:
            context.set_code(grpc.StatusCode.NOT_FOUND)
            context.set_details(f"No image found in '{keyword}'")
            return Image_pb2.ImageResponse(image_data=b"")

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    Image_pb2_grpc.add_ImageServiceServicer_to_server(ImageService(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()
