import json, random

random.seed(42)

def lt(en, mr, hi):
    return {"en": en, "mr": mr, "hi": hi}

def mcq_options(correct, distractors):
    opts = [{"text": lt(str(correct), str(correct), str(correct)), "correct": True}]
    for d in distractors:
        opts.append({"text": lt(str(d), str(d), str(d)), "correct": False})
    random.shuffle(opts)
    return opts

def distinct_distractors(correct, count, spread):
    out = set()
    tries = 0
    while len(out) < count and tries < 100:
        tries += 1
        delta = random.choice([-1, 1]) * random.randint(1, spread)
        val = correct + delta
        if val != correct and val >= 0 and val not in out:
            out.add(val)
    while len(out) < count:
        out.add(correct + len(out) + 1)
    return list(out)[:count]

qid_counter = {}
def next_id(chapter_id):
    qid_counter[chapter_id] = qid_counter.get(chapter_id, 0) + 1
    return f"{chapter_id}_q{qid_counter[chapter_id]}"

def add_question(chapter, prompt, correct, distractors, hint=None, image=None, difficulty=3):
    qid = next_id(chapter["id"])
    q = {
        "id": qid,
        "prompt": prompt,
        "difficulty": difficulty,
        "options": mcq_options(correct, distractors)
    }
    if hint: q["hint"] = hint
    if image: q["imageRef"] = image
    chapter["questions"].append(q)

def new_chapter(cid, title, blurb, icon):
    return {"id": cid, "title": title, "blurb": blurb, "iconKey": icon, "questions": []}

# ---------- question template generators ----------

def gen_addition(chapter, n, lo, hi, spread):
    for _ in range(n):
        a, b = random.randint(lo, hi), random.randint(lo, hi)
        correct = a + b
        add_question(
            chapter,
            lt(f"What is {a} + {b}?", f"{a} + {b} किती?", f"{a} + {b} कितना होता है?"),
            correct, distinct_distractors(correct, 3, spread),
            hint=lt("Count them together.", "दोन्ही एकत्र मोज.", "दोनों को साथ गिनो।")
        )

def gen_subtraction(chapter, n, lo, hi, spread):
    for _ in range(n):
        a = random.randint(lo, hi)
        b = random.randint(lo, a)
        correct = a - b
        add_question(
            chapter,
            lt(f"What is {a} - {b}?", f"{a} - {b} किती उरतात?", f"{a} - {b} कितना बचता है?"),
            correct, distinct_distractors(correct, 3, spread),
            hint=lt("Take away from the total.", "एकूणमधून वजा कर.", "कुल में से घटाओ।")
        )

