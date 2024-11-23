import os

ROOT_DATA_PATH = os.path.join(os.path.dirname(__file__), "../data")

def clear_folder(folder_name: str) -> None:
    data_path = os.path.join(ROOT_DATA_PATH, folder_name)
    if os.path.exists(data_path):
        files = os.listdir(data_path)
        for file in files:
            os.remove(os.path.join(data_path, file))
    os.rmdir(data_path)

def prepare_folder(folder_name: str) -> str:
    data_path = os.path.join(ROOT_DATA_PATH, folder_name)

    if os.path.exists(data_path):
        files = os.listdir(data_path)
        for file in files:
            os.remove(os.path.join(data_path, file))
    else:
        os.mkdir(data_path)

    return data_path