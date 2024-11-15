# rename_files.py

"""
Description: rename files according to the list
Arguments:
    file_names: file containing list of file names
    levenstein(optional): use file name similarity for renaming (false by default)
    display(optional): display results only no renaming (false by default)
"""

import argparse
import logging
import os
import re
from collections.abc import Iterable
from collections import Counter

import Levenshtein

from logger import get_logger
from file_utils import validate_file_name_and_path, get_file_lines


class FileRenamer:
    MEDIA_FILE_EXTENSIONS = "AVI|M4V|MKV|MP4|MPG|VOB|WMV".split("|")

    def __init__(self, file_names: str, folder_name: str, use_levenstein: bool, display: bool, logger: logging.Logger):
        self.file_names = file_names
        self.folder_name = folder_name
        self.use_levenstein = use_levenstein
        self.display = display
        self.name_parser = re.compile(r"^\d{2,3}\s\S+")
        self.logger = logger

    def get_media_file_names(self) -> list[str]:
        return sorted([f for f in os.listdir(self.folder_name)
                       if os.path.isfile(os.path.join(self.folder_name, f))
                       and f.upper().split(".")[-1] in FileRenamer.MEDIA_FILE_EXTENSIONS])

    @staticmethod
    def find_most_similar(ls: Iterable[str], value: str) -> str:
        distances = {s: Levenshtein.ratio(value, s) for s in ls}
        return list({k for k, v in distances.items() if v == max(distances.values())})[0]

    def get_renaming_list(self, new_names: list[str], file_names: list[str]) -> list[tuple[str, str, str]]:
        result = []
        for idx, new_name in enumerate(new_names):
            file_name_from = file_names[idx]
            if self.use_levenstein:
                file_name_from = self.find_most_similar(file_names, new_name)

            # cleanup name
            new_name = new_name.replace("?", "").replace(r"/", "-").replace("\\", "-")

            if self.name_parser.match(new_name) is None:
                new_name_formatted = f"{idx + 1:02d} {new_name}" if len(new_names) < 100 else f"{idx + 1:03d} {new_name}"
                rename_mode = "Name formatted by index"
            else:
                new_name_formatted = new_name
                rename_mode = "New name from file"
            new_file_name_formatted = f"{new_name_formatted}.{file_name_from.split('.')[-1]}"
            file_name_to = new_file_name_formatted

            result.append((file_name_from, file_name_to, rename_mode))

        return result

    def validate_renaming_list(self, rn_list: list[tuple[str, str, str]]) -> None:
        rename_to_counter = Counter([v[0] for v in rn_list])
        rename_to_duplicates = [s[0] for s in rename_to_counter.most_common() if s[1] > 1]
        if len(rename_to_duplicates) > 0:
            self.logger.error("Duplicates in renaming list:")
            _ = [self.logger.error(f"  {d}") for d in rename_to_duplicates]
            raise ValueError("Duplicates in renaming list")

    def display_renaming_list(self, rn_list: list[tuple[str, str, str]]) -> None:
        max_from_len = max(len(v[0]) for v in rn_list)
        max_to_len = max(len(v[1]) for v in rn_list)
        max_mode_len = max(len(v[2]) for v in rn_list)

        horizontal_delimiter = '*' * (max_from_len + max_to_len + max_mode_len + 6)
        self.logger.info(horizontal_delimiter)

        for v in rn_list:
            self.logger.info(f"{v[0]}{' '*(max_from_len - len(v[0]))} | {v[1]}{' '*(max_to_len - len(v[1]))} | {v[2]}")

        self.logger.info(horizontal_delimiter)

    def rename_files(self, rn_list: list[tuple[str, str, str]]) -> None:
        for v in rn_list:
            old_path = os.path.join(self.folder_name, v[0])
            new_path = os.path.join(self.folder_name, v[1])

            self.logger.info(f"Renaming {old_path} to {new_path}")
            os.replace(old_path, new_path)
            self.logger.info(f"{old_path} renamed")

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

        renaming_list = self.get_renaming_list(file_names, media_file_names)

        self.validate_renaming_list(renaming_list)

        if self.display:
            self.display_renaming_list(renaming_list)
        else:
            self.logger.info("Renaming files")
            self.rename_files(renaming_list)
            self.logger.info("Files renamed")

    def __call__(self):
        self.execute()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("file_names", help="File with file names for renaming")
    parser.add_argument("--levenstein",  action='store_true', help="Use name similarity for renaming")
    parser.add_argument("--display", action='store_true', help="Display renamed files, no renaming")

    args = parser.parse_args()

    print(f"Starting with args: {str(args)}")

    args_folder_name = os.path.dirname(args.file_names)
    FileRenamer(args.file_names, args_folder_name, args.levenstein, args.display, get_logger("rename_files"))()
