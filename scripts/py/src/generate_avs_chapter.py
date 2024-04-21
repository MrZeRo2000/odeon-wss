import argparse
import os
import re

from logger import get_logger

logger = get_logger('generate_avs_chapter')


class AVSChapterGenerator:
    AVS_CONTENT_START_MARKER = '{start}'
    AVS_CONTENT_END_MARKER = '{end}'
    RE_FRAME_START_MARKER = r"<startFrame>(\d+)"
    RE_FRAME_END_MARKER = r"<endFrame>(\d+)"

    def __init__(self, sample_file_name):
        self.sample_file_name = os.path.abspath(sample_file_name)
        with open(self.sample_file_name, "r") as f:
            self.sample_content = f.read()

        self.path = os.path.dirname(sample_file_name)

        if AVSChapterGenerator.AVS_CONTENT_START_MARKER not in self.sample_content:
            raise ValueError(f'Start marker {AVSChapterGenerator.AVS_CONTENT_START_MARKER} not found in sample content')
        if AVSChapterGenerator.AVS_CONTENT_END_MARKER not in self.sample_content:
            raise ValueError(f'End marker {AVSChapterGenerator.AVS_CONTENT_END_MARKER} not found in sample content')

    def execute(self):
        for file_name in (p for p in os.listdir(self.path) if p.lower().endswith('clt')):
            logger.info(f'Processing {file_name}')
            self.process_file(file_name)
            pass

    def process_file(self, file_name: str):
        avs_file_name = file_name.replace('.clt', '.avs')

        with open(os.path.join(self.path, file_name), "r") as f:
            content = f.read()

        start_frame = re.search(AVSChapterGenerator.RE_FRAME_START_MARKER, content).group(1)
        end_frame = re.search(AVSChapterGenerator.RE_FRAME_END_MARKER, content).group(1)
        logger.info(f'Start frame: {start_frame}, End frame: {end_frame}')

        avs_content = (self.sample_content
                       .replace(AVSChapterGenerator.AVS_CONTENT_START_MARKER, start_frame)
                       .replace(AVSChapterGenerator.AVS_CONTENT_END_MARKER, end_frame))

        with open(os.path.join(self.path, avs_file_name), "w") as f:
            f.write(avs_content)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("sample_file_name", help="Sample file name")

    args = parser.parse_args()

    AVSChapterGenerator(args.sample_file_name).execute()
