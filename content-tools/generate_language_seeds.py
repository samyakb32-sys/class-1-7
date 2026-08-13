"""
Generates trilingual seed content for the three language subjects:
Marathi, Hindi and English.

Unlike Maths, language content can't be produced by plugging random numbers
into a template — "what is 4+6" works for any numbers, "which word means X"
does not. So this uses *curated word lists and rules* per class level, and
builds questions from those. Every question still carries a real explanation
in all three languages, same as the Maths generator.

Run from repo root:  python3 content-tools/generate_language_seeds.py
"""
import json, random

random.seed(1729)


def lt(en, mr, hi):
    return {"en": en, "mr": mr, "hi": hi}


qid_counter = {}


def next_id(chapter_id):
    qid_counter[chapter_id] = qid_counter.get(chapter_id, 0) + 1
    return f"{chapter_id}_q{qid_counter[chapter_id]}"


def new_chapter(cid, title, blurb, icon):
    return {"id": cid, "title": title, "blurb": blurb, "iconKey": icon, "questions": []}


def add_q(chapter, prompt, correct_text, wrong_texts, hint, difficulty=3):
    # Guard: a distractor identical to the answer makes the question unanswerable
    # (two identical options, one silently "wrong"). Drop any such duplicate.
    seen = {correct_text["en"]}
    deduped = []
    for w in wrong_texts:
        if w["en"] not in seen:
            seen.add(w["en"])
            deduped.append(w)
    wrong_texts = deduped
    if not wrong_texts:
        return  # nothing sensible to ask; skip rather than emit a 1-option question

    """correct_text / wrong_texts are LocalizedText dicts (same word shown in
    all three languages for script questions; for translation questions the
    option IS the answer word, so it's identical across languages)."""
    opts = [{"text": correct_text, "correct": True}]
    for w in wrong_texts:
        opts.append({"text": w, "correct": False})
    random.shuffle(opts)
    chapter["questions"].append({
        "id": next_id(chapter["id"]),
        "prompt": prompt,
        "difficulty": difficulty,
        "hint": hint,
        "options": opts
    })


def same(text):
    """A word/letter that reads identically in every language column."""
    return lt(text, text, text)


# ---------------------------------------------------------------- MARATHI ---

MR_VOWELS = ["अ", "आ", "इ", "ई", "उ", "ऊ", "ए", "ऐ", "ओ", "औ", "अं", "अः"]
MR_CONSONANTS = [
    "क", "ख", "ग", "घ", "च", "छ", "ज", "झ", "ट", "ठ", "ड", "ढ", "ण",
    "त", "थ", "द", "ध", "न", "प", "फ", "ब", "भ", "म", "य", "र", "ल",
    "व", "श", "ष", "स", "ह", "ळ",
]
MR_MATRAS = [("", "अ"), ("ा", "आ"), ("ि", "इ"), ("ी", "ई"), ("ु", "उ"),
             ("ू", "ऊ"), ("े", "ए"), ("ै", "ऐ"), ("ो", "ओ"), ("ौ", "औ")]

# (marathi word, english meaning, hindi meaning, first letter)
MR_WORDS = [
    ("आंबा", "mango", "आम", "आ"), ("कमळ", "lotus", "कमल", "क"),
    ("घर", "house", "घर", "घ"), ("चिमणी", "sparrow", "गौरैया", "चि"),
    ("झाड", "tree", "पेड़", "झ"), ("तारा", "star", "तारा", "ता"),
    ("दूध", "milk", "दूध", "दू"), ("नदी", "river", "नदी", "न"),
    ("पाणी", "water", "पानी", "पा"), ("फूल", "flower", "फूल", "फू"),
    ("बकरी", "goat", "बकरी", "ब"), ("मासा", "fish", "मछली", "मा"),
    ("वाघ", "tiger", "बाघ", "वा"), ("सूर्य", "sun", "सूरज", "सू"),
    ("हत्ती", "elephant", "हाथी", "ह"), ("डोंगर", "mountain", "पहाड़", "डों"),
    ("पुस्तक", "book", "किताब", "पु"), ("शाळा", "school", "स्कूल", "शा"),
    ("मित्र", "friend", "दोस्त", "मि"), ("पक्षी", "bird", "पक्षी", "प"),
]

