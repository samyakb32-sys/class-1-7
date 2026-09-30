"""
Shared helpers for the TV question banks (English / Hindi / Marathi, Classes 1-7).

Every chapter is hand-authored data (word lists, facts, rules) turned into multiple-choice
questions by a small builder, so a child at Class 5 sees Class 5 material and not the Class 1
chapters again. Text is trilingual: prompt / hint / title in en, mr, hi. Answer options are usually
the same word in every language (the word IS the answer), or a grammar term that is translated.
"""
import random

CLASSES = range(1, 8)


def L(en, mr, hi):
    return {"en": en, "mr": mr, "hi": hi}


def S(text):
    """The same text in every language column (a word, a letter, a spelling)."""
    return {"en": text, "mr": text, "hi": text}


def as_l(x):
    return x if isinstance(x, dict) else S(x)


def fmt(frame, **kw):
    """Fill {slots} in every language of a trilingual frame."""
    return {k: v.format(**{n: (a[k] if isinstance(a, dict) else a) for n, a in kw.items()}) for k, v in frame.items()}


class Bank:
    def __init__(self, code, seed):
        self.code = code
        self.rnd = random.Random(seed)
        self.counter = {}

    def qid(self, cid):
        self.counter[cid] = self.counter.get(cid, 0) + 1
        return f"{cid}_q{self.counter[cid]}"

    def chapter(self, cid, title, blurb, icon, prompt, hint, items, pool=None, count=12, bad=None, difficulty=2):
        """
        items: list of (q, correct, [wrongs]) or (q, correct) when a [pool] supplies the distractors.
        q may be None when the prompt needs no slot.  correct / wrongs may be str or a trilingual dict.
        bad: optional {correct: {words that must never be offered with it}} (e.g. true synonyms).
        """
        ch = {"id": cid, "title": title, "blurb": blurb, "iconKey": icon, "questions": []}
        items = list(items)
        self.rnd.shuffle(items)
        for it in items[:count]:
            q, correct = it[0], it[1]
            wrongs = list(it[2]) if len(it) > 2 and it[2] is not None else []
            if len(wrongs) < 3 and pool:
                ckey = correct["en"] if isinstance(correct, dict) else correct
                candidates = [p for p in pool if (p["en"] if isinstance(p, dict) else p) != ckey and p not in wrongs]
                if bad and ckey in bad:
                    candidates = [p for p in candidates if (p["en"] if isinstance(p, dict) else p) not in bad[ckey]]
                self.rnd.shuffle(candidates)
                wrongs += candidates[: 3 - len(wrongs)]
            cl = as_l(correct)
            opts = [{"text": cl, "correct": True}]
            seen = {cl["en"]}
            for w in wrongs[:3]:
                wl = as_l(w)
                if wl["en"] in seen:
                    continue
                seen.add(wl["en"])
                opts.append({"text": wl, "correct": False})
            self.rnd.shuffle(opts)
            slots = {"q": q if q is not None else "", "a": cl}
            ch["questions"].append({
                "id": self.qid(cid),
                "prompt": fmt(prompt, **slots),
                "difficulty": difficulty,
                "hint": fmt(hint, **slots),
                "options": opts,
            })
        return ch


def check_subject(doc):
    """Structural checks. Raises AssertionError with the offending question."""
    seen_titles = {}
    for c in doc["classes"]:
        lvl = c["classLevel"]
        ids = set()
        for ch in c["chapters"]:
            assert ch["id"] not in ids, f"duplicate chapter id {ch['id']}"
            ids.add(ch["id"])
            assert len(ch["questions"]) >= 8, f"{ch['id']}: only {len(ch['questions'])} questions"
            prompts = set()
            for q in ch["questions"]:
                w = f"{q['id']} {q['prompt']['en']!r}"
                for lang in ("en", "mr", "hi"):
                    assert q["prompt"][lang].strip() and q["hint"][lang].strip(), f"{w}: blank {lang} text"
                    assert len(q["prompt"][lang]) <= 150, f"{w}: prompt too long in {lang}"
                    assert len(q["hint"][lang]) <= 170, f"{w}: hint too long in {lang}"
                opts = q["options"]
                assert 2 <= len(opts) <= 4, f"{w}: {len(opts)} options"
                assert sum(o["correct"] for o in opts) == 1, f"{w}: needs exactly one correct option"
                for lang in ("en", "mr", "hi"):
                    texts = [o["text"][lang] for o in opts]
                    assert len(set(texts)) == len(texts), f"{w}: duplicate options in {lang}: {texts}"
                    assert all(t.strip() and len(t) <= 44 for t in texts), f"{w}: bad option text {texts}"
                correct = [o["text"]["en"] for o in opts if o["correct"]][0]
                key = q["prompt"]["en"] + "|" + q["prompt"]["hi"] + "|" + correct
                assert key not in prompts, f"{ch['id']}: repeated question {w}"
                prompts.add(key)
            seen_titles.setdefault(ch["title"]["en"], []).append(lvl)
    return seen_titles
