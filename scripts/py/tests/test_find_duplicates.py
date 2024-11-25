import os
import pytest
import random
import shutil
from random_words import RandomWords

from .utils import prepare_folder, get_path

def generate_sample_files(folder_name: str, num: int) -> list[str]:
    data_path = prepare_folder(folder_name)
    result = []

    rw = RandomWords()
    for _ in range(num):
        f1_name = f"{_ + 1:02d}.txt"
        f2_name = f"{_ + 11:02d}.txt"
        with open(os.path.join(data_path, f1_name), "w") as f1:
            with open(os.path.join(data_path, f2_name), "w") as f2:
                s1 = ' '.join(rw.random_words(count=random.randint(5, 50), min_letter_count=6))
                f1.write(s1)
                result.append(f1_name)

                ls2 = list(s1)
                random.shuffle(ls2)
                s2 = ''.join(ls2)
                f2.write(s2)
                result.append(f2_name)

    return result

def create_reference_folder(folder_name: str, sample_folder_name: str) -> None:
    data_path = prepare_folder(folder_name)
    sample_path = get_path(sample_folder_name)

    # not found
    not_found_folder_name = os.path.join(data_path, "ref_not_found")
    os.makedirs(not_found_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '11.txt'), os.path.join(not_found_folder_name, '01.txt'))
    shutil.copy(os.path.join(sample_path, '02.txt'), os.path.join(not_found_folder_name, '202.txt'))

    # found_1
    found_1_folder_name = os.path.join(data_path, "ref_found_1")
    os.makedirs(found_1_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '05.txt'), os.path.join(found_1_folder_name, '505.txt'))

    # found_2
    found_2_folder_name = os.path.join(data_path, "ref_found_2")
    os.makedirs(found_2_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '06.txt'), os.path.join(found_2_folder_name, '606.txt'))

    # self_dup
    self_dup_folder_name = os.path.join(data_path, "self_dup")
    os.makedirs(self_dup_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '06.txt'), os.path.join(self_dup_folder_name, 'sd06.txt'))

    # found_3
    found_3_folder_name = os.path.join(data_path, "ref_found_3")
    os.makedirs(found_3_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '07.txt'), os.path.join(found_3_folder_name, '707.txt'))
    shutil.copy(os.path.join(sample_path, '08.txt'), os.path.join(found_3_folder_name, '08.txt'))
    shutil.copy(os.path.join(sample_path, '09.txt'), os.path.join(found_3_folder_name, '909.txt'))

def create_source_folder(folder_name: str, sample_folder_name: str) -> None:
    data_path = prepare_folder(folder_name)
    sample_path = get_path(sample_folder_name)

    # not found
    not_found_folder_name = os.path.join(data_path, "not_found")
    os.makedirs(not_found_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '03.txt'), os.path.join(not_found_folder_name, '03.txt'))
    shutil.copy(os.path.join(sample_path, '04.txt'), os.path.join(not_found_folder_name, '04.txt'))

    # found_1
    found_1_folder_name = os.path.join(data_path, "found_1")
    os.makedirs(found_1_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '05.txt'), os.path.join(found_1_folder_name, '1005.txt'))

    # found_2
    found_2_folder_name = os.path.join(data_path, "found_2")
    os.makedirs(found_2_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '06.txt'), os.path.join(found_2_folder_name, '1006.txt'))
    shutil.copy(os.path.join(sample_path, '06.txt'), os.path.join(found_2_folder_name, '6606.txt'))

    # found_3_1
    found_3_1_folder_name = os.path.join(data_path, "found_3_1")
    os.makedirs(found_3_1_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '07.txt'), os.path.join(found_3_1_folder_name, '707.txt'))

    # found_3_2
    found_3_2_folder_name = os.path.join(data_path, "found_3_2")
    os.makedirs(found_3_2_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '08.txt'), os.path.join(found_3_2_folder_name, '8808.txt'))
    shutil.copy(os.path.join(sample_path, '09.txt'), os.path.join(found_3_2_folder_name, '9909.txt'))

def test_generate_files():
    generate_sample_files("samples", 10)
    create_reference_folder("reference", "samples")
    create_source_folder("source", "samples")