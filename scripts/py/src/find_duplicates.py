import argparse
import os
import hashlib
from dataclasses import dataclass
from collections import Counter
from typing import List

from logger import get_logger

logger = get_logger(__name__)

@dataclass
class FileSummary:
    path: str
    name: str
    size: int
    hash_digest: str

class DuplicateFinder:
    MEDIA_FILE_EXTENSIONS = "AVI|M4V|MKV|MP4|MPG|VOB|WMV".split("|")

    def __init__(self, reference_path: str, search_path: str):
        self.reference_path = reference_path
        self.search_path = search_path

    @staticmethod
    def get_file_hash(file_path: str) -> str:
        with open(file_path, "rb") as f:
            return hashlib.file_digest(f, "sha256").hexdigest()

    @staticmethod
    def scan_path(path: str) -> List[FileSummary]:
        result = []

        for root, _, files in os.walk(path):
            files_summary = [FileSummary(full_path, f, os.path.getsize(full_path), DuplicateFinder.get_file_hash(full_path))
                             for f in files
                             if f.upper().split(".")[-1] in DuplicateFinder.MEDIA_FILE_EXTENSIONS
                             and (full_path := os.path.abspath(os.path.join(root, f)))]
            result.extend(files_summary)

        return result

    @staticmethod
    def get_inner_duplicates(file_summaries: List[FileSummary]) -> List[FileSummary]:
        hash_counts = Counter([f.hash_digest for f in file_summaries])
        hash_duplicates = [c[0] for c in hash_counts.items() if c[1] > 1]
        return sorted([f for f in file_summaries if f.hash_digest in hash_duplicates],
                      key=lambda f: f.hash_digest + f.path)

    @staticmethod
    def get_inner_duplicates_report(file_summaries: List[FileSummary]) -> List[str]:
        result = []
        delimiter = '*' * 60

        hash_num = 0
        hash_value = ''
        for f in file_summaries:
            if f.hash_digest != hash_value:
                hash_num += 1
                hash_value = f.hash_digest
                result.append(delimiter)
            result.append(f"{hash_num:3d} {f.path}")

        result.append(delimiter)
        return result

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("reference_folder", help="Reference folder")
    parser.add_argument("search_folder", help="Search folder")

    args = parser.parse_args()
    logger.info(f"Starting with args: {str(args)}")