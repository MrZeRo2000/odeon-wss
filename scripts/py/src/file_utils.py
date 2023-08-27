import os


def validate_file_name_and_path(file_name: str, path: str):
    if not os.path.exists(file_name):
        raise ValueError(f"File name does not exist: {file_name}")
    if not os.path.isfile(file_name):
        raise ValueError(f"{file_name} is not a file")
    if not os.path.exists(path):
        raise ValueError(f"Folder does not exist: {path}")
    if not os.path.isdir(path):
        raise ValueError(f"{path} is not a folder")


def get_file_lines(file_name: str) -> list[str]:
    result = []
    with open(file_name, mode="r", encoding="utf-8-sig") as f:
        for line in f:
            result.append(line.rstrip())

    return result