MR_OPPOSITES = [
    ("मोठा", "लहान", "big", "small"), ("उंच", "बुटका", "tall", "short"),
    ("नवीन", "जुना", "new", "old"), ("गरम", "थंड", "hot", "cold"),
    ("दिवस", "रात्र", "day", "night"), ("वर", "खाली", "up", "down"),
    ("आत", "बाहेर", "inside", "outside"), ("गोड", "कडू", "sweet", "bitter"),
]

MR_PLURALS = [
    ("मुलगा", "मुले", "boy"), ("पुस्तक", "पुस्तके", "book"),
    ("झाड", "झाडे", "tree"), ("फूल", "फुले", "flower"),
    ("घर", "घरे", "house"), ("मैदान", "मैदाने", "ground"),
]

# ------------------------------------------------------------------ HINDI ---

HI_VOWELS = ["अ", "आ", "इ", "ई", "उ", "ऊ", "ए", "ऐ", "ओ", "औ", "अं", "अः"]
HI_CONSONANTS = [
    "क", "ख", "ग", "घ", "च", "छ", "ज", "झ", "ट", "ठ", "ड", "ढ", "ण",
    "त", "थ", "द", "ध", "न", "प", "फ", "ब", "भ", "म", "य", "र", "ल",
    "व", "श", "ष", "स", "ह",
]

HI_WORDS = [
    ("आम", "mango", "आंबा", "आ"), ("कमल", "lotus", "कमळ", "क"),
    ("घर", "house", "घर", "घ"), ("चिड़िया", "bird", "पक्षी", "चि"),
    ("पेड़", "tree", "झाड", "पे"), ("तारा", "star", "तारा", "ता"),
    ("दूध", "milk", "दूध", "दू"), ("नदी", "river", "नदी", "न"),
    ("पानी", "water", "पाणी", "पा"), ("फूल", "flower", "फूल", "फू"),
    ("बकरी", "goat", "बकरी", "ब"), ("मछली", "fish", "मासा", "म"),
    ("बाघ", "tiger", "वाघ", "बा"), ("सूरज", "sun", "सूर्य", "सू"),
    ("हाथी", "elephant", "हत्ती", "हा"), ("पहाड़", "mountain", "डोंगर", "प"),
    ("किताब", "book", "पुस्तक", "कि"), ("स्कूल", "school", "शाळा", "स"),
    ("दोस्त", "friend", "मित्र", "दो"), ("रोटी", "bread", "भाकरी", "रो"),
]

HI_OPPOSITES = [
    ("बड़ा", "छोटा", "big", "small"), ("लंबा", "छोटा", "tall", "short"),
    ("नया", "पुराना", "new", "old"), ("गरम", "ठंडा", "hot", "cold"),
    ("दिन", "रात", "day", "night"), ("ऊपर", "नीचे", "up", "down"),
    ("अंदर", "बाहर", "inside", "outside"), ("मीठा", "कड़वा", "sweet", "bitter"),
]

# Every pair here must actually CHANGE in the plural — Hindi words like
# पेड़/पेड़ and फूल/फूल are identical in both forms, which made a meaningless
# question and produced duplicate answer options.
HI_PLURALS = [
    ("लड़का", "लड़के", "boy"), ("किताब", "किताबें", "book"),
    ("बच्चा", "बच्चे", "child"), ("लड़की", "लड़कियाँ", "girl"),
    ("कहानी", "कहानियाँ", "story"), ("घोड़ा", "घोड़े", "horse"),
    ("बेटा", "बेटे", "son"), ("नदी", "नदियाँ", "river"),
]

# ---------------------------------------------------------------- ENGLISH ---