def gen_multiplication(chapter, n, table_lo, table_hi, mult_lo, mult_hi):
    for _ in range(n):
        a, b = random.randint(table_lo, table_hi), random.randint(mult_lo, mult_hi)
        correct = a * b
        add_question(
            chapter,
            lt(f"What is {a} × {b}?", f"{a} × {b} किती?", f"{a} × {b} कितना होता है?"),
            correct, distinct_distractors(correct, 3, max(3, correct // 5)),
            hint=lt(f"Add {a}, {b} times.", f"{a} ला {b} वेळा बेरीज कर.", f"{a} को {b} बार जोड़ो।")
        )

def gen_division(chapter, n, divisor_lo, divisor_hi, quotient_lo, quotient_hi):
    for _ in range(n):
        b = random.randint(divisor_lo, divisor_hi)
        q = random.randint(quotient_lo, quotient_hi)
        a = b * q
        add_question(
            chapter,
            lt(f"What is {a} ÷ {b}?", f"{a} ÷ {b} किती?", f"{a} ÷ {b} कितना होता है?"),
            q, distinct_distractors(q, 3, max(2, q // 3)),
            hint=lt("Split into equal groups.", "समान गटांत विभागा.", "बराबर समूहों में बाँटो।")
        )

def gen_place_value(chapter, n, digits):
    for _ in range(n):
        num = random.randint(10 ** (digits - 1), 10 ** digits - 1)
        place_names_en = ["ones", "tens", "hundreds", "thousands", "ten-thousands"]
        place_names_mr = ["एकक", "दशक", "शतक", "हजार", "दहा-हजार"]
        place_names_hi = ["इकाई", "दहाई", "सैकड़ा", "हज़ार", "दस-हज़ार"]
        pos = random.randint(0, digits - 1)
        digit = int(str(num)[digits - 1 - pos])
        add_question(
            chapter,
            lt(
                f"In {num}, what digit is in the {place_names_en[pos]} place?",
                f"{num} मध्ये {place_names_mr[pos]} स्थानी कोणता अंक आहे?",
                f"{num} में {place_names_hi[pos]} स्थान पर कौन-सा अंक है?"
            ),
            digit, distinct_distractors(digit, 3, 4),
            hint=lt("Count places from the right.", "उजवीकडून स्थान मोज.", "दाईं ओर से स्थान गिनो।")
        )

def gen_word_problem_fruit(chapter, n, lo, hi):
    fruits = [("mangoes", "आंबे", "आम"), ("apples", "सफरचंद", "सेब"), ("bananas", "केळी", "केले")]
    for _ in range(n):
        total = random.randint(lo, hi)
        given = random.randint(1, total - 1) if total > 1 else 1
        correct = total - given
        f_en, f_mr, f_hi = random.choice(fruits)
        add_question(
            chapter,
            lt(
                f"There are {total} {f_en} in a basket. {given} are given away. How many are left?",
                f"टोपलीत {total} {f_mr} आहेत. {given} दिले. किती उरले?",
                f"टोकरी में {total} {f_hi} हैं। {given} दे दिए। कितने बचे?"
            ),
            correct, distinct_distractors(correct, 3, max(2, total // 4)),
            hint=lt("Subtract what was given away.", "दिलेले वजा कर.", "दिए गए घटाओ।")
        )

def gen_comparison(chapter, n, lo, hi):
    for _ in range(n):
        a, b = random.randint(lo, hi), random.randint(lo, hi)
        while a == b:
            b = random.randint(lo, hi)
        bigger = max(a, b)
        add_question(
            chapter,
            lt(f"Which is greater: {a} or {b}?", f"कोणती संख्या मोठी: {a} की {b}?", f"कौन-सी संख्या बड़ी है: {a} या {b}?"),
            bigger, [min(a, b)],
            hint=lt("Compare digit by digit.", "अंक-अंक तुलना कर.", "अंक-दर-अंक तुलना करो।")
        )

def gen_fraction_basic(chapter, n):
    for _ in range(n):
        den = random.choice([2, 3, 4, 5, 6, 8, 10])
        num = random.randint(1, den - 1)
        # ask: what is num/den of a total that's a clean multiple
        total = den * random.randint(2, 6)
        correct = (total // den) * num
        add_question(
            chapter,
            lt(
                f"What is {num}/{den} of {total}?",
                f"{total} च्या {num}/{den} किती?",
                f"{total} का {num}/{den} कितना होता है?"
            ),
            correct, distinct_distractors(correct, 3, max(3, correct // 4)),
            hint=lt(f"First find 1/{den} of {total}.", f"आधी {total} चा 1/{den} शोध.", f"पहले {total} का 1/{den} निकालो।")
        )

def gen_percentage(chapter, n):
    for _ in range(n):
        pct = random.choice([10, 20, 25, 50, 75])
        total = random.choice([20, 40, 60, 80, 100, 200])
        correct = total * pct // 100
        add_question(
            chapter,
            lt(f"What is {pct}% of {total}?", f"{total} च्या {pct}% किती?", f"{total} का {pct}% कितना होता है?"),
            correct, distinct_distractors(correct, 3, max(3, correct // 3)),
            hint=lt("Percent means 'out of 100'.", "टक्केवारी म्हणजे '100 पैकी'.", "प्रतिशत का मतलब है '100 में से'।")
        )

def gen_simple_equation(chapter, n):
    for _ in range(n):
        x = random.randint(2, 20)
        b = random.randint(2, 15)
        c = x + b
        add_question(
            chapter,
            lt(f"If x + {b} = {c}, what is x?", f"जर x + {b} = {c}, तर x किती?", f"यदि x + {b} = {c}, तो x क्या है?"),
            x, distinct_distractors(x, 3, 5),
            hint=lt(f"Subtract {b} from {c}.", f"{c} मधून {b} वजा कर.", f"{c} में से {b} घटाओ।")
        )

def gen_perimeter_rect(chapter, n):
    for _ in range(n):
        l, w = random.randint(3, 20), random.randint(2, 15)
        correct = 2 * (l + w)
        add_question(
            chapter,
            lt(
                f"A rectangle is {l} cm long and {w} cm wide. What is its perimeter?",
                f"एका आयताची लांबी {l} सेमी आणि रुंदी {w} सेमी आहे. परिमिती किती?",
                f"एक आयत की लंबाई {l} सेमी और चौड़ाई {w} सेमी है। परिधि क्या है?"
            ),
            correct, distinct_distractors(correct, 3, max(4, correct // 5)),
            hint=lt("Perimeter = 2 × (length + width).", "परिमिती = 2 × (लांबी + रुंदी).", "परिधि = 2 × (लंबाई + चौड़ाई)।")
        )

def gen_area_rect(chapter, n):
    for _ in range(n):
        l, w = random.randint(3, 15), random.randint(2, 12)
        correct = l * w
        add_question(
            chapter,
            lt(
                f"A rectangle is {l} cm long and {w} cm wide. What is its area?",
                f"एका आयताची लांबी {l} सेमी आणि रुंदी {w} सेमी आहे. क्षेत्रफळ किती?",
                f"एक आयत की लंबाई {l} सेमी और चौड़ाई {w} सेमी है। क्षेत्रफल क्या है?"
            ),
            correct, distinct_distractors(correct, 3, max(4, correct // 5)),
            hint=lt("Area = length × width.", "क्षेत्रफळ = लांबी × रुंदी.", "क्षेत्रफल = लंबाई × चौड़ाई।")
        )

def gen_ratio(chapter, n):
    for _ in range(n):
        a, b = random.randint(2, 9), random.randint(2, 9)
        while a == b:
            b = random.randint(2, 9)
        multiplier = random.randint(2, 6)
        total_a = a * multiplier
        correct = total_a
        add_question(
            chapter,
            lt(
                f"The ratio of boys to girls is {a}:{b}. If there are {b * multiplier} girls, how many boys are there?",
                f"मुले व मुली यांचे गुणोत्तर {a}:{b} आहे. जर {b * multiplier} मुली असतील, तर मुले किती?",
                f"लड़कों और लड़कियों का अनुपात {a}:{b} है। यदि {b * multiplier} लड़कियाँ हैं, तो लड़के कितने हैं?"
            ),
            correct, distinct_distractors(correct, 3, max(3, correct // 4)),
            hint=lt("Find the multiplier from the girls' count first.", "आधी मुलींवरून गुणक शोध.", "पहले लड़कियों से गुणक निकालो।")
        )

def gen_integers(chapter, n):
    for _ in range(n):
        a = random.randint(-20, 20)
        b = random.randint(-20, 20)
        correct = a + b
        add_question(
            chapter,
            lt(f"What is ({a}) + ({b})?", f"({a}) + ({b}) किती?", f"({a}) + ({b}) कितना होता है?"),
            correct, distinct_distractors(correct, 3, 6),
            hint=lt("Watch the signs carefully.", "चिन्हांकडे लक्ष दे.", "चिन्हों पर ध्यान दो।")
        )

# ---------- build all classes ----------

classes = []

def build_class1():
    c = {"classLevel": 1, "chapters": []}
    ch1 = new_chapter("maths_c1_ch1", lt("Counting Trail", "मोजणीची वाट", "गिनती की राह"),
        lt("Count and compare small numbers.", "लहान संख्या मोज आणि तुलना कर.", "छोटी संख्याएँ गिनो और तुलना करो।"), "footprints")
    gen_addition(ch1, 5, 1, 15, 3)
    gen_comparison(ch1, 4, 1, 20)
    gen_word_problem_fruit(ch1, 3, 4, 12)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c1_ch2", lt("Adding Adventures", "बेरीज साहस", "जोड़ का साहस"),
        lt("Addition within 20.", "20 पर्यंत बेरीज.", "20 तक जोड़।"), "plus")
    gen_addition(ch2, 10, 1, 18, 4)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c1_ch3", lt("Taking Away", "वजाबाकी", "घटाना"),
        lt("Subtraction within 20.", "20 पर्यंत वजाबाकी.", "20 तक घटाना।"), "minus")
    gen_subtraction(ch3, 10, 1, 18, 4)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c1_ch4", lt("Number Homes", "संख्यांची घरे", "संख्याओं के घर"),
        lt("Ones and tens place value.", "एकक व दशक स्थान.", "इकाई और दहाई स्थान।"), "home")
    gen_place_value(ch4, 8, 2)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c1_ch5", lt("Basket Stories", "टोपलीच्या गोष्टी", "टोकरी की कहानियाँ"),
        lt("Simple story sums.", "सोप्या गोष्टीतील बेरीज-वजाबाकी.", "सरल कहानी वाले सवाल।"), "basket")
    gen_word_problem_fruit(ch5, 6, 6, 18)
    gen_addition(ch5, 5, 1, 15, 3)
    c["chapters"].append(ch5)
    return c

def build_class2():
    c = {"classLevel": 2, "chapters": []}
    ch1 = new_chapter("maths_c2_ch1", lt("Numbers to 100", "100 पर्यंत संख्या", "100 तक संख्याएँ"),
        lt("Place value and comparison.", "स्थानिक किंमत व तुलना.", "स्थानीय मान और तुलना।"), "hash")
    gen_place_value(ch1, 6, 2)
    gen_comparison(ch1, 6, 10, 99)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c2_ch2", lt("Two-Digit Addition", "दोन अंकी बेरीज", "दो अंकों का जोड़"),
        lt("Adding two-digit numbers.", "दोन अंकी संख्यांची बेरीज.", "दो अंकों की संख्याओं का जोड़।"), "plus")
    gen_addition(ch2, 11, 10, 60, 6)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c2_ch3", lt("Two-Digit Subtraction", "दोन अंकी वजाबाकी", "दो अंकों का घटाव"),
        lt("Subtracting two-digit numbers.", "दोन अंकी संख्यांची वजाबाकी.", "दो अंकों की संख्याओं का घटाव।"), "minus")
    gen_subtraction(ch3, 11, 15, 80, 6)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c2_ch4", lt("First Steps in Multiplying", "गुणाकाराची पहिली पावले", "गुणा के पहले कदम"),
        lt("Tables of 2, 3, 4, 5.", "2, 3, 4, 5 चे पाढे.", "2, 3, 4, 5 के पहाड़े।"), "x")
    gen_multiplication(ch4, 12, 2, 5, 1, 10)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c2_ch5", lt("Shape and Size", "आकार आणि माप", "आकार और माप"),
        lt("Perimeter of simple rectangles.", "साध्या आयताची परिमिती.", "सरल आयतों की परिधि।"), "ruler")
    gen_perimeter_rect(ch5, 10)
    c["chapters"].append(ch5)
    return c

def build_class3():
    c = {"classLevel": 3, "chapters": []}
    ch1 = new_chapter("maths_c3_ch1", lt("Numbers to 1000", "1000 पर्यंत संख्या", "1000 तक संख्याएँ"),
        lt("Place value up to hundreds.", "शतकापर्यंत स्थानिक किंमत.", "सैकड़ा तक स्थानीय मान।"), "hash")
    gen_place_value(ch1, 8, 3)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c3_ch2", lt("Multiplication Tables", "पाढे", "पहाड़े"),
        lt("Tables 2 to 10.", "2 ते 10 चे पाढे.", "2 से 10 तक पहाड़े।"), "x")
    gen_multiplication(ch2, 13, 2, 10, 2, 10)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c3_ch3", lt("Sharing Equally", "समान वाटणी", "समान बाँट"),
        lt("Simple division.", "सोपी भागाकार.", "सरल भाग।"), "divide")
    gen_division(ch3, 11, 2, 10, 2, 10)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c3_ch4", lt("Parts of a Whole", "संपूर्णाचे भाग", "पूर्ण के भाग"),
        lt("Introduction to fractions.", "अपूर्णांकांची ओळख.", "भिन्न का परिचय।"), "pie")
    gen_fraction_basic(ch4, 10)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c3_ch5", lt("Market Maths", "बाजारातील गणित", "बाज़ार का गणित"),
        lt("Word problems with money and objects.", "पैसे व वस्तूंच्या गोष्टीतील सवाल.", "पैसे और वस्तुओं के सवाल।"), "basket")
    gen_word_problem_fruit(ch5, 6, 20, 60)
    gen_subtraction(ch5, 5, 20, 90, 8)
    c["chapters"].append(ch5)
    return c

def build_class4():
    c = {"classLevel": 4, "chapters": []}
    ch1 = new_chapter("maths_c4_ch1", lt("Numbers to 10,000", "10,000 पर्यंत संख्या", "10,000 तक संख्याएँ"),
        lt("Place value up to thousands.", "हजारापर्यंत स्थानिक किंमत.", "हज़ार तक स्थानीय मान।"), "hash")
    gen_place_value(ch1, 8, 4)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c4_ch2", lt("Bigger Multiplication", "मोठा गुणाकार", "बड़ा गुणा"),
        lt("Multiplying larger numbers.", "मोठ्या संख्यांचा गुणाकार.", "बड़ी संख्याओं का गुणा।"), "x")
    gen_multiplication(ch2, 12, 6, 12, 4, 15)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c4_ch3", lt("Division with Leftovers", "उरलेल्यासह भागाकार", "शेष के साथ भाग"),
        lt("Division, including remainders.", "भागाकार व बाकी.", "भाग और शेषफल।"), "divide")
    gen_division(ch3, 12, 3, 12, 3, 15)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c4_ch4", lt("Fraction Friends", "अपूर्णांक मित्र", "भिन्न के दोस्त"),
        lt("Working with fractions.", "अपूर्णांकांसह काम.", "भिन्न के साथ काम।"), "pie")
    gen_fraction_basic(ch4, 11)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c4_ch5", lt("Around the Field", "मैदानाभोवती", "मैदान के चारों ओर"),
        lt("Perimeter and area basics.", "परिमिती व क्षेत्रफळाची ओळख.", "परिधि और क्षेत्रफल का परिचय।"), "ruler")
    gen_perimeter_rect(ch5, 6)
    gen_area_rect(ch5, 6)
    c["chapters"].append(ch5)
    return c

def build_class5():
    c = {"classLevel": 5, "chapters": []}
    ch1 = new_chapter("maths_c5_ch1", lt("Numbers to 1,00,000", "1,00,000 पर्यंत संख्या", "1,00,000 तक संख्याएँ"),
        lt("Place value with large numbers.", "मोठ्या संख्यांची स्थानिक किंमत.", "बड़ी संख्याओं का स्थानीय मान।"), "hash")
    gen_place_value(ch1, 8, 5)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c5_ch2", lt("Fraction Power", "अपूर्णांकाची ताकद", "भिन्न की शक्ति"),
        lt("Fractions of quantities.", "प्रमाणांचे अपूर्णांक.", "मात्राओं के भिन्न।"), "pie")
    gen_fraction_basic(ch2, 13)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c5_ch3", lt("Percent Basics", "टक्केवारीची ओळख", "प्रतिशत का परिचय"),
        lt("Introduction to percentages.", "टक्केवारीची सुरुवात.", "प्रतिशत की शुरुआत।"), "percent")
    gen_percentage(ch3, 12)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c5_ch4", lt("Field and Fence", "शेत आणि कुंपण", "खेत और बाड़"),
        lt("Perimeter and area of rectangles.", "आयताची परिमिती व क्षेत्रफळ.", "आयत की परिधि और क्षेत्रफल।"), "ruler")
    gen_perimeter_rect(ch4, 6)
    gen_area_rect(ch4, 6)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c5_ch5", lt("Money Matters", "पैशांचा हिशोब", "पैसों का हिसाब"),
        lt("Word problems with money.", "पैशांच्या गोष्टीतील सवाल.", "पैसों की कहानी वाले सवाल।"), "coin")
    gen_word_problem_fruit(ch5, 5, 40, 120)
    gen_multiplication(ch5, 6, 6, 12, 3, 20)
    c["chapters"].append(ch5)
    return c

