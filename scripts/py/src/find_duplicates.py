# find_duplicates.py

"""
Description: finds file content duplicates ignoring names and paths
For reference folder also searches for duplicates inside
Result is outputted to console
Arguments:
    reference_folder: reference folder
    search_folder: search folder
"""

import argparse
import os
import hashlib
from dataclasses import dataclass
from collections import Counter
from itertools import groupby
from typing import List

from logger import get_logger

logger = get_logger(''.join(__file__.split(os.path.sep)[-1].split('.')[:-1]))

@dataclass
class FileSummary:
    path: str
    name: str
    size: int
    hash_digest: str

    def calc_hash(self):
        self.hash_digest = DuplicateFinder.get_file_hash(self.path)
        return self

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
            files_summary = [FileSummary(full_path, f, os.path.getsize(full_path), '')
                             for f in files
                             if f.upper().split(".")[-1] in DuplicateFinder.MEDIA_FILE_EXTENSIONS
                             and (full_path := os.path.abspath(os.path.join(root, f)))]
            result.extend(files_summary)

        return result

    def scan_reference_path(self) -> List[FileSummary]:
        return DuplicateFinder.scan_path(self.reference_path)

    def scan_search_path(self) -> List[FileSummary]:
        return DuplicateFinder.scan_path(self.search_path)

    @staticmethod
    def get_inner_duplicates(file_summaries: List[FileSummary]) -> List[FileSummary]:
        size_counts = Counter([f.size for f in file_summaries])
        size_duplicates = [c[0] for c in size_counts.items() if c[1] > 1]

        file_summaries_hashed = [f.calc_hash() for f in file_summaries if f.size in size_duplicates]
        hash_counts = Counter([f.hash_digest for f in file_summaries_hashed])
        hash_duplicates = [c[0] for c in hash_counts.items() if c[1] > 1]
        return sorted([f for f in file_summaries_hashed if f.hash_digest in hash_duplicates],
                      key=lambda f: f.hash_digest + f.path)


    def get_inner_duplicates_report(self, file_summaries: List[FileSummary]) -> List[str]:
        result = []
        delimiter = '-' * (max([len(v.path) for v in file_summaries]) - len(self.reference_path) + 5)

        hash_num = 0
        hash_value = ''
        for f in file_summaries:
            if f.hash_digest != hash_value:
                hash_num += 1
                hash_value = f.hash_digest
                result.append(delimiter)
            result.append(f"{hash_num:3d} | {f.path.replace(self.reference_path, '')[1:]}")

        result.append(delimiter)
        return result

    @staticmethod
    def get_ref_duplicates(ref_summaries: List[FileSummary], src_summaries: List[FileSummary]) -> List[tuple]:
        result = []

        ref_sizes = set((f.size for f in ref_summaries))
        src_sizes = set((f.size for f in src_summaries))
        joined_sizes = ref_sizes & src_sizes

        ref_summaries_hashed = [f.calc_hash() for f in ref_summaries if f.size in joined_sizes]
        src_summaries_hashed = [f.calc_hash() for f in src_summaries if f.size in joined_sizes]

        src_dict = {k: sorted([g1.path for g1 in g]) for k,g in groupby(src_summaries_hashed, lambda x: x.hash_digest)}

        hash_num = 0
        for rs in ref_summaries_hashed:
            src = src_dict.get(rs.hash_digest)
            if src is not None:
                hash_num += 1
                result.append((hash_num, rs.path, src))

        return result

    def get_ref_duplicates_report(self, duplicates: List[tuple]) -> List[str]:
        result = []

        max_ref_len = max([len(v[1]) for v in duplicates]) - len(self.reference_path) - 1
        max_src_len = max([len(v2) for v in duplicates for v2 in v[2]]) - len(self.search_path) - 1
        horizontal_delimiter = '-' * (max_ref_len + max_src_len + 3 + 3 + 3)
        result.append(horizontal_delimiter)

        hash_num = 0
        for row in duplicates:
            hash_num += 1
            for r in row[2]:
                v1 = row[1].replace(self.reference_path, '')[1:]
                v2 = r.replace(self.search_path, '')[1:]
                result.append(f"{hash_num:3d} | {v1}{' '*(max_ref_len - len(v1))} | {v2}{' '*(max_src_len - len(v2))}")

            result.append(horizontal_delimiter)
        return result


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("reference_folder", help="Reference folder")
    parser.add_argument("search_folder", help="Search folder")

    args = parser.parse_args()
    logger.info(f"Starting with args: {str(args)}")

    df = DuplicateFinder(args.reference_folder, args.search_folder)

    logger.info("Scanning reference path")
    reference_summaries = df.scan_reference_path()
    logger.info("Reference path scan completed")

    logger.info("Scanning search path")
    search_summaries = df.scan_search_path()
    logger.info("Search path scan completed")

    self_duplicates_summaries = DuplicateFinder.get_inner_duplicates(reference_summaries)
    if len(self_duplicates_summaries) > 0:
        self_duplicates_report_lines = df.get_inner_duplicates_report(self_duplicates_summaries)
        logger.info('\nSelf duplicates\n' + '\n'.join(self_duplicates_report_lines) + '\n')
    else:
        logger.info('\nNo self duplicates\n')

    duplicates = DuplicateFinder.get_ref_duplicates(reference_summaries, search_summaries)
    if len(duplicates) > 0:
        duplicates_report_lines = df.get_ref_duplicates_report(duplicates)
        logger.info('\nDuplicates\n' + '\n'.join(duplicates_report_lines) + '\n')
    else:
        logger.info('\nNo duplicates\n')
