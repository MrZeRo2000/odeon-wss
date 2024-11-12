import os
import pytest
from random_words import RandomWords
import random

from src.rename_files import FileRenamer
from src.logger import get_logger

ROOT_DATA_PATH = os.path.join(os.path.dirname(__file__), "../data")

def clear_folder(folder_name: str) -> None:
    data_path = os.path.join(ROOT_DATA_PATH, folder_name)
    if os.path.exists(data_path):
        files = os.listdir(data_path)
        for file in files:
            os.remove(os.path.join(data_path, file))
    os.rmdir(data_path)

def generate_data(folder_name: str) -> tuple[list[str], list[str]]:
    data_path = os.path.join(ROOT_DATA_PATH, folder_name)
    if os.path.exists(data_path):
        files = os.listdir(data_path)
        for file in files:
            os.remove(os.path.join(data_path, file))
    else:
        os.mkdir(data_path)

    rw = RandomWords()

    names = []
    for fc in range(3, 3 + random.randint(6, 12)):
        name = f"{' '.join(rw.random_words(count=random.randint(2, 6), min_letter_count=6))}.mkv".capitalize()
        with open(os.path.join(data_path, name), "w") as f:
            f.write(' ' * random.randint(5, 50))
        names.append(name)

    names_by_index = names
    with open(os.path.join(data_path, "names_by_index.txt"), "w") as f:
        f.write('\n'.join([os.path.splitext(n)[0] for n in names_by_index]))

    names_from_file = []
    for i in list(range(len(names))):
        names_from_file.append(f"{(i + 1):02d} {names[i]}")
    with open(os.path.join(data_path, "names_from_file.txt"), "w") as f:
        f.write('\n'.join([os.path.splitext(n)[0] for n in names_from_file]))

    return names_by_index, names_from_file

@pytest.fixture()
def generate_data_by_index() -> tuple[list[str], list[str]]:
    yield generate_data("rename_files_by_index")
    clear_folder("rename_files_by_index")

@pytest.fixture()
def generate_data_from_file() -> tuple[list[str], list[str]]:
    yield generate_data("rename_files_from_file")
    clear_folder("rename_files_from_file")

def get_output_file_names(data_path: str) -> list[str]:
    return sorted([f for f in os.listdir(data_path)
            if os.path.isfile(os.path.join(data_path, f))
            and f.upper().split(".")[-1] in FileRenamer.MEDIA_FILE_EXTENSIONS])

def test_by_index(generate_data_by_index) -> None:
    data_path = os.path.join(ROOT_DATA_PATH, "rename_files_by_index")
    FileRenamer(os.path.join(data_path, "names_by_index.txt"), data_path, False, get_logger("test_by_index"))()
    output_file_names = get_output_file_names(data_path)

    for i in range(len(output_file_names)):
        assert output_file_names[i] == f"{(i + 1):02d} {generate_data_by_index[0][i]}"

def test_from_file(generate_data_from_file) -> None:
    data_path = os.path.join(ROOT_DATA_PATH, "rename_files_from_file")
    FileRenamer(os.path.join(data_path, "names_from_file.txt"), data_path, False, get_logger("test_from_file"))()
    output_file_names = get_output_file_names(data_path)
    data_from_file = sorted(generate_data_from_file[1])

    for i in range(len(output_file_names)):
        assert output_file_names[i] == data_from_file[i]