def build_class6():
    c = {"classLevel": 6, "chapters": []}
    ch1 = new_chapter("maths_c6_ch1", lt("Meet the Integers", "पूर्णांकांची ओळख", "पूर्णांकों का परिचय"),
        lt("Positive and negative numbers.", "धन व ऋण संख्या.", "धनात्मक और ऋणात्मक संख्याएँ।"), "plusminus")
    gen_integers(ch1, 12)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c6_ch2", lt("Fractions & Decimals", "अपूर्णांक व दशांश", "भिन्न और दशमलव"),
        lt("Working across both forms.", "दोन्ही रूपांत काम.", "दोनों रूपों में काम।"), "pie")
    gen_fraction_basic(ch2, 11)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c6_ch3", lt("Ratio Race", "गुणोत्तराची शर्यत", "अनुपात की दौड़"),
        lt("Ratio and proportion.", "गुणोत्तर व प्रमाण.", "अनुपात और समानुपात।"), "scale")
    gen_ratio(ch3, 11)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c6_ch4", lt("Letter Numbers", "अक्षर संख्या", "अक्षर संख्याएँ"),
        lt("Simple algebraic equations.", "साधी बीजगणितीय समीकरणे.", "सरल बीजगणितीय समीकरण।"), "x-variable")
    gen_simple_equation(ch4, 12)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c6_ch5", lt("Percent in Action", "कृतीतील टक्केवारी", "क्रिया में प्रतिशत"),
        lt("Applying percentages.", "टक्केवारीचा वापर.", "प्रतिशत का उपयोग।"), "percent")
    gen_percentage(ch5, 12)
    c["chapters"].append(ch5)
    return c

