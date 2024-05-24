import logging
import os


def get_logger(logger_name: str) -> logging.Logger:
    logger = logging.getLogger(logger_name)
    logger.setLevel(logging.DEBUG)

    # create formatter
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')

    # create console handler and set level to debug
    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    ch.setFormatter(formatter)

    logger.addHandler(ch)

    log_path = os.path.join(os.path.dirname(__file__), "../log/")
    if not os.path.exists(log_path):
        os.makedirs(log_path)
    log_file_name = f"{log_path}log.txt"
    if os.path.exists(log_file_name):
        os.remove(log_file_name)

    fh = logging.FileHandler(log_file_name, encoding='utf-8')
    fh.setLevel(logging.DEBUG)
    fh.setFormatter(formatter)

    logger.addHandler(fh)

    return logger