EN_ALPHABET_WORDS = [
    ("A", "Apple", "सफरचंद", "सेब"), ("B", "Ball", "चेंडू", "गेंद"),
    ("C", "Cat", "मांजर", "बिल्ली"), ("D", "Dog", "कुत्रा", "कुत्ता"),
    ("E", "Elephant", "हत्ती", "हाथी"), ("F", "Fish", "मासा", "मछली"),
    ("G", "Goat", "बकरी", "बकरी"), ("H", "House", "घर", "घर"),
    ("I", "Ink", "शाई", "स्याही"), ("J", "Jug", "सुरई", "सुराही"),
    ("K", "Kite", "पतंग", "पतंग"), ("L", "Lion", "सिंह", "शेर"),
    ("M", "Mango", "आंबा", "आम"), ("N", "Nest", "घरटे", "घोंसला"),
    ("O", "Owl", "घुबड", "उल्लू"), ("P", "Parrot", "पोपट", "तोता"),
    ("R", "River", "नदी", "नदी"), ("S", "Sun", "सूर्य", "सूरज"),
    ("T", "Tiger", "वाघ", "बाघ"), ("W", "Water", "पाणी", "पानी"),
]

EN_OPPOSITES = [
    ("big", "small"), ("hot", "cold"), ("day", "night"), ("up", "down"),
    ("open", "close"), ("fast", "slow"), ("happy", "sad"), ("old", "new"),
    ("in", "out"), ("long", "short"),
]

EN_PLURALS = [
    ("book", "books"), ("box", "boxes"), ("baby", "babies"),
    ("child", "children"), ("man", "men"), ("leaf", "leaves"),
    ("bus", "buses"), ("city", "cities"), ("foot", "feet"), ("mouse", "mice"),
]

EN_VERBS_PAST = [
    ("go", "went"), ("eat", "ate"), ("run", "ran"), ("see", "saw"),
    ("write", "wrote"), ("take", "took"), ("come", "came"), ("give", "gave"),
    ("sing", "sang"), ("drink", "drank"),
]

EN_ARTICLES = [
    ("apple", "an"), ("book", "a"), ("hour", "an"), ("umbrella", "an"),
    ("cat", "a"), ("orange", "an"), ("school", "a"), ("egg", "an"),
]


# ======================================================= QUESTION BUILDERS ===

def q_letter_word_match(chapter, words, n, lang):
    """"Which word starts with <letter>?" — the core early-literacy question."""
    picked = random.sample(words, min(n, len(words)))
    for word, meaning_en, meaning_other, first in picked:
        wrong_pool = [w for w in words if w[0] != word]
        wrongs = random.sample(wrong_pool, 3)
        if lang == "mr":
            prompt = lt(
                f"Which word starts with '{first}'?",
                f"'{first}' ने सुरू होणारा शब्द कोणता?",
                f"'{first}' से शुरू होने वाला शब्द कौन-सा है?"
            )
            hint = lt(
                f"'{word}' begins with '{first}'. It means '{meaning_en}'.",
                f"'{word}' हा शब्द '{first}' ने सुरू होतो. त्याचा अर्थ '{meaning_en}' असा आहे.",
                f"'{word}' शब्द '{first}' से शुरू होता है। इसका अर्थ '{meaning_other}' है।"
            )
        else:
            prompt = lt(
                f"Which word starts with '{first}'?",
                f"'{first}' ने सुरू होणारा शब्द कोणता?",
                f"'{first}' से शुरू होने वाला शब्द कौन-सा है?"
            )
            hint = lt(
                f"'{word}' begins with '{first}'. It means '{meaning_en}'.",
                f"'{word}' हा शब्द '{first}' ने सुरू होतो. अर्थ: '{meaning_en}'.",
                f"'{word}' शब्द '{first}' से शुरू होता है। इसका अर्थ '{meaning_en}' है।"
            )
        add_q(chapter, prompt, same(word), [same(w[0]) for w in wrongs], hint, difficulty=2)


def q_word_meaning(chapter, words, n, lang):
    """"What does <word> mean?" — answer given in English."""
    picked = random.sample(words, min(n, len(words)))
    for word, meaning_en, meaning_other, first in picked:
        wrong_pool = [w for w in words if w[1] != meaning_en]
        wrongs = random.sample(wrong_pool, 3)
        lang_name_en = "Marathi" if lang == "mr" else "Hindi"
        prompt = lt(
            f"What does the {lang_name_en} word '{word}' mean in English?",
            f"'{word}' या शब्दाचा इंग्रजीत अर्थ काय?",
            f"'{word}' शब्द का अंग्रेज़ी में अर्थ क्या है?"
        )
        hint = lt(
            f"'{word}' means '{meaning_en}'.",
            f"'{word}' म्हणजे '{meaning_en}'.",
            f"'{word}' का अर्थ '{meaning_en}' होता है।"
        )
        add_q(chapter, prompt, same(meaning_en), [same(w[1]) for w in wrongs], hint, difficulty=3)


