import os
import subprocess

proto_file = "playlist.proto"
current_dir = os.path.dirname(os.path.abspath(__file__))
proto_file_path = os.path.join(current_dir, proto_file)

output_dir = os.path.abspath(os.path.join(current_dir, "../spotify-api"))
java_dir_rel = "ingestion-api\src\main\java\\big\data\ingestion\data"
java_dir = os.path.abspath(os.path.join(current_dir, f"../{java_dir_rel}"))

protoc_command_python = [
    "protoc", 
    f"--proto_path={current_dir}",  
    f"--python_out={output_dir}", # python_out
    f"--pyi_out={output_dir}", # python stubs
    proto_file_path               
]

java_command = [
    "protoc", 
    f"--proto_path={current_dir}",  
    f"--java_out={java_dir}", # java_out
    proto_file_path
]


try:
    # Execute the command
    subprocess.run(protoc_command_python, check=True)
    print(f"Python classes generated successfully in: {output_dir}")
    subprocess.run(java_command, check=True)
    print(f"Java classes generated successfully in: {java_dir}")
except FileNotFoundError:
    print("Error: 'protoc' is not installed or not in your PATH.")
    print("Please install the Protocol Buffers compiler.")
except subprocess.CalledProcessError as e:
    print("Error during generation process:")
    print(e)
