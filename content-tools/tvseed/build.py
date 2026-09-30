"""
Builds the TV question banks:  tv/src/main/assets/seed/{english,hindi,marathi,maths}.json

Class 1 of each language subject is carried over from the phone app's bank (it is genuinely Class 1
material); Classes 2-7 are the class-specific chapters written in english.py / hindi.py / marathi.py.
Maths already had different chapters per class and is copied unchanged.

Run from the repo root:   python3 content-tools/tvseed/build.py
"""
import json, os, shutil, sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from common import Bank, check_subject  # noqa: E402
import english, hindi, marathi, maths_hard  # noqa: E402

ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
SRC = os.path.join(ROOT, "app", "src", "main", "assets", "seed")
OUT = os.path.join(ROOT, "tv", "src", "main", "assets", "seed")


def load(code):
    with open(os.path.join(SRC, code + ".json"), encoding="utf8") as f:
        return json.load(f)


def words_in(chapter_list):
    words = set()
    for ch in chapter_list:
        for q in ch["questions"]:
            for lang in ("en",):
                words.add(q["prompt"][lang])
            for o in q["options"]:
                words.add(o["text"]["en"])
    return words


def build(code, module, seed):
    base = load(code)
    c1 = next(c for c in base["classes"] if c["classLevel"] == 1)
    # the phone bank repeats a few Class 1 questions word for word: keep the first of each
    for ch in c1["chapters"]:
        seen, keep = set(), []
        for q in ch["questions"]:
            key = (q["prompt"]["en"], q["prompt"]["hi"], [o["text"]["en"] for o in q["options"] if o["correct"]][0])
            if key not in seen:
                seen.add(key)
                keep.append(q)
        ch["questions"] = keep
    b = Bank(code, seed)
    kw = {}
    if code != "english":
        kw["existing_c1_words"] = words_in(c1["chapters"])
    new = module.chapters(b, **kw)
    classes = [c1] + [{"classLevel": lvl, "chapters": new[lvl]} for lvl in sorted(new)]
    for c in classes:
        for k, v in base["classes"][0].items():
            if k not in ("classLevel", "chapters"):
                c.setdefault(k, v)
    doc = {"version": 2, "subject": base["subject"], "classes": classes}
    titles = check_subject(doc)
    # no question may appear in two different classes (Class 1 comes from the old bank and is skipped)
    where = {}
    for c in doc["classes"]:
        if c["classLevel"] == 1:
            continue
        for ch in c["chapters"]:
            for q in ch["questions"]:
                key = (q["prompt"]["hi"], q["prompt"]["mr"], [o["text"]["en"] for o in q["options"] if o["correct"]][0])
                assert key not in where or where[key] == c["classLevel"], f"{code}: same question in classes {where[key]} and {c['classLevel']}: {q['prompt']['en']}"
                where[key] = c["classLevel"]
    repeated = {t: lv for t, lv in titles.items() if len(lv) > 1}
    assert not repeated, f"{code}: chapter titles repeated across classes: {repeated}"
    return doc


def main():
    os.makedirs(OUT, exist_ok=True)
    for code, module, seed in (("english", english, 2026), ("hindi", hindi, 2027), ("marathi", marathi, 2028)):
        doc = build(code, module, seed)
        with open(os.path.join(OUT, code + ".json"), "w", encoding="utf8") as f:
            json.dump(doc, f, ensure_ascii=False, indent=1)
        n = sum(len(ch["questions"]) for c in doc["classes"] for ch in c["chapters"])
        print(f"{code}: {len(doc['classes'])} classes, {sum(len(c['chapters']) for c in doc['classes'])} chapters, {n} questions")
    # Maths: Classes 1-5 from the phone bank, Classes 6-7 rewritten at medium-to-hard level
    base = load("maths")
    hard = maths_hard.chapters(Bank("maths", 2029))
    classes = [c for c in base["classes"] if c["classLevel"] <= 5]
    proto = {k: v for k, v in base["classes"][0].items() if k not in ("classLevel", "chapters")}
    classes += [dict(proto, classLevel=lvl, chapters=hard[lvl]) for lvl in sorted(hard)]
    doc = {"version": 2, "subject": base["subject"], "classes": classes}
    check_subject({"classes": [c for c in classes if c["classLevel"] >= 6]})  # Classes 1-5 are the phone bank, untouched
    with open(os.path.join(OUT, "maths.json"), "w", encoding="utf8") as f:
        json.dump(doc, f, ensure_ascii=False, indent=1)
    print("maths: classes 1-5 kept, classes 6-7 rebuilt:", sum(len(ch["questions"]) for c in classes if c["classLevel"] >= 6 for ch in c["chapters"]), "questions")


if __name__ == "__main__":
    main()
