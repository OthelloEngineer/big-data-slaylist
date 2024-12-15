import os
import subprocess

# Path to the .proto file
proto_file = "playlist.proto"
current_dir = os.path.dirname(os.path.abspath(__file__))
proto_file_path = os.path.join(current_dir, proto_file)

# Output directory for the generated Python file
output_dir = os.path.abspath(os.path.join(current_dir, "../spotify-api"))

# Ensure the output directory exists
os.makedirs(output_dir, exist_ok=True)

# Command to generate Python classes using protoc
protoc_command = [
    "protoc", 
    f"--proto_path={current_dir}",  # Directory of the .proto file
    f"--python_out={output_dir}",  # Directory for the generated Python file
    proto_file_path               # The .proto file to process
]

try:
    # Execute the command
    subprocess.run(protoc_command, check=True)
    print(f"Python classes generated successfully in: {output_dir}")
except FileNotFoundError:
    print("Error: 'protoc' is not installed or not in your PATH.")
    print("Please install the Protocol Buffers compiler.")
except subprocess.CalledProcessError as e:
    print("Error during generation process:")
    print(e)
