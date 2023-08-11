import bs4.element
import unicodedata
from bs4 import BeautifulSoup
import argparse
import logging
import os
import requests
import re
from collections import namedtuple
from logger import get_logger


class SeriesParser:
    PR = namedtuple('ParseResult', ['title', 'original_title', 'year'])

    def __init__(self, file_name: str, logger: logging.Logger):
        self.file_name = file_name
        self.logger = logger

    def get_document(self) -> str:
        with open(self.file_name, 'r', encoding="utf-8") as file:
            data = file.read()
        return data

    def parse_document(self, document) -> list:
        soup = BeautifulSoup(document, 'html.parser')
        table = soup.find_all('table')[0]
        trs = table.find('tbody').find_all('tr')
        tds = [t.find_all('td') for t in trs if len(t.find_all('td')) == 4]

        result = []
        for td in tds:
            ts = [t.get_text().replace("\n", "") for t in td]
            title = ts[1].strip()
            original_title = ts[2].strip()
            year = str(int(re.search(r"\d{4}", ts[3]).group(0)))

            result.append({'title': title, 'original_title': original_title, 'year': year})

        return result

    def save_files(self, pd: list):
        for k in pd[0].keys():
            ls = [p[k] for p in pd]
            file_name = os.path.join('../out', f"{os.path.splitext(os.path.basename(self.file_name))[0]}_{k}.txt")
            with open(file_name, 'w', encoding="utf-8") as f:
                for line in ls:
                    f.write(f"{line}\n")
            pass

        pass

    def execute(self):
        self.logger.info(f"Reading from {self.file_name}")
        document = self.get_document()
        self.logger.info(f"Document obtained: {len(document)} size")

        self.logger.info("Parsing document")
        pd = self.parse_document(document)
        self.logger.info(f"Document parsed, obtained {len(pd)} rows")

        self.save_files(pd)
        self.logger.info("Files saved")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("file_name", help="File name with table to parse")

    args = parser.parse_args()

    SeriesParser(args.file_name, get_logger("parser")).execute()

