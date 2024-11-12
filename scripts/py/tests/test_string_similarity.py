from collections.abc import Iterable
import pytest
import Levenshtein

def find_most_similar(ls: Iterable[str], value: str) -> str:
    distances = {s:Levenshtein.ratio(value, s) for s in ls}
    return list({k for k, v in distances.items() if v == max(distances.values())})[0]

@pytest.mark.parametrize(
    "lst,value,result",
    [
        (["aaa", "bbb", "ccc"], "aaa", "aaa"),
        (["yard", "mouse", "ground"], "spouse", "mouse"),
        (["quirk", "green", "spin"], "speed", "spin"),
    ]
)
def test_string_similarity(lst: list[str], value: str, result: str):
    assert find_most_similar(lst, value) == result