def q_barakhadi(chapter, consonants, matras, n):
    """"<consonant> + <matra vowel> = ?" — barakhadi formation."""
    for _ in range(n):
        cons = random.choice(consonants)
        matra, vowel = random.choice(matras[1:])  # skip the bare form
        correct = cons + matra
        wrong_matras = random.sample([m for m in matras if m[0] != matra], 3)
        wrongs = [same(cons + wm[0]) for wm in wrong_matras]
        prompt = lt(
            f"'{cons}' + '{vowel}' makes which letter?",
            f"'{cons}' + '{vowel}' = कोणते अक्षर?",
            f"'{cons}' + '{vowel}' से कौन-सा अक्षर बनता है?"
        )
        hint = lt(
            f"Adding the '{vowel}' sign to '{cons}' gives '{correct}'.",
            f"'{cons}' ला '{vowel}' ची मात्रा लावली की '{correct}' होते.",
            f"'{cons}' पर '{vowel}' की मात्रा लगाने से '{correct}' बनता है।"
        )
        add_q(chapter, prompt, same(correct), wrongs, hint, difficulty=3)


def q_vowel_or_consonant(chapter, vowels, consonants, n):
    for _ in range(n):
        is_vowel = random.choice([True, False])
        letter = random.choice(vowels if is_vowel else consonants)
        correct_en = "Vowel (स्वर)" if is_vowel else "Consonant (व्यंजन)"
        wrong_en = "Consonant (व्यंजन)" if is_vowel else "Vowel (स्वर)"
        prompt = lt(
            f"Is '{letter}' a vowel or a consonant?",
            f"'{letter}' हे स्वर आहे की व्यंजन?",
            f"'{letter}' स्वर है या व्यंजन?"
        )
        hint = lt(
            f"'{letter}' is a {'vowel' if is_vowel else 'consonant'}. Vowels can be spoken on their own; consonants need a vowel sound to complete them.",
            f"'{letter}' हे {'स्वर' if is_vowel else 'व्यंजन'} आहे. स्वर स्वतंत्रपणे उच्चारता येतात; व्यंजनांना स्वराची जोड लागते.",
            f"'{letter}' {'स्वर' if is_vowel else 'व्यंजन'} है। स्वर अपने आप बोले जा सकते हैं; व्यंजनों को स्वर की ज़रूरत होती है।"
        )
        add_q(chapter, prompt, same(correct_en), [same(wrong_en)], hint, difficulty=2)


def q_opposite(chapter, pairs, n, lang):
    picked = random.sample(pairs, min(n, len(pairs)))
    for item in picked:
        if lang == "en":
            word, opp = item
            meaning_note_en = f"The opposite of '{word}' is '{opp}'."
            meaning_note_mr = f"'{word}' चा विरुद्ध अर्थ '{opp}' आहे."
            meaning_note_hi = f"'{word}' का विलोम '{opp}' है।"
            wrong_pool = [p[1] for p in pairs if p[1] != opp]
        else:
            word, opp, w_en, o_en = item
            meaning_note_en = f"The opposite of '{word}' ({w_en}) is '{opp}' ({o_en})."
            meaning_note_mr = f"'{word}' ({w_en}) चा विरुद्धार्थी शब्द '{opp}' ({o_en}) आहे."
            meaning_note_hi = f"'{word}' ({w_en}) का विलोम '{opp}' ({o_en}) है।"
            wrong_pool = [p[1] for p in pairs if p[1] != opp]
        wrongs = random.sample(wrong_pool, min(3, len(wrong_pool)))
        prompt = lt(
            f"What is the opposite of '{word}'?",
            f"'{word}' चा विरुद्धार्थी शब्द कोणता?",
            f"'{word}' का विलोम शब्द क्या है?"
        )
        hint = lt(meaning_note_en, meaning_note_mr, meaning_note_hi)
        add_q(chapter, prompt, same(opp), [same(w) for w in wrongs], hint, difficulty=3)


