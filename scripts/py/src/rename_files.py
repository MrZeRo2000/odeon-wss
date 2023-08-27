# rename_files.py

"""
Description: rename files according to the list
Arguments:
    file_names: file containing list of file names
    folder_name: folder to contain generated files
"""

import argparse
import logging
import os
import re
from logger import get_logger
from file_utils import validate_file_name_and_path, get_file_lines


class FileRenamer:
    MEDIA_FILE_EXTENSIONS = "AVI|M4V|MKV|MP4|MPG|VOB".split("|")

    def __init__(self, file_names: str, folder_name: str, display: bool, logger: logging.Logger):
        self.file_names = file_names
        self.folder_name = folder_name
        self.display = display
        self.name_parser = re.compile(r"^\d{2,3}\s\S+")
        self.logger = logger

    def get_media_file_names(self) -> list[str]:
        return sorted([f for f in os.listdir(self.folder_name)
                       if os.path.isfile(os.path.join(self.folder_name, f))
                       and f.upper().split(".")[-1] in FileRenamer.MEDIA_FILE_EXTENSIONS])

    def rename_files(self, new_names: list[str], file_names: list[str]):
        horizontal_delimiter = "".join("*" for _ in range(max([len(f) + 6 for f in file_names])))
        for idx, new_name in enumerate(new_names):
            file_name_from = os.path.join(self.folder_name, file_names[idx])

            # cleanup name
            new_name = new_name.replace("?", "")

            if self.name_parser.match(new_name) is None:
                new_name_formatted = f"{idx + 1:02d} {new_name}" if len(new_names) < 100 else f"{idx + 1:03d} {new_name}"
                rename_mode = "Name formatted by index"
            else:
                new_name_formatted = new_name
                rename_mode = "New name from file"
            new_file_name_formatted = f"{new_name_formatted}.{file_names[idx].split('.')[-1]}" 
            file_name_to = os.path.join(self.folder_name, new_file_name_formatted)

            if self.display:
                self.logger.info(horizontal_delimiter)
                self.logger.info(f"From: {file_names[idx]}")
                self.logger.info(f"  To: {new_file_name_formatted} ({rename_mode})")
            else:
                self.logger.info(f"Renaming {file_name_from} to {file_name_to}")
                os.replace(file_name_from, file_name_to)
                self.logger.info(f"{file_name_from} renamed")
        if self.display:
            self.logger.info(horizontal_delimiter)

    def execute(self):
        self.logger.info("Validating params")
        validate_file_name_and_path(self.file_names, self.folder_name)
        self.logger.info("Params validated")

        self.logger.info("Reading file names")
        file_names = get_file_lines(self.file_names)
        self.logger.info(f"File names obtained: {','.join(file_names)}")

        self.logger.info("Reading media file names")
        media_file_names = self.get_media_file_names()
        self.logger.info(f"Media file names obtained: {','.join(media_file_names)}")

        num_file_names = len(file_names)
        num_media_file_names = len(media_file_names)
        if num_file_names != num_media_file_names:
            raise Exception(
                f"File names to rename {num_file_names} "
                f"differs from media file names in folder {num_media_file_names}")

        self.logger.info("Renaming files")
        self.rename_files(file_names, media_file_names)
        self.logger.info("Files renamed")

    def __call__(self):
        self.execute()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("file_names", help="File with file names for renaming")
    parser.add_argument("--display", action='store_true', help="Display renamed files, no renaming")
    # parser.add_argument("folder_name", help="Folder with files to be renamed")    

    args = parser.parse_args()

    args_folder_name = os.path.dirname(args.file_names)
    FileRenamer(args.file_names, args_folder_name, args.display, get_logger("rename_files"))()
