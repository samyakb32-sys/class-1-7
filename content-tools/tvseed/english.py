"""English (learning English) - Classes 2 to 7. Class 1 is kept from the existing bank."""
from common import L, S, Bank

P_WHICH = L("Which word {x}?", "कोणता शब्द {x}?", "कौन-सा शब्द {x}?")


def _a(noun):
    return ("an " if noun[0] in "aeiou" else "a ") + noun


def chapters(b: Bank):
    out = {}
    cid = lambda lvl, n: f"english_c{lvl}_ch{n}"

    # ================================================================== CLASS 2
    cats = {
        "animal": (L("animal", "प्राणी", "जानवर"), ["cow", "lion", "horse", "rabbit", "tiger", "goat", "monkey", "elephant"]),
        "fruit": (L("fruit", "फळ", "फल"), ["mango", "banana", "apple", "grapes", "orange", "guava", "papaya", "cherry"]),
        "vegetable": (L("vegetable", "भाजी", "सब्ज़ी"), ["potato", "onion", "carrot", "tomato", "spinach", "cabbage", "peas", "brinjal"]),
        "colour": (L("colour", "रंग", "रंग"), ["red", "blue", "green", "yellow", "pink", "purple", "white", "black"]),
        "body part": (L("part of the body", "शरीराचा अवयव", "शरीर का अंग"), ["hand", "eye", "nose", "ear", "leg", "mouth", "finger", "knee"]),
        "vehicle": (L("vehicle", "वाहन", "वाहन"), ["bus", "train", "bicycle", "aeroplane", "ship", "car", "truck", "rickshaw"]),
        "place": (L("place", "ठिकाण", "स्थान"), ["school", "hospital", "market", "temple", "garden", "library", "station", "farm"]),
        "person": (L("person", "व्यक्ती", "व्यक्ति"), ["teacher", "doctor", "farmer", "baker", "driver", "nurse", "tailor", "painter"]),
    }
    items = []
    for key, (name, words) in cats.items():
        others = [w for k, (_, ws) in cats.items() if k != key for w in ws]
        for w in b.rnd.sample(words, 2):
            items.append((name, w, b.rnd.sample(others, 3)))
    # the slot q must be a per-language word: build questions by hand so each language gets its own category word
    ch = {"id": cid(2, 1), "title": L("Naming Words", "नाम सांगणारे शब्द", "नाम बताने वाले शब्द"),
          "blurb": L("Animals, fruits, places and people.", "प्राणी, फळे, ठिकाणे आणि माणसे.", "जानवर, फल, स्थान और लोग।"),
          "iconKey": "tag", "questions": []}
    b.rnd.shuffle(items)
    for name, w, wr in items[:12]:
        opts = [{"text": S(w), "correct": True}] + [{"text": S(x), "correct": False} for x in wr]
        b.rnd.shuffle(opts)
        ch["questions"].append({
            "id": b.qid(ch["id"]),
            "prompt": L(f"Which of these is {_a(name['en'])}?", f"यापैकी '{name['mr']}' या गटातील शब्द कोणता?", f"इनमें से '{name['hi']}' वर्ग का शब्द कौन-सा है?"),
            "difficulty": 1,
            "hint": L(f"'{w}' is {_a(name['en'])}. Naming words tell us the name of a person, animal, place or thing.",
                      f"'{w}' हा '{name['mr']}' या गटातील शब्द आहे. नाम म्हणजे व्यक्ती, प्राणी, ठिकाण किंवा वस्तूचे नाव.",
                      f"'{w}' '{name['hi']}' वर्ग का शब्द है। नाम शब्द व्यक्ति, जानवर, स्थान या वस्तु का नाम बताते हैं।"),
            "options": opts,
        })

    verbs = ["run", "jump", "eat", "sing", "write", "read", "swim", "sleep", "drink", "dance", "climb", "throw"]
    non_verbs = ["table", "apple", "tree", "chair", "spoon", "house", "big", "red", "happy", "tall", "soft", "cold", "street", "window"]
    ch_verb = b.chapter(
        cid(2, 2), L("Action Words", "क्रिया दाखवणारे शब्द", "काम बताने वाले शब्द"),
        L("Words that say what we do.", "आपण काय करतो ते सांगणारे शब्द.", "हम क्या करते हैं, यह बताने वाले शब्द।"), "run",
        L("Which one is an action word?", "यापैकी क्रिया (कृती) दाखवणारा शब्द कोणता?", "इनमें से काम (क्रिया) बताने वाला शब्द कौन-सा है?"),
        L("'{a}' tells us what someone does, so it is an action word.", "'{a}' म्हणजे कोणीतरी काहीतरी करते, म्हणून तो क्रियावाचक शब्द आहे.", "'{a}' बताता है कि कोई क्या करता है, इसलिए यह क्रिया शब्द है।"),
        [(None, v) for v in verbs], pool=non_verbs, difficulty=1,
    )

    be = [("I ___ a boy.", "am"), ("She ___ happy.", "is"), ("They ___ my friends.", "are"), ("We ___ in class two.", "are"),
          ("The dog ___ big.", "is"), ("You ___ very kind.", "are"), ("It ___ a red ball.", "is"), ("The birds ___ in the sky.", "are"),
          ("He ___ my brother.", "is"), ("I ___ seven years old.", "am"), ("The books ___ on the table.", "are"), ("My mother ___ a teacher.", "is")]
    ch_be = b.chapter(
        cid(2, 3), L("Is, Am or Are", "is, am की are", "is, am या are"),
        L("Pick the right helping word.", "योग्य शब्द निवडा.", "सही शब्द चुनिए।"), "check",
        L("Fill in the blank: {q}", "रिकाम्या जागी योग्य शब्द निवडा: {q}", "खाली जगह भरिए: {q}"),
        L("Use 'am' with I, 'is' with one person or thing, and 'are' with you, we, they or many. Answer: {a}.",
          "I सोबत 'am', एकासाठी 'is', आणि you, we, they किंवा अनेकांसाठी 'are' वापरतात. उत्तर: {a}.",
          "I के साथ 'am', एक के लिए 'is', और you, we, they या कई के लिए 'are' लगता है। उत्तर: {a}।"),
        [(q, a, [x for x in ("is", "am", "are", "be") if x != a]) for q, a in be],
    )

    rhymes = [("cat", "hat"), ("sun", "run"), ("pen", "hen"), ("dog", "log"), ("bee", "tree"), ("star", "car"), ("king", "ring"),
              ("cake", "lake"), ("night", "light"), ("moon", "spoon"), ("bell", "shell"), ("rain", "train")]
    pool_r = [w for p in rhymes for w in p]
    ch_rhyme = b.chapter(
        cid(2, 4), L("Rhyming Words", "यमक जुळणारे शब्द", "तुक वाले शब्द"),
        L("Words that sound the same at the end.", "शेवटी सारखा आवाज असणारे शब्द.", "अंत में एक जैसी आवाज़ वाले शब्द।"), "music",
        L("Which word rhymes with '{q}'?", "'{q}' शी यमक जुळणारा शब्द कोणता?", "'{q}' से तुक मिलाने वाला शब्द कौन-सा है?"),
        L("'{q}' and '{a}' sound the same at the end.", "'{q}' आणि '{a}' यांचा शेवटचा आवाज सारखा आहे.", "'{q}' और '{a}' के अंत की आवाज़ एक जैसी है।"),
        [(q, a, [w for w in b.rnd.sample(pool_r, 12) if w not in (q, a) and not any(w in p and (q in p or a in p) for p in rhymes)][:3]) for q, a in rhymes], difficulty=1,
    )

    days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
    months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
    dm = []
    for i in (0, 2, 4, 5):
        dm.append((f"Which day comes after {days[i]}?", days[i + 1], b.rnd.sample([d for d in days if d not in (days[i], days[i + 1])], 3)))
    for i in (1, 3, 6):
        dm.append((f"Which day comes before {days[i]}?", days[i - 1], b.rnd.sample([d for d in days if d not in (days[i], days[i - 1])], 3)))
    for i in (0, 3, 5, 8, 10):
        dm.append((f"Which month comes after {months[i]}?", months[i + 1], b.rnd.sample([m for m in months if m not in (months[i], months[i + 1])], 3)))
    dm.append(("Which is the first month of the year?", "January", ["March", "July", "December"]))
    ch_dm = b.chapter(
        cid(2, 5), L("Days and Months", "वार आणि महिने", "दिन और महीने"),
        L("The week and the year in English.", "इंग्रजीतील आठवडा आणि वर्ष.", "अंग्रेज़ी में सप्ताह और साल।"), "calendar",
        L("{q}", "{q}", "{q}"),
        L("Say them in order: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday. Answer: {a}.", "क्रमाने म्हणा: Monday ते Sunday. उत्तर: {a}.", "क्रम से बोलिए: Monday से Sunday। उत्तर: {a}।"),
        [(q, a, w) for q, a, w in dm],
    )
    out[2] = [ch, ch_verb, ch_be, ch_rhyme, ch_dm]

    # ================================================================== CLASS 3
    kinds = {"noun": L("noun", "नाम", "संज्ञा"), "verb": L("verb", "क्रियापद", "क्रिया"),
             "adjective": L("adjective", "विशेषण", "विशेषण"), "pronoun": L("pronoun", "सर्वनाम", "सर्वनाम")}
    kw = [("table", "noun"), ("jump", "verb"), ("beautiful", "adjective"), ("she", "pronoun"), ("teacher", "noun"), ("write", "verb"),
          ("tall", "adjective"), ("they", "pronoun"), ("river", "noun"), ("laugh", "verb"), ("happy", "adjective"), ("he", "pronoun"),
          ("Delhi", "noun"), ("sleep", "verb")]
    ch = b.chapter(
        cid(3, 1), L("Kinds of Words", "शब्दांचे प्रकार", "शब्दों के प्रकार"),
        L("Noun, verb, adjective or pronoun?", "नाम, क्रियापद, विशेषण की सर्वनाम?", "संज्ञा, क्रिया, विशेषण या सर्वनाम?"), "shapes",
        L("What kind of word is '{q}'?", "'{q}' हा कोणत्या प्रकारचा शब्द आहे?", "'{q}' किस प्रकार का शब्द है?"),
        L("A noun names, a verb does, an adjective describes and a pronoun takes the place of a noun. '{q}' is one of these.",
          "नाम नाव सांगते, क्रियापद काम सांगते, विशेषण वर्णन करते, सर्वनाम नामाच्या जागी येते.",
          "संज्ञा नाम बताती है, क्रिया काम, विशेषण वर्णन करता है और सर्वनाम संज्ञा की जगह आता है।"),
        [(q, kinds[k], [v for kk, v in kinds.items() if kk != k]) for q, k in kw],
    )
    k3a = ch

    pron = [("Mary is tall. ___ is tall.", "She"), ("Ravi is my friend. ___ lives near me.", "He"), ("Ravi and Anu are twins. ___ are eight.", "They"),
            ("I and my sister go to school. ___ go by bus.", "We"), ("The bag is heavy. ___ is full of books.", "It"), ("Mother called me. ___ gave me a book.", "She"),
            ("The boys are playing. ___ are happy.", "They"), ("My father is a doctor. ___ works in a hospital.", "He"),
            ("The tree is tall. ___ has many leaves.", "It"), ("Sita and I are friends. ___ play together.", "We"),
            ("Dear Anu, ___ are my best friend.", "You"), ("Raju has a pen. The pen is ___.", "his")]
    pr_pool = ["She", "He", "It", "They", "We", "You", "I"]
    ch_pron = b.chapter(
        cid(3, 2), L("Pronouns", "सर्वनामे", "सर्वनाम"),
        L("Words that take the place of names.", "नावाच्या जागी येणारे शब्द.", "नाम की जगह आने वाले शब्द।"), "user",
        L("Fill in the blank: {q}", "रिकाम्या जागी योग्य शब्द निवडा: {q}", "खाली जगह भरिए: {q}"),
        L("Pronouns take the place of nouns, so we don't repeat names. Answer: {a}.", "सर्वनामे नामाच्या जागी येतात, म्हणून नाव पुन्हा पुन्हा सांगावे लागत नाही. उत्तर: {a}.", "सर्वनाम संज्ञा की जगह आते हैं, इसलिए नाम बार-बार नहीं दोहराना पड़ता। उत्तर: {a}।"),
        [(q, a, ([x for x in pr_pool if x != a][:3] if a != "his" else ["he", "him", "hers"])) for q, a in pron],
    )

    past = [("walk", "walked", "walking"), ("play", "played", "playing"), ("jump", "jumped", "jumping"), ("clean", "cleaned", "cleaning"),
            ("open", "opened", "opening"), ("cook", "cooked", "cooking"), ("call", "called", "calling"), ("help", "helped", "helping"),
            ("look", "looked", "looking"), ("watch", "watched", "watching"), ("paint", "painted", "painting"), ("climb", "climbed", "climbing")]
    ch_past = b.chapter(
        cid(3, 3), L("Yesterday's Actions", "कालच्या कृती", "कल के काम"),
        L("Add -ed for things already done.", "झालेल्या कामांसाठी -ed जोडा.", "हो चुके कामों के लिए -ed लगाइए।"), "clock",
        L("Yesterday I ___ ({q}).", "काल मी ___ ({q}). योग्य रूप निवडा.", "कल मैंने ___ ({q})। सही रूप चुनिए।"),
        L("For something that happened in the past we add -ed: {a}.", "भूतकाळातील कामासाठी -ed जोडतो: {a}.", "बीते हुए काम के लिए -ed लगाते हैं: {a}।"),
        [(b_, p, [b_, g, b_ + "s"]) for b_, p, g in past],
    )

    plur = [("baby", "babies"), ("child", "children"), ("man", "men"), ("foot", "feet"), ("tooth", "teeth"), ("mouse", "mice"), ("woman", "women"),
            ("leaf", "leaves"), ("knife", "knives"), ("sheep", "sheep"), ("bus", "buses"), ("box", "boxes"), ("city", "cities"), ("story", "stories")]
    bad_pl = {"baby": ["babys", "babyes", "baby"], "child": ["childs", "childes", "childrens"], "man": ["mans", "manes", "mens"], "foot": ["foots", "feets", "footes"],
              "tooth": ["tooths", "teeths", "toothes"], "mouse": ["mouses", "mices", "mouse"], "woman": ["womans", "womens", "womanes"], "leaf": ["leafs", "leafes", "leavs"],
              "knife": ["knifes", "knifs", "knivs"], "sheep": ["sheeps", "sheepes", "sheepies"], "bus": ["buss", "bus", "busies"], "box": ["boxs", "boxies", "box"],
              "city": ["citys", "cityes", "city"], "story": ["storys", "storyes", "story"]}
    ch_plur = b.chapter(
        cid(3, 4), L("Tricky Plurals", "अवघड अनेकवचने", "मुश्किल बहुवचन"),
        L("Babies, mice, feet and more.", "babies, mice, feet आणि बरेच काही.", "babies, mice, feet और बहुत कुछ।"), "copy",
        L("What is the plural of '{q}'?", "'{q}' चे अनेकवचन कोणते?", "'{q}' का बहुवचन क्या है?"),
        L("The plural of '{q}' is '{a}'. Some words change their spelling instead of just adding -s.", "'{q}' चे अनेकवचन '{a}' आहे. काही शब्दांचे स्पेलिंग बदलते, फक्त -s जोडत नाहीत.", "'{q}' का बहुवचन '{a}' है। कुछ शब्दों की वर्तनी बदल जाती है, सिर्फ़ -s नहीं लगता।"),
        [(s, p, bad_pl[s]) for s, p in plur],
    )

    prep = [("We keep milk ___ the fridge.", "in", ["between", "into", "to"]), ("The lamp is ___ the table.", "on", ["into", "between", "to"]),
            ("The ball rolled ___ the bed, so I couldn't see it.", "under", ["above", "on", "between"]), ("The cat is hiding ___ the door.", "behind", ["into", "between", "to"]),
            ("The teacher stands ___ the two girls.", "between", ["on", "under", "into"]), ("Fish live ___ water.", "in", ["on", "between", "behind"]),
            ("The picture hangs ___ the wall.", "on", ["into", "between", "under"]), ("The plane flew ___ the clouds, high above them.", "above", ["under", "between", "into"]),
            ("Our school is ___ the park, just a short walk away.", "near", ["into", "between", "under"]), ("The mouse ran ___ the hole.", "into", ["on", "above", "between"]),
            ("Please sit ___ me, right beside me.", "next to", ["under", "into", "above"]), ("She put the letter ___ the envelope.", "in", ["under", "between", "behind"])]
    ch_prep = b.chapter(
        cid(3, 5), L("In, On, Under", "in, on, under", "in, on, under"),
        L("Words that tell where things are.", "वस्तू कुठे आहेत ते सांगणारे शब्द.", "चीज़ें कहाँ हैं, यह बताने वाले शब्द।"), "pin",
        L("Fill in the blank: {q}", "रिकाम्या जागी योग्य शब्द निवडा: {q}", "खाली जगह भरिए: {q}"),
        L("Position words show where something is. The best word here is '{a}'.", "स्थान दाखवणारे शब्द वस्तू कुठे आहे ते सांगतात. येथे योग्य शब्द '{a}' आहे.", "स्थान बताने वाले शब्द चीज़ की जगह बताते हैं। यहाँ सही शब्द '{a}' है।"),
        prep,
    )
    out[3] = [k3a, ch_pron, ch_past, ch_plur, ch_prep]

    # ================================================================== CLASS 4
    tenses = {"present": L("Present", "वर्तमान", "वर्तमान"), "past": L("Past", "भूतकाळ", "भूतकाल"), "future": L("Future", "भविष्यकाळ", "भविष्यकाल")}
    ts = [("She plays cricket every day.", "present"), ("They went to school yesterday.", "past"), ("I will eat a mango tomorrow.", "future"),
          ("The sun rises in the east.", "present"), ("We visited our grandmother last Sunday.", "past"), ("He will come to the party.", "future"),
          ("My sister sings well.", "present"), ("The train arrived an hour ago.", "past"), ("It will rain tonight.", "future"),
          ("Birds build nests in trees.", "present"), ("Rita painted a picture last week.", "past"), ("We shall go to Pune next month.", "future")]
    ch_tense = b.chapter(
        cid(4, 1), L("Past, Present, Future", "भूत, वर्तमान, भविष्य", "भूत, वर्तमान, भविष्य"),
        L("When did it happen?", "ते केव्हा घडले?", "यह कब हुआ?"), "clock",
        L("Which tense is this sentence? {q}", "हे वाक्य कोणत्या काळातील आहे? {q}", "यह वाक्य किस काल का है? {q}"),
        L("Look at the time clue and the verb.", "वेळ दाखवणारा शब्द आणि क्रियापद पाहा.", "समय बताने वाला शब्द और क्रिया देखिए।"),
        [(q, tenses[k], [v for kk, v in tenses.items() if kk != k]) for q, k in ts],
    )
    ch_tense = _fix_dict_hint(ch_tense, ts, tenses)

    cmp_ = [("tall", "taller", "tallest"), ("big", "bigger", "biggest"), ("small", "smaller", "smallest"), ("fast", "faster", "fastest"), ("good", "better", "best"),
            ("bad", "worse", "worst"), ("happy", "happier", "happiest"), ("long", "longer", "longest"), ("hot", "hotter", "hottest"), ("old", "older", "oldest"),
            ("heavy", "heavier", "heaviest"), ("cold", "colder", "coldest")]
    items = []
    for a, c, s in cmp_:
        if b.rnd.random() < 0.5:
            items.append((f"Ravi is ___ than Raju. ({a})", c, [a, s, "more " + a]))
        else:
            items.append((f"Ravi is the ___ boy in class. ({a})", s, [a, c, "most " + a]))
    ch_cmp = b.chapter(
        cid(4, 2), L("Comparing Things", "तुलना करा", "तुलना कीजिए"),
        L("Tall, taller, tallest.", "tall, taller, tallest.", "tall, taller, tallest."), "scale",
        L("Fill in the blank: {q}", "रिकाम्या जागी योग्य शब्द निवडा: {q}", "खाली जगह भरिए: {q}"),
        L("Use 'than' with the comparing form (taller). Use 'the' with the top form (tallest). Answer: {a}.", "दोघांची तुलना करताना 'than' सोबत (taller) वापरा; सर्वांत जास्त सांगताना 'the' सोबत (tallest). उत्तर: {a}.", "दो की तुलना में 'than' के साथ (taller) और सबसे ज़्यादा बताने में 'the' के साथ (tallest) लगाइए। उत्तर: {a}।"),
        items,
    )

    syn = [("big", "large"), ("small", "tiny"), ("happy", "glad"), ("begin", "start"), ("clever", "smart"), ("quick", "fast"), ("shut", "close"),
           ("rich", "wealthy"), ("silent", "quiet"), ("strong", "powerful"), ("pretty", "beautiful"), ("sleepy", "tired")]
    syn_pool = [w for p in syn for w in p]
    ch_syn = b.chapter(
        cid(4, 3), L("Words with the Same Meaning", "सारखा अर्थ असलेले शब्द", "समान अर्थ वाले शब्द"),
        L("Synonyms: big and large.", "समानार्थी शब्द: big आणि large.", "पर्यायवाची: big और large।"), "equal",
        L("Which word means the same as '{q}'?", "'{q}' च्या सारखा अर्थ असलेला शब्द कोणता?", "'{q}' के समान अर्थ वाला शब्द कौन-सा है?"),
        L("'{q}' and '{a}' mean almost the same thing.", "'{q}' आणि '{a}' यांचा अर्थ जवळजवळ सारखाच आहे.", "'{q}' और '{a}' का अर्थ लगभग एक जैसा है।"),
        [(q, a, [w for w in b.rnd.sample(syn_pool, len(syn_pool)) if w not in (q, a) and not any(w in p and (q in p or a in p) for p in syn)][:3]) for q, a in syn],
    )

    punct = [("Where do you live", "?"), ("My name is Anu", "."), ("What a lovely day", "!"), ("How old are you", "?"), ("The sky is blue", "."),
             ("Wow, that is a huge fish", "!"), ("Do you like ice cream", "?"), ("We are going to the zoo", "."), ("Watch out, a snake is there", "!"), ("Who is at the door", "?"),
             ("I have two brothers", "."), ("Help, a fire", "!")]
    ch_punct = b.chapter(
        cid(4, 4), L("Full Stop, Question Mark, Exclamation", "पूर्णविराम, प्रश्नचिन्ह, उद्गारचिन्ह", "पूर्णविराम, प्रश्नचिह्न, विस्मयादिबोधक"),
        L("Which mark ends the sentence?", "वाक्याच्या शेवटी कोणते चिन्ह येते?", "वाक्य के अंत में कौन-सा चिह्न आता है?"), "dot",
        L("Which mark goes at the end? \"{q}\"", "वाक्याच्या शेवटी कोणते चिन्ह येईल? \"{q}\"", "वाक्य के अंत में कौन-सा चिह्न लगेगा? \"{q}\""),
        L("A telling sentence ends with '.', a question with '?', and a surprise or strong feeling with '!'. Answer: {a}", "सांगणाऱ्या वाक्याला '.', प्रश्नाला '?', आणि आश्चर्य किंवा तीव्र भावनेला '!' येते. उत्तर: {a}", "बताने वाले वाक्य के अंत में '.', प्रश्न में '?', और हैरानी या तेज़ भावना में '!' लगता है। उत्तर: {a}"),
        [(q, a, [m for m in (".", "?", "!", ",") if m != a]) for q, a in punct],
    )

    conj = [("It was raining, ___ we stayed inside.", "so", ["but", "or", "because"]), ("We stayed inside ___ it was raining.", "because", ["but", "or", "so"]),
            ("He is poor ___ he is happy.", "but", ["or", "because", "so"]), ("You can have milk ___ juice.", "or", ["but", "because", "so"]),
            ("I like tea ___ coffee.", "and", ["because", "so", "but"]), ("She is small ___ very strong.", "but", ["or", "because", "so"]),
            ("I took an umbrella ___ it might rain.", "because", ["but", "or", "so"]), ("Do you want to walk ___ take the bus?", "or", ["because", "so", "but"]),
            ("Ravi ___ Anu are cousins.", "and", ["because", "so", "but"]), ("I was tired, ___ I went to bed early.", "so", ["but", "or", "because"]),
            ("He ran fast ___ he missed the bus.", "but", ["or", "because", "so"]), ("She was late ___ the bus broke down.", "because", ["but", "or", "so"])]
    ch_conj = b.chapter(
        cid(4, 5), L("Joining Words", "जोडणारे शब्द", "जोड़ने वाले शब्द"),
        L("and, but, or, because, so.", "and, but, or, because, so.", "and, but, or, because, so."), "link",
        L("Choose the joining word: {q}", "योग्य जोडशब्द निवडा: {q}", "सही जोड़ने वाला शब्द चुनिए: {q}"),
        L("'and' adds, 'but' shows a difference, 'or' gives a choice, 'because' gives a reason and 'so' gives a result. Answer: {a}.", "'and' जोडतो, 'but' फरक दाखवतो, 'or' पर्याय देतो, 'because' कारण देतो आणि 'so' परिणाम सांगतो. उत्तर: {a}.", "'and' जोड़ता है, 'but' अंतर दिखाता है, 'or' विकल्प देता है, 'because' कारण और 'so' नतीजा बताता है। उत्तर: {a}।"),
        conj,
    )
    out[4] = [ch_tense, ch_cmp, ch_syn, ch_punct, ch_conj]

    # ================================================================== CLASS 5
    adv = [("quick", "quickly"), ("slow", "slowly"), ("careful", "carefully"), ("loud", "loudly"), ("happy", "happily"), ("easy", "easily"),
           ("bad", "badly"), ("neat", "neatly"), ("brave", "bravely"), ("kind", "kindly"), ("angry", "angrily"), ("soft", "softly")]
    adv_pool = [a for _, a in adv]
    ch_adv = b.chapter(
        cid(5, 1), L("Adverbs", "क्रियाविशेषणे", "क्रियाविशेषण"),
        L("Words that tell how something is done.", "काम कसे केले जाते ते सांगणारे शब्द.", "काम कैसे किया गया, यह बताने वाले शब्द।"), "speed",
        L("She did it ___. ({q})", "तिने ते ___ केले. ({q} चे योग्य रूप निवडा)", "उसने यह ___ किया। ({q} का सही रूप चुनिए)"),
        L("An adverb tells how. We often make it by adding -ly: {q} becomes {a}.", "क्रियाविशेषण 'कसे' ते सांगते. बहुधा -ly जोडून ते बनते: {q} चे {a}.", "क्रियाविशेषण 'कैसे' बताता है। अक्सर -ly लगाकर बनता है: {q} से {a}।"),
        [(q, a, [q] + b.rnd.sample([x for x in adv_pool if x != a], 2)) for q, a in adv],
    )

    irr = [("go", "went", "gone"), ("eat", "ate", "eaten"), ("see", "saw", "seen"), ("come", "came", "come"), ("run", "ran", "run"), ("write", "wrote", "written"),
           ("take", "took", "taken"), ("give", "gave", "given"), ("sing", "sang", "sung"), ("swim", "swam", "swum"), ("drink", "drank", "drunk"), ("buy", "bought", "bought")]
    items = []
    for base, p, pp in irr:
        w = [base + "ed", base + "s", base + "ing"]
        if pp != p:
            w[1] = pp
        if base in ("come", "run"):
            w = [base + "ed", base + "s", base + "ing"]
        items.append((base, p, w))
    ch_irr = b.chapter(
        cid(5, 2), L("Verbs That Change", "बदलणारी क्रियापदे", "बदलने वाली क्रियाएँ"),
        L("go - went, eat - ate, see - saw.", "go - went, eat - ate, see - saw.", "go - went, eat - ate, see - saw."), "swap",
        L("Yesterday I ___ ({q}). Pick the past form.", "काल मी ___ ({q}). भूतकाळाचे रूप निवडा.", "कल मैंने ___ ({q})। भूतकाल का रूप चुनिए।"),
        L("Some verbs don't take -ed. The past of {q} is '{a}'.", "काही क्रियापदांना -ed लागत नाही. {q} चे भूतकाळी रूप '{a}' आहे.", "कुछ क्रियाओं में -ed नहीं लगता। {q} का भूतकाल '{a}' है।"),
        items,
    )

    sv = [("The boys ___ playing.", "are", ["is", "am", "be"]), ("She ___ to school every day.", "goes", ["go", "going", "gone"]), ("My friends ___ here.", "are", ["is", "am", "be"]),
          ("The dog ___ loudly.", "barks", ["bark", "barking", "barkes"]), ("They ___ cricket on Sundays.", "play", ["plays", "playing", "playes"]),
          ("He ___ a cold.", "has", ["have", "having", "haves"]), ("The children ___ happy.", "are", ["is", "am", "be"]), ("Ravi ___ his homework every evening.", "does", ["do", "doing", "doed"]),
          ("We ___ lunch at one o'clock.", "have", ["has", "having", "haves"]), ("Anu ___ a book now and then.", "reads", ["reading", "readed", "are reading"]),
          ("The cows ___ grass.", "eat", ["eats", "eating", "eaten"]), ("Mother ___ food for us.", "cooks", ["cook", "cooking", "cookes"])]
    ch_sv = b.chapter(
        cid(5, 3), L("Subject and Verb Match", "कर्ता आणि क्रियापद जुळवा", "कर्ता और क्रिया का मेल"),
        L("he goes, they go.", "he goes, they go.", "he goes, they go।"), "puzzle",
        L("Choose the right verb: {q}", "योग्य क्रियापद निवडा: {q}", "सही क्रिया चुनिए: {q}"),
        L("One person or thing takes a verb with -s (she goes). Many take the plain verb (they go). Answer: {a}.", "एकासाठी -s असलेले क्रियापद (she goes), अनेकांसाठी साधे क्रियापद (they go). उत्तर: {a}.", "एक के लिए -s वाली क्रिया (she goes), कई के लिए सादी क्रिया (they go)। उत्तर: {a}।"),
        sv,
    )

    hom = [("I can ___ the birds singing.", "hear", "here"), ("Come ___ and sit next to me.", "here", "hear"), ("The ___ shines in the sky.", "sun", "son"),
           ("I ___ an apple for lunch.", "ate", "eight"), ("She has ___ pens.", "two", "too"), ("I ___ the answer.", "know", "no"),
           ("Turn ___ at the corner.", "right", "write"), ("We swam in the ___.", "sea", "see"), ("We eat ___ and fish.", "meat", "meet"),
           ("A rose is a ___.", "flower", "flour"), ("The dog wagged its ___.", "tail", "tale"), ("We walked ___ the long tunnel.", "through", "threw")]
    hpool = [w for _, a, bb in hom for w in (a, bb)]
    ch_hom = b.chapter(
        cid(5, 4), L("Sound-alike Words", "सारखे ऐकू येणारे शब्द", "एक जैसी आवाज़ वाले शब्द"),
        L("hear or here? sun or son?", "hear की here? sun की son?", "hear या here? sun या son?"), "ear",
        L("Pick the right word: {q}", "योग्य शब्द निवडा: {q}", "सही शब्द चुनिए: {q}"),
        L("Some words sound alike but have different meanings and spellings. Here the right word is '{a}'.", "काही शब्द सारखे ऐकू येतात पण अर्थ आणि स्पेलिंग वेगळे असते. येथे '{a}' योग्य आहे.", "कुछ शब्द एक जैसे सुनाई देते हैं पर उनका अर्थ और वर्तनी अलग होती है। यहाँ '{a}' सही है।"),
        [(q, a, [twin] + b.rnd.sample([w for w in hpool if w not in (a, twin)], 2)) for q, a, twin in hom],
    )

    pre = [("un", "happy", "unhappy", ["dishappy", "rehappy", "mishappy"]), ("dis", "agree", "disagree", ["unagree", "preagree", "misagree"]),
           ("un", "tie", "untie", ["distie", "mistie", "pretie"]), ("pre", "heat", "preheat", ["unheat", "disheat", "misheat"]),
           ("mis", "spell", "misspell", ["unspell", "prespell", "disspell"]), ("un", "kind", "unkind", ["diskind", "rekind", "miskind"]),
           ("dis", "honest", "dishonest", ["unhonest", "rehonest", "mishonest"]), ("mis", "understand", "misunderstand", ["ununderstand", "disunderstand", "preunderstand"]),
           ("un", "fair", "unfair", ["disfair", "refair", "misfair"]), ("pre", "view", "preview", ["unview", "disview", "misview"]),
           ("dis", "obey", "disobey", ["unobey", "preobey", "misobey"]), ("mis", "lead", "mislead", ["unlead", "dislead", "prelead"]),
           ("mis", "take", "mistake", ["untake", "distake", "pretake"]), ("pre", "pay", "prepay", ["unpay", "dispay", "mispay"])]
    items = [(f"{p}- + {base}", word, w) for p, base, word, w in pre]
    ch_pre = b.chapter(
        cid(5, 5), L("Word Beginnings", "शब्दाची सुरुवात (उपसर्ग)", "शब्द की शुरुआत (उपसर्ग)"),
        L("un-, dis-, re-, pre-, mis-.", "un-, dis-, re-, pre-, mis-.", "un-, dis-, re-, pre-, mis-।"), "build",
        L("Which word do you make? {q}", "कोणता शब्द तयार होतो? {q}", "कौन-सा शब्द बनता है? {q}"),
        L("A prefix goes in front of a word and changes its meaning: un- and dis- mean 'not', re- means 'again', pre- means 'before', mis- means 'wrongly'. Answer: {a}.", "उपसर्ग शब्दाच्या आधी येऊन अर्थ बदलतो: un-, dis- = नाही; re- = पुन्हा; pre- = आधी; mis- = चुकीचे. उत्तर: {a}.", "उपसर्ग शब्द के आगे लगकर अर्थ बदलता है: un-, dis- = नहीं; re- = फिर से; pre- = पहले; mis- = ग़लत। उत्तर: {a}।"),
        items,
    )
    out[5] = [ch_adv, ch_irr, ch_sv, ch_hom, ch_pre]

    # ================================================================== CLASS 6
    cont = [("Look! The baby ___.", "is sleeping", ["sleeps", "slept", "has slept"]), ("She ___ tea every morning.", "drinks", ["is drinking", "will drink", "has drunk"]),
            ("They ___ football yesterday.", "played", ["play", "are playing", "have played"]), ("I ___ my homework already.", "have finished", ["am finishing", "finish", "will finish"]),
            ("We ___ to Pune tomorrow.", "will go", ["went", "have gone", "were going"]), ("He ___ a book at the moment.", "is reading", ["reads", "read", "has read"]),
            ("The train ___ just ___.", "has; left", ["is; leaving", "will; leave", "did; left"]),
            ("Water ___ at 100 degrees.", "boils", ["is boiling", "boiled", "has boiled"]), ("Right now it ___.", "is raining", ["rains", "rained", "will rain"]),
            ("Last year we ___ to Goa.", "went", ["go", "are going", "have gone"]), ("Hurry! The bus ___.", "is coming", ["comes", "came", "camed"]),
            ("I ___ this film twice already.", "have seen", ["am seeing", "see", "will see"])]
    cont = [(q, a.replace("; ", " ... ") if ";" in a else a, [w.replace("; ", " ... ") for w in ws]) for q, a, ws in cont]
    ch_cont = b.chapter(
        cid(6, 1), L("Choose the Right Tense", "योग्य काळ निवडा", "सही काल चुनिए"),
        L("is doing, did, has done, will do.", "is doing, did, has done, will do.", "is doing, did, has done, will do।"), "clock",
        L("Choose the correct form: {q}", "योग्य रूप निवडा: {q}", "सही रूप चुनिए: {q}"),
        L("Look at the clue (now, yesterday, already, tomorrow, every day). The right form is: {a}.", "वेळेचा संकेत पाहा (now, yesterday, already, tomorrow, every day). योग्य रूप: {a}.", "समय का संकेत देखिए (now, yesterday, already, tomorrow, every day)। सही रूप: {a}।"),
        cont,
    )

    st = {"statement": L("statement", "विधान", "कथन"), "question": L("question", "प्रश्न", "प्रश्न"), "command": L("command", "आज्ञा", "आज्ञा"), "exclamation": L("exclamation", "उद्गार", "विस्मय")}
    sent = [("The sky is blue.", "statement"), ("Is it raining?", "question"), ("Close the door.", "command"), ("What a big fish!", "exclamation"),
            ("Ravi plays football.", "statement"), ("Where is my bag?", "question"), ("Please sit down.", "command"), ("How brave you are!", "exclamation"),
            ("We went to the zoo.", "statement"), ("Do you like mangoes?", "question"), ("Open your books.", "command"), ("What a beautiful garden!", "exclamation")]
    ch_st = b.chapter(
        cid(6, 2), L("Kinds of Sentences", "वाक्यांचे प्रकार", "वाक्यों के प्रकार"),
        L("Telling, asking, ordering, wondering.", "सांगणे, विचारणे, आज्ञा, आश्चर्य.", "बताना, पूछना, आदेश, हैरानी।"), "chat",
        L("What kind of sentence is this? \"{q}\"", "हे कोणत्या प्रकारचे वाक्य आहे? \"{q}\"", "यह किस प्रकार का वाक्य है? \"{q}\""),
        L("A statement tells, a question asks, a command orders and an exclamation shows strong feeling.", "विधान सांगते, प्रश्न विचारतो, आज्ञा हुकूम देते आणि उद्गार तीव्र भावना दाखवतो.", "कथन बताता है, प्रश्न पूछता है, आज्ञा आदेश देती है और विस्मय तेज़ भावना दिखाता है।"),
        [(q, st[k], [v for kk, v in st.items() if kk != k]) for q, k in sent],
    )

    idi = [("a piece of cake", "very easy"), ("under the weather", "feeling ill"), ("break the ice", "start a friendly talk"), ("once in a blue moon", "very rarely"),
           ("hit the sack", "go to bed"), ("spill the beans", "tell a secret"), ("a blessing in disguise", "a good thing that seemed bad"),
           ("cost an arm and a leg", "very expensive"), ("beat around the bush", "avoid the main point"), ("bite the bullet", "face something hard bravely"),
           ("call it a day", "stop working"), ("let the cat out of the bag", "reveal a secret by mistake")]
    idi_pool = [m for _, m in idi]
    ch_idi = b.chapter(
        cid(6, 3), L("Idioms", "वाक्प्रचार", "मुहावरे"),
        L("Sayings with a hidden meaning.", "लपलेला अर्थ असलेले शब्दसमूह.", "छिपे अर्थ वाले वाक्यांश।"), "bulb",
        L("What does '{q}' mean?", "'{q}' म्हणजे काय?", "'{q}' का क्या अर्थ है?"),
        L("An idiom doesn't mean its words exactly. '{q}' means '{a}'.", "वाक्प्रचाराचा अर्थ शब्दशः नसतो. '{q}' म्हणजे '{a}'.", "मुहावरे का अर्थ शब्दों जैसा नहीं होता। '{q}' का अर्थ '{a}' है।"),
        [(q, a, [m for m in b.rnd.sample(idi_pool, len(idi_pool)) if m != a and not (a == "tell a secret" and m == "reveal a secret by mistake") and not (a == "reveal a secret by mistake" and m == "tell a secret")][:3]) for q, a in idi],
        difficulty=3,
    )

    spl = [("receive", ["recieve", "receeve", "reseive"]), ("believe", ["beleive", "beleave", "belive"]), ("necessary", ["neccessary", "necesary", "neccesary"]),
           ("separate", ["seperate", "separete", "saparate"]), ("friend", ["freind", "frend", "friand"]), ("beautiful", ["beutiful", "beautifull", "beatiful"]),
           ("because", ["becuase", "becouse", "becasue"]), ("calendar", ["calender", "calandar", "calander"]), ("definitely", ["definately", "definitly", "definitley"]),
           ("government", ["goverment", "govenment", "governmnt"]), ("tomorrow", ["tommorow", "tomorow", "tommorrow"]), ("library", ["libary", "liberry", "librery"])]
    ch_spl = b.chapter(
        cid(6, 4), L("Spelling Check", "स्पेलिंग तपासा", "वर्तनी जाँचिए"),
        L("Which spelling is right?", "योग्य स्पेलिंग कोणते?", "सही वर्तनी कौन-सी है?"), "spell",
        L("Which spelling is correct?", "योग्य स्पेलिंग कोणते?", "सही वर्तनी कौन-सी है?"),
        L("The correct spelling is '{a}'. Say it slowly, sound by sound.", "योग्य स्पेलिंग '{a}' आहे. हळू हळू, आवाजानुसार म्हणा.", "सही वर्तनी '{a}' है। धीरे-धीरे, आवाज़ के हिसाब से बोलिए।"),
        [(None, w, list(dict.fromkeys(ws))[:3]) for w, ws in spl], difficulty=3,
    )

    voc = [("generous", "gives freely to others"), ("fragile", "easily broken"), ("ancient", "very old"), ("curious", "eager to learn or know"), ("enormous", "very big"),
           ("reluctant", "unwilling"), ("fortunate", "lucky"), ("abundant", "more than enough"), ("cautious", "careful to avoid danger"), ("humble", "not proud"),
           ("vacant", "empty"), ("diligent", "hard-working")]
    voc_pool = [d for _, d in voc]
    ch_voc = b.chapter(
        cid(6, 5), L("Word Power", "शब्दसंपत्ती", "शब्द भंडार"),
        L("New words and their meanings.", "नवे शब्द आणि त्यांचे अर्थ.", "नए शब्द और उनके अर्थ।"), "star",
        L("What does '{q}' mean?", "'{q}' चा अर्थ काय?", "'{q}' का अर्थ क्या है?"),
        L("'{q}' means '{a}'. Try using it in a sentence of your own.", "'{q}' म्हणजे '{a}'. त्याचे स्वतःचे वाक्य बनवून पाहा.", "'{q}' का अर्थ '{a}' है। इससे अपना एक वाक्य बनाकर देखिए।"),
        [(q, a, [d for d in b.rnd.sample(voc_pool, len(voc_pool)) if d != a][:3]) for q, a in voc], difficulty=3,
    )
    out[6] = [ch_cont, ch_st, ch_idi, ch_spl, ch_voc]

    # ================================================================== CLASS 7
    act = [("Ravi wrote a letter.", "A letter was written by Ravi.", ["A letter is written by Ravi.", "A letter has written by Ravi.", "A letter wrote Ravi."]),
           ("The cook makes soup.", "Soup is made by the cook.", ["Soup was made by the cook.", "Soup is making by the cook.", "Soup makes the cook."]),
           ("Children love ice cream.", "Ice cream is loved by children.", ["Ice cream was loved by children.", "Ice cream loves children.", "Ice cream is love by children."]),
           ("She sang a song.", "A song was sung by her.", ["A song is sung by her.", "A song was sang by her.", "A song sang her."]),
           ("The teacher praised the students.", "The students were praised by the teacher.", ["The students was praised by the teacher.", "The students are praised by the teacher.", "The teacher was praised by the students."]),
           ("They will paint the wall.", "The wall will be painted by them.", ["The wall was painted by them.", "The wall will painted by them.", "The wall is painted by them."]),
           ("The dog chased the thief.", "The thief was chased by the dog.", ["The thief is chased by the dog.", "The dog was chased by the thief.", "The thief chased the dog."]),
           ("Mother cooks dinner.", "Dinner is cooked by mother.", ["Dinner was cooked by mother.", "Dinner is cooking by mother.", "Mother is cooked by dinner."]),
           ("Farmers grow rice.", "Rice is grown by farmers.", ["Rice was grown by farmers.", "Rice is growing by farmers.", "Rice grows farmers."]),
           ("He broke the window.", "The window was broken by him.", ["The window is broken by him.", "The window was broke by him.", "The window broke him."]),
           ("We celebrate Diwali.", "Diwali is celebrated by us.", ["Diwali was celebrated by us.", "Diwali is celebrating by us.", "We are celebrated by Diwali."]),
           ("Sita is painting a picture.", "A picture is being painted by Sita.", ["A picture was painted by Sita.", "A picture is painted by Sita.", "A picture has been painted by Sita."])]
    ch_act = b.chapter(
        cid(7, 1), L("Active and Passive Voice", "कर्तरी आणि कर्मणी प्रयोग", "कर्तृवाच्य और कर्मवाच्य"),
        L("Who does it, or what it happens to.", "कोण करतो, किंवा कशावर घडते.", "कौन करता है, या किस पर होता है।"), "swap",
        L("Which is the passive form of: \"{q}\"", "\"{q}\" चे कर्मणी (passive) रूप कोणते?", "\"{q}\" का कर्मवाच्य (passive) रूप कौन-सा है?"),
        L("Passive: the receiver comes first: object + be + past participle (+ by ...). Answer: {a}", "कर्मणी वाक्यात क्रिया ज्यावर घडते ते आधी येते: कर्म + 'be' चे रूप + past participle (+ by ...). उत्तर: {a}", "कर्मवाच्य में जिस पर क्रिया होती है वह पहले आता है: कर्म + 'be' का रूप + past participle (+ by ...)। उत्तर: {a}"),
        act, difficulty=3,
    )

    dis = [("He said, \"I am tired.\"", "He said that he was tired.", ["He said that I am tired.", "He said that he is tired.", "He said me that he was tired."]),
           ("She said, \"I like mangoes.\"", "She said that she liked mangoes.", ["She said that I liked mangoes.", "She said that she likes mangoes.", "She says that she liked mangoes."]),
           ("Ravi said, \"I will come.\"", "Ravi said that he would come.", ["Ravi said that he will come.", "Ravi said that I would come.", "Ravi said that he had come."]),
           ("They said, \"We are happy.\"", "They said that they were happy.", ["They said that we were happy.", "They said that they are happy.", "They said that they had happy."]),
           ("Mother said, \"I am cooking.\"", "Mother said that she was cooking.", ["Mother said that she is cooking.", "Mother said that I was cooking.", "Mother said that she has cooked."]),
           ("He said, \"I went to Pune.\"", "He said that he had gone to Pune.", ["He said that he went to Pune.", "He said that I had gone to Pune.", "He said that he has gone to Pune."]),
           ("Anu said, \"I can swim.\"", "Anu said that she could swim.", ["Anu said that she can swim.", "Anu said that I could swim.", "Anu said that she would swim."]),
           ("The teacher said, \"Work hard.\"", "The teacher told us to work hard.", ["The teacher said us work hard.", "The teacher told that work hard.", "The teacher asked that we work hard."]),
           ("He said, \"Open the door.\"", "He told me to open the door.", ["He said me open the door.", "He told that open the door.", "He told me opened the door."]),
           ("She asked, \"Where do you live?\"", "She asked where I lived.", ["She asked where do I live.", "She asked where I live.", "She asked that where I lived."]),
           ("Dad said, \"I am busy.\"", "Dad said that he was busy.", ["Dad said that I was busy.", "Dad said that he is busy.", "Dad said that he has been busy."]),
           ("They said, \"We have finished.\"", "They said that they had finished.", ["They said that we had finished.", "They said that they have finished.", "They said that they finished."])]
    ch_dis = b.chapter(
        cid(7, 2), L("Direct and Indirect Speech", "प्रत्यक्ष आणि अप्रत्यक्ष कथन", "प्रत्यक्ष और अप्रत्यक्ष कथन"),
        L("Telling what someone said.", "कुणी काय म्हणाले ते सांगणे.", "किसी ने क्या कहा, यह बताना।"), "quote",
        L("Change into indirect speech: {q}", "अप्रत्यक्ष कथनात बदला: {q}", "अप्रत्यक्ष कथन में बदलिए: {q}"),
        L("In indirect speech the pronoun changes to fit the speaker and the verb moves one step back in time. Answer: {a}", "अप्रत्यक्ष कथनात सर्वनाम बोलणाऱ्याप्रमाणे बदलते आणि क्रियापद एक पाऊल मागच्या काळात जाते. उत्तर: {a}", "अप्रत्यक्ष कथन में सर्वनाम वक्ता के अनुसार बदलता है और क्रिया एक काल पीछे चली जाती है। उत्तर: {a}"),
        dis, difficulty=3,
    )

    fig = [("as brave as a lion", "simile"), ("The moon is a silver coin.", "metaphor"), ("The wind whispered through the trees.", "personification"),
           ("I have told you a million times.", "hyperbole"), ("Peter picked a peck of pickled peppers.", "alliteration"), ("The bees buzzed around the flowers.", "onomatopoeia"),
           ("as cold as ice", "simile"), ("Life is a journey.", "metaphor"), ("The sun smiled down on us.", "personification"), ("I am so hungry I could eat a horse.", "hyperbole"),
           ("She sells sea shells by the seashore.", "alliteration"), ("The clock went tick-tock.", "onomatopoeia")]
    fig_pool = ["simile", "metaphor", "personification", "hyperbole", "alliteration", "onomatopoeia"]
    ch_fig = b.chapter(
        cid(7, 3), L("Figures of Speech", "अलंकार", "अलंकार"),
        L("Simile, metaphor and friends.", "उपमा, रूपक आणि इतर.", "उपमा, रूपक और अन्य।"), "sparkle",
        L("Which figure of speech is this? \"{q}\"", "हा कोणता अलंकार आहे? \"{q}\"", "यह कौन-सा अलंकार है? \"{q}\""),
        L("Simile: uses as/like. Metaphor: A IS B. Personification: things act like people. Hyperbole: exaggeration. Onomatopoeia: sound words. This is {a}.",
          "Simile: as/like ने तुलना. Metaphor: एक गोष्ट दुसरी आहे. Personification: निर्जीवाला माणसाचे गुण. Hyperbole: अतिशयोक्ती. हा {a} आहे.",
          "Simile: as/like से तुलना. Metaphor: एक को दूसरा कहना. Personification: बेजान में इंसानी गुण. Hyperbole: बढ़ा-चढ़ाकर कहना. यह {a} है।"),
        [(q, a, [x for x in fig_pool if x != a]) for q, a in fig], difficulty=3,
    )

    rel = [("The boy ___ won the prize is my friend.", "who", ["whom", "whose", "which"]), ("The book ___ I read was exciting.", "which", ["who", "whose", "whom"]),
           ("This is the girl ___ bag was lost.", "whose", ["who", "whom", "which"]), ("___ it rained, we played outside.", "Although", ["Unless", "Until", "Because"]),
           ("You will fail ___ you study.", "unless", ["although", "while", "since"]), ("Wait here ___ I come back.", "until", ["although", "unless", "whereas"]),
           ("Ravi likes tea, ___ Raju likes coffee.", "whereas", ["unless", "until", "because"]), ("She was reading ___ I was cooking.", "while", ["unless", "whose", "whom"]),
           ("I have lived here ___ 2015.", "since", ["until", "unless", "although"]), ("He was ill; ___, he came to school.", "however", ["because", "unless", "whose"]),
           ("The teacher ___ I met was kind.", "whom", ["whose", "which", "whereas"]), ("Neither Ravi ___ Raju came.", "nor", ["or", "and", "but"])]
    ch_rel = b.chapter(
        cid(7, 4), L("Clauses and Connectors", "उपवाक्ये आणि जोडशब्द", "उपवाक्य और योजक"),
        L("who, which, whose, although, unless...", "who, which, whose, although, unless...", "who, which, whose, although, unless..."), "link",
        L("Fill in the blank: {q}", "रिकाम्या जागी योग्य शब्द निवडा: {q}", "खाली जगह भरिए: {q}"),
        L("Read the whole sentence and ask what the joining word must do. The right word is '{a}'.", "संपूर्ण वाक्य वाचा आणि जोडशब्दाचे काम काय ते पाहा. योग्य शब्द '{a}' आहे.", "पूरा वाक्य पढ़िए और सोचिए कि जोड़ने वाला शब्द क्या काम करेगा। सही शब्द '{a}' है।"),
        rel, difficulty=3,
    )

    conf = [("Good food will ___ your health.", "affect", "effect"), ("Exercise has a good ___ on the body.", "effect", "affect"), ("I ___ your invitation.", "accept", "except"),
            ("Everyone ___ Raju was there.", "except", "accept"), ("He gave me good ___.", "advice", "advise"), ("I ___ you to study daily.", "advise", "advice"),
            ("Don't ___ your keys.", "lose", "loose"), ("My tooth is ___.", "loose", "lose"), ("She is taller ___ her brother.", "than", "then"),
            ("First wash your hands, ___ eat.", "then", "than"), ("The ___ of our school is very strict.", "principal", "principle"), ("Is it sunny, or is it raining? I don't know ___.", "whether", "weather")]
    cpool = [w for _, a, bb in conf for w in (a, bb)]
    ch_conf = b.chapter(
        cid(7, 5), L("Confusing Words", "गोंधळात टाकणारे शब्द", "उलझाने वाले शब्द"),
        L("affect or effect? than or then?", "affect की effect? than की then?", "affect या effect? than या then?"), "question",
        L("Pick the right word: {q}", "योग्य शब्द निवडा: {q}", "सही शब्द चुनिए: {q}"),
        L("These words look or sound alike but mean different things. Here the right word is '{a}'.", "हे शब्द सारखे दिसतात किंवा ऐकू येतात पण अर्थ वेगळा असतो. येथे '{a}' योग्य आहे.", "ये शब्द एक जैसे दिखते या सुनाई देते हैं पर अर्थ अलग होता है। यहाँ '{a}' सही है।"),
        [(q, a, [twin] + b.rnd.sample([w for w in cpool if w not in (a, twin)], 2)) for q, a, twin in conf], difficulty=3,
    )
    out[7] = [ch_act, ch_dis, ch_fig, ch_rel, ch_conf]
    return out



def _fix_dict_hint(ch, ts, tenses):
    """The tense chapter's hint names the answer in each language: rebuild it with per-language terms."""
    by_q = {q: k for q, k in ts}
    for q in ch["questions"]:
        sent = q["prompt"]["en"].split("? ", 1)[1]
        ans = tenses[by_q[sent]]
        q["hint"] = L(f"Look at the time clue and the verb. This sentence is in the {ans['en']} tense.",
                      f"वेळ दाखवणारा शब्द आणि क्रियापद पाहा. हे वाक्य '{ans['mr']}' आहे.",
                      f"समय बताने वाला शब्द और क्रिया देखिए। यह वाक्य '{ans['hi']}' का है।")
    return ch
