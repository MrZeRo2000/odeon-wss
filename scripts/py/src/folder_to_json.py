# folder_to_json.py

"""
Description: json from folder structure, substituting files with random samples
Arguments:
    folder_name: folder name to scan
    sample_folder_name: folder to contain sample files
"""


import argparse
import os
import json
import random


def folder_to_json(folder_name: str, sample_files: list[str]) -> dict[str, str]:
    result = {}
    for entry in os.listdir(folder_name):
        full_path = os.path.join(folder_name, entry)
        if os.path.isdir(full_path):
            result[entry] = folder_to_json(full_path, sample_files)
        else:
            ext = entry.split(".")[-1]

            sample_selection = [s for s in sample_files if s.endswith(f".{ext}")]
            if len(sample_selection) == 0:
                raise ValueError(f"No sample found for {entry}")
            
            value = random.choice(sample_selection)
            result[entry] = value
    return result

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("folder_name", help="Folder with data files")
    parser.add_argument("sample_folder_name", help="Folder with sample files")

    args = parser.parse_args()

    sample_files = [f for f in os.listdir(args.sample_folder_name) if os.path.isfile(os.path.join(args.sample_folder_name, f))]

    tree = folder_to_json(args.folder_name, sample_files)
    print(json.dumps(tree, indent=2))
