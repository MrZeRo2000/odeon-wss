# parse_chapters.py

"""
Description: parse chapter durations from DVD chapters txt file
Arguments:
    file_name: file name
"""

import argparse
import re
import time
from logger import get_logger


class ChaptersParser:
    PARSE_EXPRESSION = r"([^=]+)=(\d{2}:\d{2}:\d{2})"

    def __init__(self, file_name: str):
        self.file_name = file_name
        self.logger = get_logger("parse_chapters")

    def parse(self):
        with open(self.file_name) as file:
            lines = [line.rstrip() for line in file]

        start_times = [time.strptime("2000 " + re.split(ChaptersParser.PARSE_EXPRESSION, line)[2], "%Y %H:%M:%S")
                       for line in lines
                       if re.match(ChaptersParser.PARSE_EXPRESSION, line)]
        durations = []
        for i in range(len(start_times) - 1):
            durations.append((i + 1, int(time.mktime(start_times[i + 1]) - time.mktime(start_times[i]))))

        self.logger.info("Durations in seconds:\n" + "\n".join(["{:02d}: {:d}".format(d[0], d[1]) for d in durations]))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("file_name", help="File name with chapters")

    args = parser.parse_args()
    ChaptersParser(args.file_name).parse()
