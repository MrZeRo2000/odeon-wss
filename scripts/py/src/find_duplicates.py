import argparse
from logger import get_logger


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("reference_folder", help="Reference folder")
    parser.add_argument("search_folder", help="Search folder")