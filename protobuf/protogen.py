import os
import subprocess

proto_file = "playlist.proto"
current_dir = os.path.dirname(os.path.abspath(__file__))
proto_file_path = os.path.join(current_dir, proto_file)

output_dir = os.path.abspath(os.path.join(current_dir, "../spotify-api"))

protoc_command = [
    "protoc", 
    f"--proto_path={current_dir}",  
    f"--python_out={output_dir}", # python_out
    f"--pyi_out={output_dir}", # python stubs
    # java_out
    f"--java_out={output_dir}",
    
    proto_file_path               
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