def q_plural(chapter, pairs, n, lang):
    picked = random.sample(pairs, min(n, len(pairs)))
    for item in picked:
        if lang == "en":
            sing, plur = item
            note_en = f"'{sing}' becomes '{plur}' in the plural."
        else:
            sing, plur, meaning = item
            note_en = f"'{sing}' ({meaning}) becomes '{plur}' in the plural."
        wrong_pool = [p[1] for p in pairs if p[1] != plur]
        wrongs = random.sample(wrong_pool, min(3, len(wrong_pool)))
        prompt = lt(
            f"What is the plural of '{sing}'?",
            f"'{sing}' चे अनेकवचन काय?",
            f"'{sing}' का बहुवचन क्या है?"
        )
        hint = lt(
            note_en,
            f"'{sing}' चे अनेकवचन '{plur}' होते.",
            f"'{sing}' का बहुवचन '{plur}' होता है।"
        )
        add_q(chapter, prompt, same(plur), [same(w) for w in wrongs], hint, difficulty=3)


def q_en_alphabet(chapter, n):
    picked = random.sample(EN_ALPHABET_WORDS, min(n, len(EN_ALPHABET_WORDS)))
    for letter, word, mr, hi in picked:
        wrong_pool = [w for w in EN_ALPHABET_WORDS if w[1] != word]
        wrongs = random.sample(wrong_pool, 3)
        prompt = lt(
            f"Which word starts with the letter '{letter}'?",
            f"'{letter}' अक्षराने सुरू होणारा शब्द कोणता?",
            f"'{letter}' अक्षर से शुरू होने वाला शब्द कौन-सा है?"
        )
        hint = lt(
            f"'{word}' starts with '{letter}'. In Marathi it is '{mr}', in Hindi '{hi}'.",
            f"'{word}' हा शब्द '{letter}' ने सुरू होतो. मराठीत '{mr}'.",
            f"'{word}' शब्द '{letter}' से शुरू होता है। हिंदी में '{hi}'।"
        )
        add_q(chapter, prompt, same(word), [same(w[1]) for w in wrongs], hint, difficulty=2)


def q_en_past_tense(chapter, n):
    picked = random.sample(EN_VERBS_PAST, min(n, len(EN_VERBS_PAST)))
    for base, past in picked:
        wrong_pool = [p[1] for p in EN_VERBS_PAST if p[1] != past]
        wrongs = random.sample(wrong_pool, 3)
        prompt = lt(
            f"What is the past tense of '{base}'?",
            f"'{base}' चा भूतकाळ काय?",
            f"'{base}' का भूतकाल क्या है?"
        )
        hint = lt(
            f"'{base}' becomes '{past}' in the past tense. It is irregular, so it does not simply add '-ed'.",
            f"'{base}' चा भूतकाळ '{past}' होतो. हे अनियमित क्रियापद आहे, त्यामुळे '-ed' लागत नाही.",
            f"'{base}' का भूतकाल '{past}' होता है। यह अनियमित क्रिया है, इसलिए '-ed' नहीं लगता।"
        )
        add_q(chapter, prompt, same(past), [same(w) for w in wrongs], hint, difficulty=4)


def q_en_article(chapter, n):
    picked = random.sample(EN_ARTICLES, min(n, len(EN_ARTICLES)))
    for word, article in picked:
        other = "a" if article == "an" else "an"
        starts_vowel_sound = article == "an"
        prompt = lt(
            f"Choose the correct article: ___ {word}",
            f"योग्य article निवडा: ___ {word}",
            f"सही article चुनो: ___ {word}"
        )
        hint = lt(
            f"'{word}' starts with a {'vowel' if starts_vowel_sound else 'consonant'} sound, so we use '{article} {word}'.",
            f"'{word}' चा उच्चार {'स्वराने' if starts_vowel_sound else 'व्यंजनाने'} सुरू होतो, म्हणून '{article} {word}'.",
            f"'{word}' का उच्चारण {'स्वर' if starts_vowel_sound else 'व्यंजन'} ध्वनि से शुरू होता है, इसलिए '{article} {word}'।"
        )
        add_q(chapter, prompt, same(article), [same(other)], hint, difficulty=3)


# ============================================================ CLASS BUILDS ===