def build_class7():
    c = {"classLevel": 7, "chapters": []}
    ch1 = new_chapter("maths_c7_ch1", lt("Rational Numbers", "परिमेय संख्या", "परिमेय संख्याएँ"),
        lt("Working with rational numbers.", "परिमेय संख्यांसह काम.", "परिमेय संख्याओं के साथ काम।"), "plusminus")
    gen_integers(ch1, 12)
    c["chapters"].append(ch1)

    ch2 = new_chapter("maths_c7_ch2", lt("Profit, Loss & Percent", "नफा, तोटा व टक्केवारी", "लाभ, हानि और प्रतिशत"),
        lt("Real-life percentage problems.", "दैनंदिन जीवनातील टक्केवारी सवाल.", "रोज़मर्रा के प्रतिशत सवाल।"), "percent")
    gen_percentage(ch2, 13)
    c["chapters"].append(ch2)

    ch3 = new_chapter("maths_c7_ch3", lt("Solving for x", "x शोधणे", "x को हल करना"),
        lt("Linear equations.", "रेषीय समीकरणे.", "रैखिक समीकरण।"), "x-variable")
    gen_simple_equation(ch3, 13)
    c["chapters"].append(ch3)

    ch4 = new_chapter("maths_c7_ch4", lt("Area All Around", "सर्वत्र क्षेत्रफळ", "चारों ओर क्षेत्रफल"),
        lt("Perimeter and area problems.", "परिमिती व क्षेत्रफळाचे सवाल.", "परिधि और क्षेत्रफल के सवाल।"), "ruler")
    gen_perimeter_rect(ch4, 6)
    gen_area_rect(ch4, 7)
    c["chapters"].append(ch4)

    ch5 = new_chapter("maths_c7_ch5", lt("Ratio in Real Life", "दैनंदिन जीवनातील गुणोत्तर", "रोज़मर्रा में अनुपात"),
        lt("Applying ratio and proportion.", "गुणोत्तर व प्रमाणाचा वापर.", "अनुपात और समानुपात का उपयोग।"), "scale")
    gen_ratio(ch5, 12)
    c["chapters"].append(ch5)
    return c

classes.append(build_class1())
classes.append(build_class2())
classes.append(build_class3())
classes.append(build_class4())
classes.append(build_class5())
classes.append(build_class6())
classes.append(build_class7())

seed = {
    "version": 1,
    "subject": {
        "code": "maths",
        "name": {"en": "Maths", "mr": "गणित", "hi": "गणित"},
        "iconKey": "abacus",
        "colorHex": "#FF7A45",
        "orderIndex": 0
    },
    "classes": classes
}

total_q = sum(len(ch["questions"]) for c in classes for ch in c["chapters"])
total_ch = sum(len(c["chapters"]) for c in classes)
print(f"Chapters: {total_ch}, Questions: {total_q}")

with open("/home/claude/repo/app/src/main/assets/seed/maths.json", "w", encoding="utf-8") as f:
    json.dump(seed, f, ensure_ascii=False, indent=2)
print("written")
