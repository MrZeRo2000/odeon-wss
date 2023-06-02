import argparse
from dataclasses import dataclass
import os
from logger import get_logger
import pathlib
import subprocess
import random

logger = get_logger("main")


@dataclass
class Params:
    ffmpegPath: str
    mediaPath: str

    def validate(self):
        if not (os.path.exists(self.ffmpegPath) and os.path.isfile(self.ffmpegPath)):
            raise ValueError(f"FFMPEG executable not found: {self.ffmpegPath}")
        if not (os.path.exists(self.mediaPath) and os.path.isdir(self.mediaPath)):
            raise ValueError(f"Media path not found: {self.mediaPath}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("ffmpeg_path", help="path to ffmpeg executable")
    parser.add_argument("media_path", help="path to root media folder")
    args = parser.parse_args()
    logger.info(f"Arguments:{str(args)}")

    params = Params(args.ffmpeg_path, args.media_path)
    params.validate()
    logger.debug(f"Params:{str(params)}")

    processed_files = []
    for root, dirs, files in os.walk(params.mediaPath):
        processed_files.extend([os.path.join(root, file) for file in files
                                if os.path.splitext(file.lower())[1] in ('.mp3', '.ape', '.flac', '.wv')])

    for processed_file in processed_files:
        sp = os.path.splitext(processed_file)
        output_file = f"{sp[0]}.tr{sp[1]}"

        if os.path.exists(output_file):
            logger.debug(f"Removing existing file: {output_file}")
            os.remove(output_file)

        command_line = " ".join([
            f"\"{params.ffmpegPath}\"",
            "-i",
            f"\"{processed_file}\"",
            f"-t {random.randint(5, 20)} "
            f"\"{output_file}\""
        ])

        logger.debug(f"Executing: {command_line}")
        subprocess.run(command_line, check=True)
        logger.debug(f"Executed")

        if os.path.exists(output_file):
            os.remove(processed_file)
            os.rename(output_file, processed_file)
