import os
import pytest
import random
import shutil
import hashlib
from random_words import RandomWords

from utils import prepare_folder, get_path
from src.find_duplicates import DuplicateFinder
from src.logger import get_logger

logger = get_logger(__name__)


def generate_sample_files(folder_name: str, num: int) -> str:
    data_path = prepare_folder(folder_name)

    rw = RandomWords()
    for _ in range(num):
        f1_name = f"{_ + 1:02d}.mkv"
        f2_name = f"{_ + 11:02d}.mkv"
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
    shutil.copy(os.path.join(sample_path, '11.mkv'), os.path.join(not_found_folder_name, '01.mkv'))
    shutil.copy(os.path.join(sample_path, '02.mkv'), os.path.join(not_found_folder_name, '202.mkv'))

    # found_1
    found_1_folder_name = os.path.join(data_path, "ref_found_1")
    os.makedirs(found_1_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '05.mkv'), os.path.join(found_1_folder_name, '505.mkv'))

    # found_2
    found_2_folder_name = os.path.join(data_path, "ref_found_2")
    os.makedirs(found_2_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '06.mkv'), os.path.join(found_2_folder_name, '606.mkv'))

    # self_dup
    self_dup_folder_name = os.path.join(data_path, "self_dup")
    os.makedirs(self_dup_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '06.mkv'), os.path.join(self_dup_folder_name, 'sd06.mkv'))

    # found_3
    found_3_folder_name = os.path.join(data_path, "ref_found_3")
    os.makedirs(found_3_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '07.mkv'), os.path.join(found_3_folder_name, '707.mkv'))
    shutil.copy(os.path.join(sample_path, '08.mkv'), os.path.join(found_3_folder_name, '08.mkv'))
    shutil.copy(os.path.join(sample_path, '09.mkv'), os.path.join(found_3_folder_name, '909.mkv'))

    return data_path

def create_source_folder(folder_name: str, sample_folder_name: str) -> str:
    data_path = prepare_folder(folder_name)
    sample_path = get_path(sample_folder_name)

    # not found
    not_found_folder_name = os.path.join(data_path, "not_found")
    os.makedirs(not_found_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '03.mkv'), os.path.join(not_found_folder_name, '03.mkv'))
    shutil.copy(os.path.join(sample_path, '04.mkv'), os.path.join(not_found_folder_name, '04.mkv'))

    # found_1
    found_1_folder_name = os.path.join(os.path.join(data_path, "found_1"), "subfolder")
    os.makedirs(found_1_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '05.mkv'), os.path.join(found_1_folder_name, '1005.mkv'))

    # found_2
    found_2_folder_name = os.path.join(data_path, "found_2")
    os.makedirs(found_2_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '06.mkv'), os.path.join(found_2_folder_name, '1006.mkv'))
    shutil.copy(os.path.join(sample_path, '06.mkv'), os.path.join(found_2_folder_name, '6606.mkv'))

    # found_3_1
    found_3_1_folder_name = os.path.join(data_path, "found_3_1")
    os.makedirs(found_3_1_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '07.mkv'), os.path.join(found_3_1_folder_name, '707.mkv'))

    # found_3_2
    found_3_2_folder_name = os.path.join(data_path, "found_3_2")
    os.makedirs(found_3_2_folder_name, exist_ok=True)
    shutil.copy(os.path.join(sample_path, '08.mkv'), os.path.join(found_3_2_folder_name, '8808.mkv'))
    shutil.copy(os.path.join(sample_path, '09.mkv'), os.path.join(found_3_2_folder_name, '9909.mkv'))

    return data_path


@pytest.fixture(scope="module")
def generate_files():
    samples_path = generate_sample_files("samples", 10)
    reference_path = create_reference_folder("reference", "samples")
    source_path = create_source_folder("source", "samples")
    yield samples_path, reference_path, source_path
    shutil.rmtree(samples_path)
    shutil.rmtree(reference_path)
    shutil.rmtree(source_path)    

def test_generate_files(generate_files):
    pass


def test_hash_lib(generate_files):
    f1_name = os.path.join(get_path("samples"), '01.mkv')
    f2_name = os.path.join(get_path("samples"), '11.mkv')

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

def test_find_duplicates_scan_path(generate_files):
    source_path = generate_files[2]
    file_summaries = DuplicateFinder.scan_path(source_path)
    assert len(file_summaries) > 0

    file_summary = file_summaries[0]
    assert file_summary.name == file_summary.path.split(os.sep)[-1]
    assert file_summary.size == os.path.getsize(file_summary.path)

def test_find_duplicates_inner_duplicates(generate_files):
    reference_path = generate_files[1]
    file_summaries = DuplicateFinder.scan_path(reference_path)
    assert len(file_summaries) > 0

    file_duplicates = DuplicateFinder.get_inner_duplicates(file_summaries)
    assert len(file_duplicates) == 2
    assert file_duplicates[0].name == '606.mkv'
    assert file_duplicates[1].name == 'sd06.mkv'

    logger.info("get_inner_duplicates_report")
    report_lines = DuplicateFinder(reference_path, reference_path).get_inner_duplicates_report(file_duplicates)
    logger.info('\n' + '\n'.join(report_lines) + '\n')

def test_find_duplicates_ref_duplicates(generate_files):
    reference_path = generate_files[1]
    reference_summaries = DuplicateFinder.scan_path(reference_path)

    source_path = generate_files[2]
    source_summaries = DuplicateFinder.scan_path(source_path)

    ref_duplicates = DuplicateFinder.get_ref_duplicates(reference_summaries, source_summaries)
    assert len(ref_duplicates) > 0
    assert len(ref_duplicates) == 6

    # found_1
    assert ref_duplicates[0][1].endswith(os.sep.join(("ref_found_1", "505.mkv")))
    assert ref_duplicates[0][2][0].endswith(os.sep.join(("found_1","subfolder", "1005.mkv")))

    # found_2
    assert ref_duplicates[1][1].endswith(os.sep.join(("ref_found_2", "606.mkv")))
    assert ref_duplicates[1][2][0].endswith(os.sep.join(("found_2", "1006.mkv")))
    assert ref_duplicates[1][2][1].endswith(os.sep.join(("found_2", "6606.mkv")))

    # found_3
    assert ref_duplicates[2][1].endswith(os.sep.join(("ref_found_3", "08.mkv")))
    assert ref_duplicates[2][2][0].endswith(os.sep.join(("found_3_2", "8808.mkv")))

    assert ref_duplicates[3][1].endswith(os.sep.join(("ref_found_3", "707.mkv")))
    assert ref_duplicates[3][2][0].endswith(os.sep.join(("found_3_1", "707.mkv")))

    assert ref_duplicates[4][1].endswith(os.sep.join(("ref_found_3", "909.mkv")))
    assert ref_duplicates[4][2][0].endswith(os.sep.join(("found_3_2", "9909.mkv")))

    # self duplicate
    assert ref_duplicates[5][1].endswith(os.sep.join(("self_dup", "sd06.mkv")))
    assert ref_duplicates[5][2][0].endswith(os.sep.join(("found_2", "1006.mkv")))
    assert ref_duplicates[5][2][1].endswith(os.sep.join(("found_2", "6606.mkv")))

    logger.info("get_ref_duplicates_report")
    report_lines = DuplicateFinder(reference_path, source_path).get_ref_duplicates_report(ref_duplicates)
    logger.info('\n' + '\n'.join(report_lines) + '\n')

