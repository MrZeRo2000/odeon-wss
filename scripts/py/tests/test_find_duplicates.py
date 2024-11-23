import os
import pytest
from random_words import RandomWords

from .utils import ROOT_DATA_PATH

def generate_files(num: int) -> list[str]:
    rw = RandomWords()
    for _ in range(num):
        file_name = '_'.join(rw.random_words(count=2, min_letter_count=4)) + '.txt'

