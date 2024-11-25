import os
import shutil

ROOT_DATA_PATH = os.path.join(os.path.dirname(__file__), "../data")

def get_path(folder_name: str) -> str:
    return os.path.abspath(os.path.join(ROOT_DATA_PATH, folder_name))

def clear_folder(folder_name: str) -> str:
    data_path = get_path(folder_name)
    if os.path.exists(data_path):
        shutil.rmtree(data_path)
    return data_path

def prepare_folder(folder_name: str) -> str:
    data_path = clear_folder(folder_name)
    os.mkdir(data_path)

    return data_path