def build_marathi():
    classes = []
    for level in range(1, 8):
        c = {"classLevel": level, "chapters": []}

        ch1 = new_chapter(f"marathi_c{level}_ch1",
            lt("Letters and Sounds", "अक्षरे आणि आवाज", "अक्षर और ध्वनि"),
            lt("Vowels and consonants.", "स्वर आणि व्यंजन.", "स्वर और व्यंजन।"), "abc")
        q_vowel_or_consonant(ch1, MR_VOWELS, MR_CONSONANTS, 11)
        c["chapters"].append(ch1)

        ch2 = new_chapter(f"marathi_c{level}_ch2",
            lt("Barakhadi Builder", "बाराखडी", "बारहखड़ी"),
            lt("Joining matras to consonants.", "व्यंजनाला मात्रा जोडणे.", "व्यंजन में मात्रा जोड़ना।"), "pen")
        q_barakhadi(ch2, MR_CONSONANTS, MR_MATRAS, 12)
        c["chapters"].append(ch2)

        ch3 = new_chapter(f"marathi_c{level}_ch3",
            lt("Word Hunt", "शब्द शोध", "शब्द खोज"),
            lt("Words and their first letters.", "शब्द आणि आद्याक्षरे.", "शब्द और उनके पहले अक्षर।"), "search")
        q_letter_word_match(ch3, MR_WORDS, 12, "mr")
        c["chapters"].append(ch3)

        ch4 = new_chapter(f"marathi_c{level}_ch4",
            lt("What Does It Mean?", "अर्थ ओळखा", "अर्थ पहचानो"),
            lt("Marathi to English meanings.", "मराठी-इंग्रजी अर्थ.", "मराठी-अंग्रेज़ी अर्थ।"), "book")
        q_word_meaning(ch4, MR_WORDS, 12, "mr")
        c["chapters"].append(ch4)

        ch5 = new_chapter(f"marathi_c{level}_ch5",
            lt("Opposites and Plurals", "विरुद्धार्थी व अनेकवचन", "विलोम और बहुवचन"),
            lt("Opposite words and plural forms.", "विरुद्धार्थी शब्द व अनेकवचन.", "विलोम शब्द और बहुवचन।"), "swap")
        q_opposite(ch5, MR_OPPOSITES, 8, "mr")
        q_plural(ch5, MR_PLURALS, 6, "mr")
        c["chapters"].append(ch5)

        classes.append(c)
    return classes


def build_hindi():
    classes = []
    for level in range(1, 8):
        c = {"classLevel": level, "chapters": []}

        ch1 = new_chapter(f"hindi_c{level}_ch1",
            lt("Letters and Sounds", "अक्षरे आणि आवाज", "अक्षर और ध्वनि"),
            lt("Vowels and consonants.", "स्वर आणि व्यंजन.", "स्वर और व्यंजन।"), "abc")
        q_vowel_or_consonant(ch1, HI_VOWELS, HI_CONSONANTS, 11)
        c["chapters"].append(ch1)

        ch2 = new_chapter(f"hindi_c{level}_ch2",
            lt("Barahkhadi Builder", "बाराखडी", "बारहखड़ी"),
            lt("Joining matras to consonants.", "व्यंजनाला मात्रा जोडणे.", "व्यंजन में मात्रा जोड़ना।"), "pen")
        q_barakhadi(ch2, HI_CONSONANTS, MR_MATRAS, 12)
        c["chapters"].append(ch2)

        ch3 = new_chapter(f"hindi_c{level}_ch3",
            lt("Word Hunt", "शब्द शोध", "शब्द खोज"),
            lt("Words and their first letters.", "शब्द आणि आद्याक्षरे.", "शब्द और उनके पहले अक्षर।"), "search")
        q_letter_word_match(ch3, HI_WORDS, 12, "hi")
        c["chapters"].append(ch3)

        ch4 = new_chapter(f"hindi_c{level}_ch4",
            lt("What Does It Mean?", "अर्थ ओळखा", "अर्थ पहचानो"),
            lt("Hindi to English meanings.", "हिंदी-इंग्रजी अर्थ.", "हिंदी-अंग्रेज़ी अर्थ।"), "book")
        q_word_meaning(ch4, HI_WORDS, 12, "hi")
        c["chapters"].append(ch4)

        ch5 = new_chapter(f"hindi_c{level}_ch5",
            lt("Opposites and Plurals", "विरुद्धार्थी व अनेकवचन", "विलोम और बहुवचन"),
            lt("Opposite words and plural forms.", "विरुद्धार्थी शब्द व अनेकवचन.", "विलोम शब्द और बहुवचन।"), "swap")
        q_opposite(ch5, HI_OPPOSITES, 8, "hi")
        q_plural(ch5, HI_PLURALS, 6, "hi")
        c["chapters"].append(ch5)

        classes.append(c)
    return classes


