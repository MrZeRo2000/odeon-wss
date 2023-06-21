# generate_files.py

"""
Description: generate file names with random content
Arguments:
    file_names: file containing list of file names
    folder_name: folder to contain generated files
"""

import argparse
import os
import random
import string
from logger import get_logger
from file_utils import validate_file_name_and_path, get_file_lines


class FileGenerator:
    def __init__(self, file_names: str, folder_name: str, logger):
        self.file_names = file_names
        self.folder_name = folder_name
        self.logger = logger

    def generate_files(self, file_names: list[str]):
        for file_name in file_names:
            self.logger.debug(f"Generating {file_name}")
            with open(os.path.join(self.folder_name, file_name), 'w+') as f:
                f.write(''.join(random.choices(string.ascii_uppercase + string.digits, k=random.randint(10, 100))))
            self.logger.debug(f"File {file_name} generation completed")

    def execute(self):
        self.logger.info("Validating params")
        validate_file_name_and_path(self.file_names, self.folder_name)
        self.logger.info("Params validated")

        self.logger.info("Reading file names")
        files = get_file_lines(self.file_names)
        self.logger.info(f"File names obtained: {str(files)}")

        self.logger.info("Generating files")
        self.generate_files(files)
        self.logger.info("Files generated")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("file_names", help="File with file names to be generated")
    parser.add_argument("folder_name", help="Folder to contain generated files")

    args = parser.parse_args()

    FileGenerator(args.file_names, args.folder_name, get_logger("generate_files")).execute()
