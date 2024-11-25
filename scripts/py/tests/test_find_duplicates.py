import os
import pytest
import random
import shutil
import hashlib
from random_words import RandomWords

from .utils import prepare_folder, get_path

def generate_sample_files(folder_name: str, num: int) -> str:
    data_path = prepare_folder(folder_name)

    rw = RandomWords()
    for _ in range(num):
        f1_name = f"{_ + 1:02d}.txt"
        f2_name = f"{_ + 11:02d}.txt"
        with open(os.path.join(data_path, f1_name), "w") as f1:
            with open(os.path.join(data_path, f2_name), "w") as f2:
                s1 = ' '.join(rw.random_words(count=random.randint(5, 50), min_letter_count=6))
                f1.write(s1)

                ls2 = list(s1)
                random.shuffle(ls2)
                s2 = ''.join(ls2)
                f2.write(s2)

    return data_path

def create_reference_folder(folder_name: str, sample_folder_name: str) -> str:
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

    return data_path

def create_source_folder(folder_name: str, sample_folder_name: str) -> str:
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

    return data_path


@pytest.fixture(scope="module")
def generate_files():
    samples_path = generate_sample_files("samples", 10)
    reference_path = create_reference_folder("reference", "samples")
    source_path = create_source_folder("source", "samples")
    yield
    '''
    shutil.rmtree(samples_path)
    shutil.rmtree(reference_path)
    shutil.rmtree(source_path)    
    '''

def test_generate_files(generate_files):
    pass


def test_hash_lib(generate_files):
    f1_name = os.path.join(get_path("samples"), '01.txt')
    f2_name = os.path.join(get_path("samples"), '11.txt')

    assert os.path.getsize(f1_name) == os.path.getsize(f2_name)

    with open(f1_name, 'rb') as f1:
        with open(f2_name, 'rb') as f2:
            digest_1 = hashlib.file_digest(f1, "sha256").hexdigest()
            digest_2 = hashlib.file_digest(f2, "sha256").hexdigest()

            print(f"Digest 1: {digest_1}")
            print(f"Digest 2: {digest_2}")
            assert digest_1 != digest_2

            f1.seek(0)
            digest_1_1 = hashlib.file_digest(f1, "sha256").hexdigest()
            assert digest_1_1 == digest_1


    with open(f1_name, 'rb') as f11:
        digest_11 = hashlib.file_digest(f11, "sha256").hexdigest()
        assert(digest_11 == digest_1)