def build_english():
    classes = []
    for level in range(1, 8):
        c = {"classLevel": level, "chapters": []}

        ch1 = new_chapter(f"english_c{level}_ch1",
            lt("A to Z Words", "A ते Z शब्द", "A से Z शब्द"),
            lt("Alphabet and starting sounds.", "मुळाक्षरे व आद्याक्षरे.", "वर्णमाला और पहले अक्षर।"), "abc")
        q_en_alphabet(ch1, 12)
        c["chapters"].append(ch1)

        ch2 = new_chapter(f"english_c{level}_ch2",
            lt("Opposite Words", "विरुद्धार्थी शब्द", "विलोम शब्द"),
            lt("Antonyms in English.", "इंग्रजी विरुद्धार्थी शब्द.", "अंग्रेज़ी विलोम शब्द।"), "swap")
        q_opposite(ch2, EN_OPPOSITES, 10, "en")
        c["chapters"].append(ch2)

        ch3 = new_chapter(f"english_c{level}_ch3",
            lt("One and Many", "एक आणि अनेक", "एक और अनेक"),
            lt("Singular and plural forms.", "एकवचन व अनेकवचन.", "एकवचन और बहुवचन।"), "copy")
        q_plural(ch3, EN_PLURALS, 10, "en")
        c["chapters"].append(ch3)

        ch4 = new_chapter(f"english_c{level}_ch4",
            lt("A or An", "A की An", "A या An"),
            lt("Choosing the right article.", "योग्य article निवडणे.", "सही article चुनना।"), "letter-a")
        q_en_article(ch4, 8)
        c["chapters"].append(ch4)

        ch5 = new_chapter(f"english_c{level}_ch5",
            lt("Yesterday's Words", "भूतकाळ", "भूतकाल"),
            lt("Past tense of common verbs.", "क्रियापदांचा भूतकाळ.", "क्रियाओं का भूतकाल।"), "clock")
        q_en_past_tense(ch5, 10)
        c["chapters"].append(ch5)

        classes.append(c)
    return classes


# ------------------------------------------------------------------ WRITE ---

SUBJECTS = [
    ("marathi", lt("Marathi", "मराठी", "मराठी"), "pen", "#15803D", 2, build_marathi),
    ("hindi", lt("Hindi", "हिंदी", "हिंदी"), "hindi-a", "#D6316B", 3, build_hindi),
    ("english", lt("English", "इंग्रजी", "अंग्रेज़ी"), "book", "#2563EB", 1, build_english),
]

for code, name, icon, color, order, builder in SUBJECTS:
    classes = builder()

    # Spread difficulty within each chapter so the Low/Medium/High picker has
    # real content in every band (same approach as the Maths generator).
    for c in classes:
        for ch in c["chapters"]:
            qs = ch["questions"]
            n = len(qs)
            for i, q in enumerate(qs):
                frac = i / max(1, n - 1) if n > 1 else 0.5
                q["difficulty"] = 2 if frac < 0.35 else (3 if frac < 0.7 else 4)

    seed = {
        "version": 1,
        "subject": {
            "code": code, "name": name, "iconKey": icon,
            "colorHex": color, "orderIndex": order
        },
        "classes": classes
    }

    path = f"app/src/main/assets/seed/{code}.json"
    with open(path, "w", encoding="utf-8") as f:
        json.dump(seed, f, ensure_ascii=False, indent=2)

    total_q = sum(len(ch["questions"]) for c in classes for ch in c["chapters"])
    total_ch = sum(len(c["chapters"]) for c in classes)
    print(f"{code}: {total_ch} chapters, {total_q} questions -> {path}")
