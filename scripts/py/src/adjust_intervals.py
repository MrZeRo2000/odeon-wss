"""
Tracks SQL:
SELECT
  a.arts_name || ' - ' || t.trck_title AS track
FROM tracks t
INNER JOIN artists a ON a.arts_id = t.arts_id
WHERE t.artf_id = 100
ORDER BY t.trck_disk_num, t.trck_num;

Sample request:
You are a music expert. Provide track durations in seconds for below tracks. Each line should contain only duration value in seconds for each track in the same order:

Sample config:
"{\"target_duration\": \"01:10:36\", \"durations\": [ 223, 303, 240, 237, 328, 215, 253, 308, 313, 209, 279, 216, 221, 246, 294, 315, 298, 201, 318, 252 ]}"
"""

import time
import json
import argparse
from datetime import datetime
from itertools import accumulate


def adjust_intervals(config):
    target_duration_string = config["target_duration"]

    target_duration = int(
        (datetime.strptime(target_duration_string, "%H:%M:%S") -
         datetime.strptime("00:00:00", "%H:%M:%S")
         ).total_seconds())

    durations = config["durations"]

    total_duration = sum(durations)

    corrected_durations = [d * target_duration // total_duration for d in durations]

    corrected_timestamps = [time.strftime('%H:%M:%S', time.gmtime(d)) for d in accumulate(corrected_durations[:-1])]

    print("\n".join(corrected_timestamps))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(prog='Adjust Intervals')
    parser.add_argument('config')
    args = parser.parse_args()

    adjust_intervals(json.loads(args.config))

