import bs4.element
import unicodedata
from bs4 import BeautifulSoup
import argparse
import logging
import os
import requests
from logger import get_logger


class SeriesParser:
    def __init__(self, url: str, name: str, logger: logging.Logger):
        self.url = url
        self.name = name
        self.logger = logger

    def get_document(self) -> str:
        return requests.get(self.url).content

    def parse_document(self, document):
        soup = BeautifulSoup(document, 'html.parser')
        table_contents = soup.find_all(text="Первый сезон (1989)")[-1].parent.parent.next_sibling.next_sibling.tbody.contents
        table_tags = [t for t in table_contents if isinstance(t, bs4.element.Tag)]

        pass

    def execute(self):
        self.logger.info(f"Reading from {self.url}")
        document = self.get_document()
        self.logger.info(f"Document obtained: {len(document)} size")

        self.logger.info("Parsing document")
        self.parse_document(document)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("url", help="URL with series")
    parser.add_argument("name", help="Series name")

    args = parser.parse_args()

    SeriesParser(args.url, args.name, get_logger("parser")).execute()

