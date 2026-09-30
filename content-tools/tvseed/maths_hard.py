"""
Maths for Class 6 and Class 7 (TV edition): medium-to-hard, multi-step questions.

The phone bank's Class 6-7 Maths was single-step ("x + 10 = 13", "25% of 100"). These chapters use negative
numbers, unlike fractions, decimals, three-part ratios, two-step and both-sides equations, percent change and
discount, rational numbers, interest, circles, Pythagoras and powers. Numbers are random (seeded) but every
answer is computed exactly (fractions.Fraction), never with floats.
"""
from fractions import Fraction
from math import gcd

from common import L


# ---- number formatting -------------------------------------------------------------------------
def cx(c):
    """Coefficient text: 1 -> 'x', 3 -> '3x'."""
    return "x" if c == 1 else f"{c}x"


def neg(n):
    return f"({n})" if n < 0 else f"{n}"


def dec(x):
    """Decimal text for a Fraction. Exact when the decimal terminates (up to 4 places); wrong-answer
    candidates that do not terminate are simply rounded to 3 places."""
    x = Fraction(x)
    if x.denominator == 1:
        return str(x.numerator)
    s = f"{float(x):.4f}".rstrip("0").rstrip(".")
    if Fraction(s) == x:
        return s
    return f"{float(x):.3f}".rstrip("0").rstrip(".")


def frac(x):
    x = Fraction(x)
    return str(x.numerator) if x.denominator == 1 else f"{x.numerator}/{x.denominator}"


def mixed(x):
    x = Fraction(x)
    if x.denominator == 1:
        return str(x.numerator)
    w, r = divmod(x.numerator, x.denominator)
    return f"{w} {r}/{x.denominator}" if w else f"{r}/{x.denominator}"


def near(correct, rnd, extras=(), spread=3, lo=None, hi=None, fmt=str):
    """Three wrong numbers: the typical mistakes first, then close neighbours."""
    out = []
    for e in extras:
        if e != correct and e not in out and (lo is None or e >= lo) and (hi is None or e <= hi):
            out.append(e)
    k = 1
    while len(out) < 6 and k < 60:
        for d in (k, -k):
            v = correct + d * spread if isinstance(correct, int) else correct + d * Fraction(spread, 1)
            if v != correct and v not in out and (lo is None or v >= lo) and (hi is None or v <= hi):
                out.append(v)
        k += 1
    rnd.shuffle(out)
    return [fmt(v) for v in out[:3]]


def build_chapter(b, cid, title, blurb, icon, gens, count=12, difficulty=3):
    ch = {"id": cid, "title": title, "blurb": blurb, "iconKey": icon, "questions": []}
    seen = set()
    tries = 0
    while len(ch["questions"]) < count and tries < 800:
        tries += 1
        g = gens[len(ch["questions"]) % len(gens)]
        prompt, correct, wrongs, hint = g(b.rnd)
        key = prompt["en"]
        wr = []
        for w in wrongs:
            if w != correct and w not in wr:
                wr.append(w)
        if key in seen or len(wr) < 3:
            continue
        seen.add(key)
        opts = [{"text": {"en": correct, "mr": correct, "hi": correct}, "correct": True}] + [{"text": {"en": w, "mr": w, "hi": w}, "correct": False} for w in wr[:3]]
        b.rnd.shuffle(opts)
        ch["questions"].append({"id": b.qid(cid), "prompt": prompt, "difficulty": difficulty, "hint": hint, "options": opts})
    assert len(ch["questions"]) == count, (cid, len(ch["questions"]))
    return ch


H_SIGNS = L("Same signs give +, different signs give −. Work in small steps.", "समान चिन्हांचे उत्तर +, वेगळ्या चिन्हांचे −. लहान पावलांनी सोडवा.", "समान चिह्न का उत्तर +, अलग चिह्न का −। छोटे-छोटे चरणों में हल कीजिए।")


# =================================================================================== CLASS 6
def chapters(b):
    out = {}
    cid = lambda lvl, n: f"maths_c{lvl}_ch{n}"

    # ---------------------------------------------------------------- 6.1 integers
    def int_addsub(r):
        a, c = r.randint(-25, 25), r.randint(-25, 25)
        if a == 0 or c == 0:
            a, c = -13, 8
        if a > 0 and c > 0:
            a = -a
        plus = r.random() < 0.5
        e = f"{neg(a)} {'+' if plus else '-'} {neg(c)}"
        ans = a + c if plus else a - c
        return (L(f"What is {e}?", f"{e} ची किंमत किती?", f"{e} का मान क्या है?"), str(ans),
                [str(x) for x in near(ans, r, [-ans, a + c if not plus else a - c, abs(ans) + 2], 2)], H_SIGNS)

    def int_muldiv(r):
        a, c = r.randint(2, 12), r.randint(2, 12)
        sa, sc = r.choice([1, -1]), r.choice([1, -1])
        if r.random() < 0.5:
            x, y = sa * a, sc * c
            e, ans = f"{neg(x)} × {neg(y)}", x * y
        else:
            y = sc * c
            x = sa * a * c
            e, ans = f"{neg(x)} ÷ {neg(y)}", x // y
            assert x % y == 0
        return (L(f"What is {e}?", f"{e} ची किंमत किती?", f"{e} का मान क्या है?"), str(ans),
                [str(v) for v in near(ans, r, [-ans, abs(ans), abs(ans) + c], 2)], H_SIGNS)

    def int_three(r):
        a, c, d = r.randint(-15, 15) or 6, r.randint(-15, 15) or -4, r.randint(-15, 15) or 9
        e = f"{neg(a)} - {neg(c)} + {neg(d)}"
        ans = a - c + d
        return (L(f"Find the value: {e}", f"किंमत काढा: {e}", f"मान ज्ञात कीजिए: {e}"), str(ans),
                [str(v) for v in near(ans, r, [a + c + d, a - c - d, -ans], 2)], H_SIGNS)

    def int_temp(r):
        t = r.randint(3, 14)
        d = r.randint(t + 3, t + 20)
        ans = t - d
        return (L(f"At noon the temperature was {t}°C. By midnight it fell by {d}°C. What was the temperature at midnight?",
                  f"दुपारी तापमान {t}°C होते. मध्यरात्रीपर्यंत ते {d}°C ने कमी झाले. मध्यरात्री तापमान किती होते?",
                  f"दोपहर में तापमान {t}°C था। आधी रात तक यह {d}°C गिर गया। आधी रात को तापमान कितना था?"), f"{ans}°C",
                [f"{v}°C" for v in near(ans, r, [d - t, t + d, -(t + d)], 2)],
                L("Start at the noon value and subtract the fall. Going below zero gives a negative number.", "दुपारच्या तापमानातून घट वजा करा. शून्याखाली गेल्यास संख्या ऋण येते.", "दोपहर के तापमान में से गिरावट घटाइए। शून्य से नीचे जाने पर संख्या ऋणात्मक आती है।"))

    def int_bracket(r):
        a, c, d = r.randint(2, 9) * r.choice([1, -1]), r.randint(1, 12), r.randint(1, 12)
        e = f"{neg(a)} × ({c} - {d})"
        ans = a * (c - d)
        return (L(f"Evaluate: {e}", f"किंमत काढा: {e}", f"मान निकालिए: {e}"), str(ans),
                [str(v) for v in near(ans, r, [-ans, a * c - d, a * c + a * d], 3)],
                L("Do the bracket first, then multiply.", "आधी कंस सोडवा, मग गुणा करा.", "पहले कोष्ठक हल कीजिए, फिर गुणा कीजिए।"))

    out[6] = [build_chapter(b, cid(6, 1), L("Integer Workout", "पूर्णांकांचा सराव", "पूर्णांक अभ्यास"), L("Negatives in every shape.", "ऋण संख्यांचे सर्व प्रकार.", "ऋणात्मक संख्याओं के सब रूप।"), "thermometer",
                           [int_addsub, int_muldiv, int_three, int_temp, int_bracket])]

    # ---------------------------------------------------------------- 6.2 fractions & decimals
    dens = [2, 3, 4, 5, 6, 8, 10, 12]

    def fr_addsub(r):
        while True:
            d1, d2 = r.sample(dens, 2)
            n1, n2 = r.randint(1, d1 - 1), r.randint(1, d2 - 1)
            f1, f2 = Fraction(n1, d1), Fraction(n2, d2)
            if f1.denominator != f2.denominator:
                break
        plus = r.random() < 0.6 or f1 == f2
        if not plus and f1 < f2:
            f1, f2 = f2, f1
        ans = f1 + f2 if plus else f1 - f2
        if ans == 0:
            plus, ans = True, f1 + f2
        e = f"{frac(f1)} {'+' if plus else '-'} {frac(f2)}"
        wr = [Fraction(f1.numerator + f2.numerator, f1.denominator + f2.denominator) if plus else Fraction(abs(f1.numerator - f2.numerator), abs(f1.denominator - f2.denominator) or 1),
              ans + Fraction(1, f1.denominator * f2.denominator), ans - Fraction(1, f1.denominator * f2.denominator) if ans > Fraction(1, f1.denominator * f2.denominator) else ans + Fraction(2, f1.denominator * f2.denominator),
              f1 * f2]
        wr = [frac(w) for w in wr if w != ans and w > 0]
        return (L(f"{e} = ? (answer in simplest form)", f"{e} = ? (उत्तर सर्वांत सोप्या रूपात)", f"{e} = ? (उत्तर सरलतम रूप में)"), frac(ans), wr,
                L("Make the bottoms equal (use the LCM), add or subtract the tops, then simplify.", "छेद समान करा (लसावि), अंश बेरीज/वजाबाकी करा, मग सोपे रूप द्या.", "हर समान कीजिए (ल.स.), अंश जोड़िए/घटाइए, फिर सरल कीजिए।"))

    def fr_mixed(r):
        w1, w2 = r.randint(1, 4), r.randint(1, 3)
        d1, d2 = r.sample([2, 3, 4, 5, 6], 2)
        f1 = w1 + Fraction(r.randint(1, d1 - 1), d1)
        f2 = w2 + Fraction(r.randint(1, d2 - 1), d2)
        ans = f1 + f2
        e = f"{mixed(f1)} + {mixed(f2)}"
        wr = [mixed(ans + Fraction(1, 2)), mixed(f1 * f2 if f1 * f2 != ans else ans + 1), mixed(ans - Fraction(1, d1 * d2)), mixed(ans + Fraction(1, d1 * d2))]
        return (L(f"What is {e}?", f"{e} = ?", f"{e} = ?"), mixed(ans), wr,
                L("Add the whole numbers, then add the fractions with a common bottom number.", "पूर्ण संख्या आणि अपूर्णांक वेगवेगळे बेरीज करा; छेद समान करा.", "पूर्ण संख्याएँ और भिन्न अलग-अलग जोड़िए; हर समान कीजिए।"))

    def dec_mul(r):
        a = Fraction(r.randint(12, 89), 10)
        c = Fraction(r.choice([2, 3, 4, 5, 6, 8, 15, 25]), 10)
        ans = a * c
        return (L(f"{dec(a)} × {dec(c)} = ?", f"{dec(a)} × {dec(c)} = ?", f"{dec(a)} × {dec(c)} = ?"), dec(ans),
                [dec(ans * 10), dec(ans / 10), dec(ans + Fraction(1, 10)) if ans + Fraction(1, 10) != ans else dec(ans + 1), dec(a + c)],
                L("Multiply as whole numbers, then count the decimal places in both numbers.", "पूर्ण संख्यांप्रमाणे गुणा करा, मग दोन्ही संख्यांतील दशांश स्थाने मोजा.", "पूर्ण संख्याओं की तरह गुणा कीजिए, फिर दोनों में दशमलव स्थान गिनिए।"))

    def dec_div(r):
        d = Fraction(r.choice([5, 25, 15, 4, 6, 12, 20]), 10)
        q = r.randint(3, 15)
        a = d * q
        return (L(f"{dec(a)} ÷ {dec(d)} = ?", f"{dec(a)} ÷ {dec(d)} = ?", f"{dec(a)} ÷ {dec(d)} = ?"), str(q),
                [str(v) for v in near(q, r, [q * 10, max(1, q // 10), q + 1, q - 1], 2, lo=1)],
                L("Multiply both numbers by 10 (or 100) to remove the decimal point, then divide.", "दोन्ही संख्यांना 10 (किंवा 100) ने गुणून दशांश काढा, मग भागा.", "दोनों संख्याओं को 10 (या 100) से गुणा करके दशमलव हटाइए, फिर भाग दीजिए।"))

    def fr_to_dec(r):
        d = r.choice([4, 5, 8, 16, 20, 25])
        n = r.randint(1, d - 1)
        while gcd(n, d) != 1:
            n = r.randint(1, d - 1)
        ans = Fraction(n, d)
        return (L(f"Write {n}/{d} as a decimal.", f"{n}/{d} हा अपूर्णांक दशांशात लिहा.", f"{n}/{d} को दशमलव में लिखिए।"), dec(ans),
                [dec(Fraction(d, n * 10)) if d % (n * 10) == 0 else dec(Fraction(n, d * 10)), dec(ans * 10) if ans * 10 < 10 else dec(ans / 10), dec(Fraction(n + 1, d)) if Fraction(n + 1, d) != ans else dec(Fraction(n, d) + Fraction(1, 100)),
                 dec(Fraction(n, d) + Fraction(1, 10))],
                L("Divide the top number by the bottom number.", "अंशाला छेदाने भागा.", "अंश को हर से भाग दीजिए।"))

    def fr_of_qty(r):
        q = r.choice([60, 72, 84, 96, 120, 144, 180])
        a, c = Fraction(r.choice([1, 2, 3, 5]), 6), Fraction(r.choice([1, 3]), 4)
        if a == Fraction(1, 6):
            a = Fraction(5, 6)
        ans = a * q - c * q
        if ans <= 0:
            a = Fraction(5, 6)
            ans = a * q - c * q
        return (L(f"What is {frac(a)} of {q} minus {frac(c)} of {q}?", f"{q} चा {frac(a)} वजा {q} चा {frac(c)} किती?", f"{q} के {frac(a)} में से {q} का {frac(c)} घटाने पर कितना मिलेगा?"), dec(ans),
                [dec(v) for v in near(ans, r, [a * q + c * q, (a - c) * q * 2 if (a - c) * q * 2 != ans else ans + 10], 6, lo=0)],
                L("Find each part of the amount first, then subtract.", "आधी प्रत्येक भाग काढा, मग वजा करा.", "पहले हर भाग निकालिए, फिर घटाइए।"))

    out[6].append(build_chapter(b, cid(6, 2), L("Fractions and Decimals Challenge", "अपूर्णांक व दशांश आव्हान", "भिन्न और दशमलव चुनौती"), L("Unlike fractions, mixed numbers, decimals.", "असमान छेद, मिश्र संख्या, दशांश.", "असमान हर, मिश्र संख्या, दशमलव।"), "target",
                                [fr_addsub, dec_mul, fr_mixed, dec_div, fr_to_dec, fr_of_qty]))

    # ---------------------------------------------------------------- 6.3 ratio & proportion
    def ratio3(r):
        a, c, d = r.sample(range(1, 8), 3)
        k = r.randint(4, 15)
        tot = (a + c + d) * k
        ans = d * k
        return (L(f"₹{tot} is shared in the ratio {a}:{c}:{d}. How much is the third share?", f"₹{tot} हे {a}:{c}:{d} या गुणोत्तरात वाटले. तिसरा वाटा किती?", f"₹{tot} को {a}:{c}:{d} के अनुपात में बाँटा गया। तीसरा हिस्सा कितना है?"), f"₹{ans}",
                [f"₹{v}" for v in near(ans, r, [c * k, a * k, tot // 3, tot // d], 4, lo=1)],
                L("Add the parts, divide the total by that to get one part, then multiply.", "भाग जोडा, एकूण भागून एक भाग काढा, मग गुणा करा.", "भाग जोड़िए, कुल को भाग देकर एक भाग निकालिए, फिर गुणा कीजिए।"))

    def unitary(r):
        n = r.randint(3, 9)
        price = r.randint(4, 15)
        m = r.choice([x for x in range(8, 25) if x != n])
        tot = n * price
        ans = m * price
        return (L(f"{n} notebooks cost ₹{tot}. How much do {m} notebooks cost?", f"{n} वह्यांची किंमत ₹{tot} आहे. {m} वह्यांची किंमत किती?", f"{n} कॉपियों की कीमत ₹{tot} है। {m} कॉपियों की कीमत कितनी होगी?"), f"₹{ans}",
                [f"₹{v}" for v in near(ans, r, [tot + m, tot * m, price * (m - n)], 5, lo=1)],
                L("Find the price of one, then multiply.", "एकाची किंमत काढा, मग गुणा करा.", "एक की कीमत निकालिए, फिर गुणा कीजिए।"))

    def prop(r):
        a, c = r.sample(range(2, 10), 2)
        k = r.randint(2, 7)
        x = c * k
        d = a * k
        return (L(f"If {a} : {c} = {d} : x, what is x?", f"{a} : {c} = {d} : x असेल तर x किती?", f"यदि {a} : {c} = {d} : x है तो x कितना है?"), str(x),
                [str(v) for v in near(x, r, [c + d - a, a * d // c if a * d % c == 0 else x + 2, d * c // a if (d * c // a) != x else x + 3], 2, lo=1)],
                L("In a proportion, a × x = b × c. Cross-multiply.", "प्रमाणात आडवा गुणाकार समान असतो. आडवे गुणा करा.", "समानुपात में तिरछे गुणनफल बराबर होते हैं। तिरछा गुणा कीजिए।"))

    def age_ratio(r):
        a, c = r.sample(range(2, 9), 2)
        k = r.randint(3, 9)
        return (L(f"The ages of Ravi and Anu are in the ratio {a}:{c}. Ravi is {a * k} years old. What is the sum of their ages?", f"रवी आणि अनूच्या वयांचे गुणोत्तर {a}:{c} आहे. रवी {a * k} वर्षांचा आहे. दोघांच्या वयांची बेरीज किती?", f"रवि और अनु की आयु का अनुपात {a}:{c} है। रवि {a * k} वर्ष का है। दोनों की आयु का योग कितना है?"), f"{k * (a + c)} years",
                [f"{v} years" for v in near(k * (a + c), r, [c * k, a * k + c, (a + c) * 2], 3, lo=1)],
                L("Find one part (Ravi's age ÷ his ratio number), then multiply the total parts.", "एक भाग काढा (रवीचे वय ÷ त्याचा भाग), मग एकूण भागांनी गुणा करा.", "एक भाग निकालिए (रवि की आयु ÷ उसका भाग), फिर कुल भागों से गुणा कीजिए।"))

    def speed_time(r):
        sp = r.choice([40, 45, 50, 60, 75])
        t = r.randint(2, 5)
        d2 = sp * r.randint(t + 2, 10)
        ans = d2 // sp
        return (L(f"A car covers {sp * t} km in {t} hours. At the same speed, how many hours will it take to cover {d2} km?", f"एक गाडी {t} तासांत {sp * t} किमी जाते. तेवढ्याच वेगाने {d2} किमी जाण्यास किती तास लागतील?", f"एक गाड़ी {t} घंटे में {sp * t} किमी चलती है। उसी चाल से {d2} किमी चलने में कितने घंटे लगेंगे?"), f"{ans} hours",
                [f"{v} hours" for v in near(ans, r, [d2 // t // 2 if d2 // t // 2 != ans else ans + 2, t + ans], 1, lo=2)],
                L("Find the speed first (distance ÷ time), then time = distance ÷ speed.", "आधी वेग काढा (अंतर ÷ वेळ), मग वेळ = अंतर ÷ वेग.", "पहले चाल निकालिए (दूरी ÷ समय), फिर समय = दूरी ÷ चाल।"))

    def map_scale(r):
        s = r.choice([2, 4, 5, 8, 10, 20])
        cm = Fraction(r.randint(3, 40), 2)
        ans = cm * s
        return (L(f"On a map, 1 cm shows {s} km. How many km does {dec(cm)} cm show?", f"नकाशावर 1 सेमी म्हणजे {s} किमी. {dec(cm)} सेमी म्हणजे किती किमी?", f"नक्शे पर 1 सेमी = {s} किमी है। {dec(cm)} सेमी कितने किमी दर्शाएगा?"), f"{dec(ans)} km",
                [f"{dec(v)} km" for v in near(ans, r, [cm + s, cm / s if cm / s != ans else ans + 1, ans * 2], 2, lo=Fraction(1, 2))],
                L("Multiply the map length by the km for 1 cm.", "नकाशावरील लांबीला 1 सेमीच्या किमीने गुणा.", "नक्शे की लंबाई को 1 सेमी के किमी से गुणा कीजिए।"))

    out[6].append(build_chapter(b, cid(6, 3), L("Ratio and Proportion Race", "गुणोत्तर आणि प्रमाण", "अनुपात और समानुपात"), L("Three-part ratios, speed and maps.", "तीन भागांचे गुणोत्तर, वेग, नकाशे.", "तीन भाग के अनुपात, चाल, नक्शे।"), "race",
                                [ratio3, unitary, prop, age_ratio, speed_time, map_scale]))

    # ---------------------------------------------------------------- 6.4 algebra
    def eq2(r):
        a, x, c = r.randint(2, 9), r.randint(2, 14), r.randint(2, 15)
        sign = r.choice([1, -1])
        rhs = a * x + sign * c
        e = f"{a}x {'+' if sign > 0 else '-'} {c} = {rhs}"
        return (L(f"Solve: {e}. What is x?", f"सोडवा: {e}. x ची किंमत किती?", f"हल कीजिए: {e}. x का मान क्या है?"), str(x),
                [str(v) for v in near(x, r, [rhs - c * sign, (rhs + c * sign) // a if a else x + 1, rhs // a if rhs // a != x else x + 2, a * x], 2, lo=0)],
                L("Undo the adding or subtracting first, then divide by the number in front of x.", "आधी बेरीज/वजाबाकी उलट करा, मग x पुढील संख्येने भागा.", "पहले जोड़/घटाव को उलटिए, फिर x के आगे की संख्या से भाग दीजिए।"))

    def eq_both(r):
        x = r.randint(2, 12)
        p, q = r.randint(4, 9), r.randint(1, 3)
        s = p - q
        c = r.randint(1, 10)
        k = r.randint(1, 12)
        lhs_c, rhs_c = k + q * x - 0, 0
        # p x + c = q x + (c + (p-q) x)   ->  choose constants so x is the solution
        c1 = r.randint(1, 15)
        c2 = c1 + s * x
        e = f"{p}x + {c1} = {cx(q)} + {c2}"
        return (L(f"Solve: {e}", f"सोडवा: {e}", f"हल कीजिए: {e}"), str(x),
                [str(v) for v in near(x, r, [(c2 + c1) // s if (c2 + c1) % s == 0 and (c2 + c1) // s != x else x + 2, c2 - c1, x + 1], 2, lo=0)],
                L("Collect the x terms on one side and the numbers on the other.", "x असलेली पदे एका बाजूला आणि संख्या दुसऱ्या बाजूला न्या.", "x वाले पद एक ओर और संख्याएँ दूसरी ओर ले जाइए।"))

    def evalx(r):
        a_, b_, m, n = r.randint(2, 5), r.randint(2, 6), r.randint(2, 5), r.randint(2, 6)
        ans = m * a_ * a_ + n * b_
        return (L(f"If a = {a_} and b = {b_}, what is {m}a² + {n}b?", f"a = {a_} आणि b = {b_} असेल तर {m}a² + {n}b ची किंमत किती?", f"यदि a = {a_} और b = {b_} है तो {m}a² + {n}b का मान क्या है?"), str(ans),
                [str(v) for v in near(ans, r, [(m * a_) ** 2 + n * b_, m * 2 * a_ + n * b_, m * a_ + n * b_], 3, lo=0)],
                L("Put the numbers in. Do the power (a²) first, then multiply, then add.", "संख्या घाला. आधी घात (a²), मग गुणा, मग बेरीज.", "संख्याएँ रखिए। पहले घात (a²), फिर गुणा, फिर जोड़।"))

    def simplify(r):
        a, c = r.randint(3, 9), r.randint(2, 8)
        d = r.randint(1, min(a + c - 2, 8))
        res = a + c - d
        e = f"{a}x + {c}x - {cx(d)}"
        return (L(f"Simplify: {e}", f"सोपे करा: {e}", f"सरल कीजिए: {e}"), cx(res),
                [cx(a + c + d), f"{res}x²", f"{res}", cx(res + 1)],
                L("Like terms (all with x) can be added and subtracted: add and subtract the numbers in front.", "सारखी पदे (सर्व x असलेली) बेरीज-वजाबाकी करता येतात: पुढील संख्यांची बेरीज-वजाबाकी करा.", "समान पदों (सभी x वाले) को जोड़ा-घटाया जा सकता है: आगे की संख्याएँ जोड़िए-घटाइए।"))

    def word_eq(r):
        x, m, n = r.randint(3, 15), r.randint(2, 7), r.randint(3, 20)
        tot = m * x + n
        return (L(f"A number is multiplied by {m} and then increased by {n}. The result is {tot}. What is the number?", f"एका संख्येला {m} ने गुणले आणि त्यात {n} मिळवले. उत्तर {tot} आले. ती संख्या कोणती?", f"एक संख्या को {m} से गुणा करके उसमें {n} जोड़ा गया। उत्तर {tot} आया। वह संख्या कौन-सी है?"), str(x),
                [str(v) for v in near(x, r, [(tot + n) // m if (tot + n) % m == 0 else x + 2, tot // m if tot // m != x else x + 3, tot - n], 2, lo=1)],
                L("Write it as an equation: m × number + n = result, then undo each step.", "समीकरण लिहा: गुणक × संख्या + n = उत्तर; मग प्रत्येक पायरी उलट करा.", "समीकरण बनाइए: गुणक × संख्या + n = उत्तर; फिर हर चरण उलटिए।"))

    def bracket(r):
        k, m, j = r.randint(3, 8), r.randint(1, 9), r.randint(1, 2)
        c = k - j
        n = k * m
        ans = f"{'x' if c == 1 else str(c) + 'x'} + {n}"
        wr = [f"{'x' if c == 1 else str(c) + 'x'} + {m}", f"{k + j}x + {n}", f"{c}x + {n + k}" if c != 1 else f"2x + {n}"]
        return (L(f"Simplify: {k}(x + {m}) - {cx(j)}", f"सोपे करा: {k}(x + {m}) - {cx(j)}", f"सरल कीजिए: {k}(x + {m}) - {cx(j)}"), ans, wr,
                L("Multiply the bracket first ({0}x + {1}), then combine the x terms.".format(k, n), "आधी कंस सोडवा, मग x ची पदे एकत्र करा.", "पहले कोष्ठक खोलिए, फिर x वाले पद जोड़िए-घटाइए।"))

    out[6].append(build_chapter(b, cid(6, 4), L("Algebra Gym", "बीजगणित कसरत", "बीजगणित कसरत"), L("Two-step equations and simplifying.", "दोन पायऱ्यांची समीकरणे आणि सोपे रूप.", "दो चरण के समीकरण और सरलीकरण।"), "dumbbell",
                                [eq2, eq_both, evalx, simplify, word_eq, bracket]))

    # ---------------------------------------------------------------- 6.5 percent & data
    def discount(r):
        p = r.choice([200, 240, 300, 360, 400, 500, 600, 800, 1200])
        d = r.choice([5, 10, 15, 20, 25, 30, 40])
        pay = p * (100 - d) // 100
        assert p * (100 - d) % 100 == 0
        return (L(f"A bag costs ₹{p}. There is a {d}% discount. How much do you pay?", f"एका बॅगची किंमत ₹{p} आहे. {d}% सवलत आहे. तुम्हाला किती पैसे द्यावे लागतील?", f"एक बैग की कीमत ₹{p} है। उस पर {d}% छूट है। आपको कितने रुपये देने होंगे?"), f"₹{pay}",
                [f"₹{v}" for v in near(pay, r, [p * d // 100, p + p * d // 100, p - d], 10, lo=1)],
                L("Find the discount (percent of the price), then subtract it from the price.", "सवलत काढा (किमतीचे टक्के), ती किमतीतून वजा करा.", "छूट निकालिए (कीमत का प्रतिशत), फिर उसे कीमत में से घटाइए।"))

    def pct_change(r):
        old = r.choice([40, 50, 80, 120, 200, 250])
        p = r.choice([10, 15, 20, 25, 30, 40, 50])
        new = old * (100 + p) // 100
        if old * (100 + p) % 100 != 0:
            old, p, new = 80, 25, 100
        return (L(f"The price of a book rose from ₹{old} to ₹{new}. What is the percent increase?", f"पुस्तकाची किंमत ₹{old} वरून ₹{new} झाली. किती टक्के वाढ झाली?", f"एक पुस्तक की कीमत ₹{old} से बढ़कर ₹{new} हो गई। कितने प्रतिशत की वृद्धि हुई?"), f"{p}%",
                [f"{v}%" for v in near(p, r, [new * 100 // old - 100 + 5, (new - old), 100 * new // (old + new)], 5, lo=1) if True],
                L("Percent increase = (increase ÷ old price) × 100.", "टक्के वाढ = (वाढ ÷ जुनी किंमत) × 100.", "प्रतिशत वृद्धि = (वृद्धि ÷ पुरानी कीमत) × 100।"))

    def what_pct(r):
        tot = r.choice([20, 40, 50, 60, 80, 120, 150, 200, 250])
        p = r.choice([5, 10, 12, 15, 20, 25, 30, 35, 40, 60, 75])
        part = tot * p // 100
        if tot * p % 100:
            tot, p, part = 80, 25, 20
        return (L(f"What percent of {tot} is {part}?", f"{part} हे {tot} चे किती टक्के आहे?", f"{part}, {tot} का कितना प्रतिशत है?"), f"{p}%",
                [f"{v}%" for v in near(p, r, [100 - p, part, tot * 100 // (part * 10) if part else p + 1], 5, lo=1)],
                L("Percent = (part ÷ whole) × 100.", "टक्के = (भाग ÷ संपूर्ण) × 100.", "प्रतिशत = (भाग ÷ पूर्ण) × 100।"))

    def reverse_pct(r):
        orig = r.choice([200, 300, 400, 500, 800, 1000])
        d = r.choice([10, 20, 25, 40])
        sale = orig * (100 - d) // 100
        if orig * (100 - d) % 100:
            orig, d, sale = 400, 25, 300
        return (L(f"After a {d}% discount a watch costs ₹{sale}. What was the price before the discount?", f"{d}% सवलतीनंतर घड्याळाची किंमत ₹{sale} झाली. सवलतीआधी किंमत किती होती?", f"{d}% छूट के बाद एक घड़ी ₹{sale} की है। छूट से पहले कीमत कितनी थी?"), f"₹{orig}",
                [f"₹{v}" for v in near(orig, r, [sale + sale * d // 100, sale * 100 // (100 + d) if (sale * 100) % (100 + d) == 0 else orig + 50, sale + d], 20, lo=1)],
                L("The sale price is (100 − discount)% of the original. Divide by that percent and multiply by 100.", "सवलतीनंतरची किंमत म्हणजे मूळ किमतीचे (100 − सवलत)%. त्या टक्क्याने भागून 100 ने गुणा.", "छूट के बाद की कीमत मूल कीमत का (100 − छूट)% है। उस प्रतिशत से भाग देकर 100 से गुणा कीजिए।"))

    def mean_missing(r):
        n = 5
        nums = [r.randint(5, 25) for _ in range(4)]
        m = r.randint(10, 20)
        last = m * n - sum(nums)
        if last < 1:
            nums = [m - 2, m + 2, m - 1, m + 1]
            last = m * n - sum(nums)
        return (L(f"The mean of 5 numbers is {m}. Four of them are {', '.join(map(str, nums))}. What is the fifth?", f"5 संख्यांची सरासरी {m} आहे. त्यापैकी चार {', '.join(map(str, nums))} आहेत. पाचवी संख्या कोणती?", f"5 संख्याओं का माध्य {m} है। उनमें से चार {', '.join(map(str, nums))} हैं। पाँचवीं संख्या कौन-सी है?"), str(last),
                [str(v) for v in near(last, r, [m, sum(nums) // 4, m * 4 - sum(nums)], 2, lo=0)],
                L("Total = mean × how many. Fifth number = total − sum of the four.", "एकूण = सरासरी × संख्या. पाचवी संख्या = एकूण − चार संख्यांची बेरीज.", "कुल = माध्य × संख्याएँ। पाँचवीं संख्या = कुल − चार संख्याओं का योग।"))

    def lcm_word(r):
        a, c = r.choice([(12, 18), (8, 12), (15, 20), (10, 25), (6, 14), (9, 12), (16, 24), (20, 30)])
        l = a * c // gcd(a, c)
        return (L(f"Two bells ring every {a} minutes and every {c} minutes. They ring together now. After how many minutes will they ring together again?", f"दोन घंटा अनुक्रमे दर {a} मिनिटांनी आणि दर {c} मिनिटांनी वाजतात. त्या आता एकत्र वाजल्या. पुन्हा किती मिनिटांनी एकत्र वाजतील?", f"दो घंटियाँ क्रमशः हर {a} मिनट और हर {c} मिनट पर बजती हैं। वे अभी साथ बजीं। कितने मिनट बाद फिर साथ बजेंगी?"), f"{l} minutes",
                [f"{v} minutes" for v in near(l, r, [a * c if a * c != l else l + a, a + c, l // 2 if l % 2 == 0 and l // 2 != l else l + 6], 6, lo=1)],
                L("Find the LCM of the two numbers.", "दोन्ही संख्यांचा लसावि काढा.", "दोनों संख्याओं का ल.स. निकालिए।"))

    out[6].append(build_chapter(b, cid(6, 5), L("Percent and Data Challenge", "टक्केवारी व आकडेमोड आव्हान", "प्रतिशत और आँकड़े चुनौती"), L("Discounts, change, averages, LCM.", "सवलत, बदल, सरासरी, लसावि.", "छूट, परिवर्तन, माध्य, ल.स.।"), "chart",
                                [discount, pct_change, mean_missing, what_pct, reverse_pct, lcm_word]))

    # =============================================================================== CLASS 7
    def rfrac(r, neg_ok=True):
        d = r.choice([2, 3, 4, 5, 6, 7, 8, 9, 10, 12])
        n = r.randint(1, d - 1)
        return Fraction(n if not neg_ok or r.random() < 0.5 else -n, d)

    def rat_add(r):
        while True:
            f1, f2 = rfrac(r), rfrac(r)
            if f1.denominator != f2.denominator and f1 + f2 != 0:
                break
        ans = f1 + f2
        e = f"({frac(f1)}) + ({frac(f2)})"
        wr = [Fraction(f1.numerator + f2.numerator, f1.denominator + f2.denominator), f1 - f2, -ans, abs(f1) + abs(f2)]
        return (L(f"What is {e}?", f"{e} = ?", f"{e} = ?"), frac(ans), [frac(w) for w in wr if w != ans and w.denominator != 0],
                L("Make the bottom numbers equal, then add the tops with their signs. Simplify.", "छेद समान करा, अंश चिन्हांसह बेरीज करा. सोपे रूप द्या.", "हर समान कीजिए, अंशों को चिह्न सहित जोड़िए। सरल कीजिए।"))

    def rat_mul(r):
        f1, f2 = rfrac(r), rfrac(r)
        ans = f1 * f2
        e = f"({frac(f1)}) × ({frac(f2)})"
        wr = [-ans, Fraction(f1.numerator * f2.denominator, f1.denominator * f2.numerator) if f2.numerator else ans + 1, f1 + f2, Fraction(abs(f1.numerator) + abs(f2.numerator), f1.denominator * f2.denominator)]
        return (L(f"What is {e}?", f"{e} = ?", f"{e} = ?"), frac(ans), [frac(w) for w in wr if w != ans],
                L("Multiply tops and bottoms. Different signs give a negative answer. Simplify.", "अंश-अंश आणि छेद-छेद गुणा. वेगळी चिन्हे असतील तर उत्तर ऋण. सोपे रूप द्या.", "अंश×अंश और हर×हर कीजिए। अलग चिह्न हों तो उत्तर ऋणात्मक। सरल कीजिए।"))

    def rat_div(r):
        f1, f2 = rfrac(r), rfrac(r)
        ans = f1 / f2
        e = f"({frac(f1)}) ÷ ({frac(f2)})"
        wr = [f1 * f2, -ans, f2 / f1, Fraction(f1.numerator, f1.denominator * f2.numerator * f2.denominator) if f2.numerator else ans + 1]
        return (L(f"What is {e}?", f"{e} = ?", f"{e} = ?"), frac(ans), [frac(w) for w in wr if w != ans],
                L("Dividing by a fraction means multiplying by its reciprocal (flip it).", "अपूर्णांकाने भागणे म्हणजे त्याच्या व्यस्ताने गुणणे (उलटा करा).", "भिन्न से भाग देना यानी उसके व्युत्क्रम से गुणा करना (उलटिए)।"))

    def rat_lowest(r):
        f = rfrac(r)
        k = r.randint(3, 9)
        n, d = f.numerator * k, f.denominator * k
        return (L(f"Write {n}/{d} in lowest terms.", f"{n}/{d} सर्वांत सोप्या रूपात लिहा.", f"{n}/{d} को सरलतम रूप में लिखिए।"), frac(f),
                [frac(Fraction(f.numerator * 2, f.denominator * 2)) if False else f"{f.numerator * 2}/{f.denominator * 2}", frac(-f), f"{n // k}/{d // (k if d % k == 0 else 1) + 1}", frac(Fraction(f.denominator, abs(f.numerator)) if f.numerator else f)],
                L("Divide the top and bottom by their HCF.", "अंश आणि छेद त्यांच्या मसावीने भागा.", "अंश और हर को उनके म.स. से भाग दीजिए।"))

    def rat_recip(r):
        f = rfrac(r)
        ans = 1 / f
        return (L(f"What is the reciprocal of {frac(f)}?", f"{frac(f)} चा व्यस्त कोणता?", f"{frac(f)} का व्युत्क्रम क्या है?"), frac(ans),
                [frac(f), frac(-ans), frac(-f), frac(Fraction(f.denominator, f.denominator + abs(f.numerator)))],
                L("Flip the fraction upside down and keep the sign.", "अपूर्णांक उलटा करा; चिन्ह तेच ठेवा.", "भिन्न को उलटिए; चिह्न वही रखिए।"))

    def rat_compare(r):
        while True:
            f1, f2 = rfrac(r), rfrac(r)
            if f1 != f2 and f1.denominator != f2.denominator:
                break
        big = f1 if f1 > f2 else f2
        small = f2 if big is f1 else f1
        return (L(f"Which is greater: {frac(f1)} or {frac(f2)}?", f"कोणता मोठा: {frac(f1)} की {frac(f2)}?", f"कौन-सा बड़ा है: {frac(f1)} या {frac(f2)}?"), frac(big),
                [frac(small), "they are equal", frac(-big)] if frac(-big) not in (frac(big), frac(small)) else [frac(small), "they are equal", "cannot say"],
                L("Change both to the same bottom number, then compare the tops. On the number line, right is greater.", "दोन्ही समान छेदात आणा, मग अंश तुलना करा. संख्यारेषेवर उजवीकडील संख्या मोठी.", "दोनों को समान हर में बदलिए, फिर अंश की तुलना कीजिए। संख्या रेखा पर दाईं संख्या बड़ी होती है।"))

    out[7] = [build_chapter(b, cid(7, 1), L("Rational Number Workout", "परिमेय संख्यांचा सराव", "परिमेय संख्या अभ्यास"), L("Add, multiply, divide and compare.", "बेरीज, गुणाकार, भागाकार, तुलना.", "जोड़, गुणा, भाग और तुलना।"), "brain",
                          [rat_add, rat_mul, rat_div, rat_lowest, rat_recip, rat_compare])]

    # ---------------------------------------------------------------- 7.2 profit, loss, interest
    def profit_pct(r):
        cp = r.choice([200, 250, 400, 500, 800, 1000, 1200])
        p = r.choice([10, 12, 15, 20, 25, 30])
        gain = cp * p // 100
        if cp * p % 100:
            cp, p, gain = 400, 25, 100
        sp = cp + gain
        return (L(f"A shopkeeper buys a fan for ₹{cp} and sells it for ₹{sp}. What is the profit percent?", f"दुकानदाराने एक पंखा ₹{cp} ला घेतला आणि ₹{sp} ला विकला. नफा किती टक्के?", f"एक दुकानदार ने पंखा ₹{cp} में खरीदा और ₹{sp} में बेचा। लाभ कितने प्रतिशत है?"), f"{p}%",
                [f"{v}%" for v in near(p, r, [100 * gain // sp if 100 * gain % sp == 0 else p + 5, gain, p * 2], 5, lo=1)],
                L("Profit % = (profit ÷ cost price) × 100.", "नफा % = (नफा ÷ खरेदी किंमत) × 100.", "लाभ % = (लाभ ÷ क्रय मूल्य) × 100।"))

    def loss_sp(r):
        cp = r.choice([400, 500, 600, 800, 1000, 1500, 2000])
        l = r.choice([5, 10, 20, 25])
        sp = cp * (100 - l) // 100
        if cp * (100 - l) % 100:
            cp, l, sp = 400, 25, 300
        return (L(f"A watch bought for ₹{cp} is sold at a loss of {l}%. What is the selling price?", f"₹{cp} ला घेतलेले घड्याळ {l}% तोट्याने विकले. विक्री किंमत किती?", f"₹{cp} में खरीदी घड़ी {l}% हानि पर बेची गई। विक्रय मूल्य कितना है?"), f"₹{sp}",
                [f"₹{v}" for v in near(sp, r, [cp + cp * l // 100, cp * l // 100, cp - l], 20, lo=1)],
                L("Loss = loss% of the cost price. Selling price = cost price − loss.", "तोटा = खरेदी किमतीचे तोटा%. विक्री किंमत = खरेदी किंमत − तोटा.", "हानि = क्रय मूल्य का हानि%। विक्रय मूल्य = क्रय मूल्य − हानि।"))

    def cp_from_sp(r):
        cp = r.choice([400, 500, 800, 1000, 1200, 2000])
        p = r.choice([5, 10, 20, 25])
        sp = cp * (100 + p) // 100
        if cp * (100 + p) % 100:
            cp, p, sp = 400, 25, 500
        return (L(f"A chair is sold for ₹{sp} at a profit of {p}%. What did it cost the seller?", f"एक खुर्ची {p}% नफ्याने ₹{sp} ला विकली. विक्रेत्याला ती किती रुपयांना पडली?", f"एक कुर्सी {p}% लाभ पर ₹{sp} में बेची गई। विक्रेता को वह कितने में पड़ी?"), f"₹{cp}",
                [f"₹{v}" for v in near(cp, r, [sp - sp * p // 100, sp - p, sp * (100 - p) // 100], 20, lo=1)],
                L("Selling price = (100 + profit)% of the cost price. Divide by that percent, multiply by 100.", "विक्री किंमत = खरेदी किमतीचे (100 + नफा)%. त्याने भागून 100 ने गुणा.", "विक्रय मूल्य = क्रय मूल्य का (100 + लाभ)%। उससे भाग देकर 100 से गुणा कीजिए।"))

    def si_rate(r):
        p = r.choice([1000, 1500, 2000, 2500, 4000, 5000])
        rate = r.choice([4, 5, 6, 8, 10])
        t = r.choice([2, 3, 4, 5])
        si = p * rate * t // 100
        return (L(f"₹{p} earns simple interest of ₹{si} in {t} years. What is the rate percent per year?", f"₹{p} वर {t} वर्षांत ₹{si} साधे व्याज मिळाले. वार्षिक दर किती टक्के?", f"₹{p} पर {t} वर्ष में ₹{si} साधारण ब्याज मिला। वार्षिक दर कितने प्रतिशत है?"), f"{rate}%",
                [f"{v}%" for v in near(rate, r, [si * 100 // p, si // t, rate * t], 1, lo=1)],
                L("Rate = (interest × 100) ÷ (principal × time).", "दर = (व्याज × 100) ÷ (मुद्दल × काळ).", "दर = (ब्याज × 100) ÷ (मूलधन × समय)।"))

    def si_amount(r):
        p = r.choice([1200, 1500, 2000, 2500, 3000, 4000, 5000])
        rate = r.choice([4, 5, 6, 8, 10])
        t = r.choice([2, 3, 4])
        si = p * rate * t // 100
        return (L(f"₹{p} is kept for {t} years at {rate}% simple interest per year. What is the total amount at the end?", f"₹{p} वार्षिक {rate}% साध्या व्याजाने {t} वर्षे ठेवले. शेवटी एकूण रक्कम किती?", f"₹{p} को वार्षिक {rate}% साधारण ब्याज पर {t} वर्ष रखा गया। अंत में कुल राशि कितनी होगी?"), f"₹{p + si}",
                [f"₹{v}" for v in near(p + si, r, [si, p - si, p + si // t], max(20, si // 4), lo=1)],
                L("Interest = P × R × T ÷ 100. Amount = principal + interest.", "व्याज = मुद्दल × दर × काळ ÷ 100. रक्कम = मुद्दल + व्याज.", "ब्याज = मूलधन × दर × समय ÷ 100। राशि = मूलधन + ब्याज।"))

    def disc_chain(r):
        mp = r.choice([500, 800, 1000, 1200, 2000])
        d1, d2 = r.choice([(10, 10), (20, 10), (25, 20), (10, 20), (20, 25), (50, 10)])
        x = mp * (100 - d1) // 100
        if mp * (100 - d1) % 100 or x * (100 - d2) % 100:
            mp, d1, d2 = 1000, 20, 10
            x = 800
        ans = x * (100 - d2) // 100
        return (L(f"The marked price of a jacket is ₹{mp}. A shop gives {d1}% off, and then another {d2}% off the new price. What is the final price?", f"जॅकेटची छापील किंमत ₹{mp} आहे. दुकान {d1}% सूट देते आणि नंतर नव्या किमतीवर आणखी {d2}% सूट. अंतिम किंमत किती?", f"जैकेट का अंकित मूल्य ₹{mp} है। दुकान {d1}% छूट देता है और फिर नई कीमत पर {d2}% और छूट। अंतिम कीमत कितनी है?"), f"₹{ans}",
                [f"₹{v}" for v in near(ans, r, [mp * (100 - d1 - d2) // 100, x, mp - mp * d1 // 100 - mp * d2 // 100 if False else mp * (100 - d1 - d2) // 100 + 20], 20, lo=1)],
                L("Take the first discount off. Then take the second discount off the NEW price (not the marked price).", "आधी पहिली सूट वजा करा. मग दुसरी सूट नव्या किमतीवर (छापील किमतीवर नव्हे) वजा करा.", "पहले पहली छूट घटाइए। फिर दूसरी छूट नई कीमत पर (अंकित मूल्य पर नहीं) घटाइए।"))

    def tax(r):
        p = r.choice([200, 250, 400, 500, 800, 1000])
        t = r.choice([5, 12, 18])
        tax_ = Fraction(p * t, 100)
        ans = p + tax_
        return (L(f"A bill is ₹{p} before tax. The tax is {t}%. What is the total to pay?", f"करापूर्वी बिल ₹{p} आहे. कर {t}% आहे. एकूण किती द्यावे लागतील?", f"कर से पहले बिल ₹{p} है। कर {t}% है। कुल कितना देना होगा?"), f"₹{dec(ans)}",
                [f"₹{dec(v)}" for v in near(ans, r, [tax_, p - tax_, p + t], 5, lo=1)],
                L("Tax = tax% of the bill. Total = bill + tax.", "कर = बिलाचे कर%. एकूण = बिल + कर.", "कर = बिल का कर%। कुल = बिल + कर।"))

    out[7].append(build_chapter(b, cid(7, 2), L("Profit, Loss and Interest Challenge", "नफा, तोटा, व्याज आव्हान", "लाभ, हानि, ब्याज चुनौती"), L("Percent, reverse percent, simple interest.", "टक्के, उलट टक्के, साधे व्याज.", "प्रतिशत, उलटा प्रतिशत, साधारण ब्याज।"), "coins",
                                [profit_pct, loss_sp, cp_from_sp, si_rate, si_amount, disc_chain, tax]))

    # ---------------------------------------------------------------- 7.3 linear equations
    def le_bracket(r):
        k, x, m = r.randint(2, 7), r.randint(2, 12), r.randint(1, 9)
        rhs = k * (x + m)
        return (L(f"Solve: {k}(x + {m}) = {rhs}", f"सोडवा: {k}(x + {m}) = {rhs}", f"हल कीजिए: {k}(x + {m}) = {rhs}"), str(x),
                [str(v) for v in near(x, r, [rhs // k, rhs - m, rhs // k + m if rhs // k + m != x else x + 2], 2, lo=0)],
                L("Divide both sides by the number outside the bracket, then take away the number inside.", "कंसाबाहेरील संख्येने दोन्ही बाजू भागा, मग कंसातील संख्या वजा करा.", "कोष्ठक के बाहर की संख्या से दोनों ओर भाग दीजिए, फिर कोष्ठक के अंदर की संख्या घटाइए।"))

    def le_both(r):
        x = r.randint(2, 15)
        a = r.randint(3, 8)
        c = r.randint(1, a - 1)
        c1 = r.randint(1, 12)
        c2 = c1 + (a - c) * x
        return (L(f"Solve: {a}x - {c1} = {c}x + {c2 - 2 * c1}" if False else f"Solve: {a}x - {c1} = {c}x + {c2 - 2 * c1}", f"सोडवा: {a}x - {c1} = {c}x + {c2 - 2 * c1}", f"हल कीजिए: {a}x - {c1} = {c}x + {c2 - 2 * c1}"), str(x),
                [str(v) for v in near(x, r, [c2 - c1, (c2 - 3 * c1) // (a - c) if (c2 - 3 * c1) % (a - c) == 0 else x + 2, x + 1], 2, lo=0)],
                L("Move x terms to one side and numbers to the other, then divide.", "x ची पदे एका बाजूला आणि संख्या दुसऱ्या बाजूला न्या, मग भागा.", "x वाले पद एक ओर और संख्याएँ दूसरी ओर ले जाइए, फिर भाग दीजिए।"))

    def le_frac(r):
        x = r.choice([8, 12, 16, 20, 24, 28, 36, 40])
        d = r.choice([2, 4])
        if x % d:
            x = 20
        c = r.randint(2, 9)
        rhs = x // d + c
        return (L(f"Solve: x/{d} + {c} = {rhs}", f"सोडवा: x/{d} + {c} = {rhs}", f"हल कीजिए: x/{d} + {c} = {rhs}"), str(x),
                [str(v) for v in near(x, r, [rhs - c, (rhs + c) * d, rhs * d], 4, lo=1)],
                L("Take away the number first, then multiply both sides by the bottom number.", "आधी संख्या वजा करा, मग दोन्ही बाजूंना छेदाने गुणा.", "पहले संख्या घटाइए, फिर दोनों ओर हर से गुणा कीजिए।"))

    def le_word1(r):
        x = r.choice([20, 24, 30, 36, 40, 48])
        tot = x + x // 2
        return (L(f"The sum of a number and its half is {tot}. What is the number?", f"एका संख्येची आणि तिच्या निम्म्याची बेरीज {tot} आहे. ती संख्या कोणती?", f"एक संख्या और उसके आधे का योग {tot} है। वह संख्या कौन-सी है?"), str(x),
                [str(v) for v in near(x, r, [tot // 2, tot - x // 2 if tot - x // 2 != x else x + 4, tot * 2 // 3 + 2], 4, lo=1)],
                L("Let the number be x. Then x + x/2 = total, which is (3/2)x.", "संख्या x मानू. मग x + x/2 = एकूण, म्हणजे (3/2)x.", "संख्या x मानिए। तब x + x/2 = कुल, यानी (3/2)x।"))

    def le_word2(r):
        g = r.randint(5, 12)
        extra = r.randint(2, 6)
        s = 2 * g + extra
        tot = g + s
        return (L(f"Sita is {extra} years older than twice Gita's age. Together their ages add up to {tot}. How old is Gita?", f"सीता ही गीताच्या वयाच्या दुप्पटीपेक्षा {extra} वर्षांनी मोठी आहे. दोघींच्या वयांची बेरीज {tot} आहे. गीता किती वर्षांची?", f"सीता की आयु गीता की आयु के दुगुने से {extra} वर्ष अधिक है। दोनों की आयु का योग {tot} है। गीता की आयु कितनी है?"), f"{g} years",
                [f"{v} years" for v in near(g, r, [tot // 3, tot - extra, (tot - extra) // 2 if (tot - extra) // 2 != g else g + 2], 1, lo=1)],
                L("Let Gita be x. Sita is 2x + extra. Then x + (2x + extra) = total.", "गीता x मानू. सीता 2x + अधिक वर्षे. मग x + (2x + अधिक) = एकूण.", "गीता को x मानिए। सीता 2x + अधिक वर्ष। तब x + (2x + अधिक) = कुल।"))

    def le_perim(r):
        x = r.randint(4, 15)
        m = r.randint(2, 6)
        perim = 2 * ((x + m) + x)
        return (L(f"A rectangle has length (x + {m}) cm and width x cm. Its perimeter is {perim} cm. What is x?", f"एका आयताची लांबी (x + {m}) सेमी आणि रुंदी x सेमी आहे. त्याची परिमिती {perim} सेमी आहे. x किती?", f"एक आयत की लंबाई (x + {m}) सेमी और चौड़ाई x सेमी है। उसका परिमाप {perim} सेमी है। x कितना है?"), str(x),
                [str(v) for v in near(x, r, [perim // 4, perim // 2 - m, (perim - m) // 4 if (perim - m) % 4 else x + 3], 2, lo=1)],
                L("Perimeter = 2 × (length + width). Write it as an equation and solve.", "परिमिती = 2 × (लांबी + रुंदी). समीकरण मांडून सोडवा.", "परिमाप = 2 × (लंबाई + चौड़ाई)। समीकरण बनाकर हल कीजिए।"))

    out[7].append(build_chapter(b, cid(7, 3), L("Linear Equation Lab", "एकचल समीकरण प्रयोगशाळा", "रैखिक समीकरण प्रयोगशाला"), L("Brackets, both sides, fractions, word problems.", "कंस, दोन्ही बाजू, अपूर्णांक, शाब्दिक उदाहरणे.", "कोष्ठक, दोनों पक्ष, भिन्न, शाब्दिक प्रश्न।"), "flask",
                                [le_bracket, le_both, le_frac, le_word1, le_word2, le_perim]))

    # ---------------------------------------------------------------- 7.4 geometry & mensuration
    def circ_area(r):
        rad = 7 * r.randint(1, 4)
        ans = Fraction(22, 7) * rad * rad
        return (L(f"Using π = 22/7, what is the area of a circle of radius {rad} cm?", f"π = 22/7 घेऊन {rad} सेमी त्रिज्येच्या वर्तुळाचे क्षेत्रफळ किती?", f"π = 22/7 लेकर {rad} सेमी त्रिज्या वाले वृत्त का क्षेत्रफल कितना है?"), f"{dec(ans)} cm²",
                [f"{dec(v)} cm²" for v in near(ans, r, [Fraction(22, 7) * 2 * rad, Fraction(22, 7) * rad * rad * 2, Fraction(22, 7) * (2 * rad) ** 2 if False else Fraction(22, 7) * rad], 14, lo=1)],
                L("Area of a circle = π × r × r.", "वर्तुळाचे क्षेत्रफळ = π × r × r.", "वृत्त का क्षेत्रफल = π × r × r।"))

    def circ_len(r):
        rad = 7 * r.randint(1, 5)
        ans = Fraction(22, 7) * 2 * rad
        return (L(f"Using π = 22/7, what is the circumference of a circle of diameter {2 * rad} cm?", f"π = 22/7 घेऊन {2 * rad} सेमी व्यासाच्या वर्तुळाचा परीघ किती?", f"π = 22/7 लेकर {2 * rad} सेमी व्यास वाले वृत्त की परिधि कितनी है?"), f"{dec(ans)} cm",
                [f"{dec(v)} cm" for v in near(ans, r, [Fraction(22, 7) * rad * rad, Fraction(22, 7) * rad, ans * 2], 4, lo=1)],
                L("Circumference = π × diameter.", "परीघ = π × व्यास.", "परिधि = π × व्यास।"))

    triples = [(3, 4, 5), (6, 8, 10), (5, 12, 13), (9, 12, 15), (8, 15, 17), (7, 24, 25), (12, 16, 20), (15, 20, 25), (20, 21, 29)]

    def pyth_hyp(r):
        a, c, h = r.choice(triples)
        return (L(f"A right-angled triangle has legs {a} cm and {c} cm. What is the hypotenuse?", f"काटकोन त्रिकोणाच्या दोन बाजू {a} सेमी आणि {c} सेमी आहेत. कर्ण किती?", f"एक समकोण त्रिभुज की दो भुजाएँ {a} सेमी और {c} सेमी हैं। कर्ण कितना है?"), f"{h} cm",
                [f"{v} cm" for v in near(h, r, [a + c, abs(c - a), h + 2], 1, lo=1)],
                L("Hypotenuse² = leg² + leg². Take the square root.", "कर्ण² = बाजू² + बाजू². वर्गमूळ काढा.", "कर्ण² = भुजा² + भुजा²। वर्गमूल निकालिए।"))

    def pyth_leg(r):
        a, c, h = r.choice(triples)
        return (L(f"In a right-angled triangle the hypotenuse is {h} cm and one leg is {a} cm. How long is the other leg?", f"काटकोन त्रिकोणात कर्ण {h} सेमी आणि एक बाजू {a} सेमी आहे. दुसरी बाजू किती?", f"समकोण त्रिभुज में कर्ण {h} सेमी और एक भुजा {a} सेमी है। दूसरी भुजा कितनी है?"), f"{c} cm",
                [f"{v} cm" for v in near(c, r, [h - a, h + a, (h * h - a * a) // 10 if (h * h - a * a) // 10 != c else c + 1], 1, lo=1)],
                L("Other leg² = hypotenuse² − known leg². Take the square root.", "दुसरी बाजू² = कर्ण² − ज्ञात बाजू². वर्गमूळ काढा.", "दूसरी भुजा² = कर्ण² − ज्ञात भुजा²। वर्गमूल निकालिए।"))

    def ext_angle(r):
        a = r.randint(30, 80)
        c = r.randint(30, 80)
        return (L(f"In a triangle two interior angles are {a}° and {c}°. What is the exterior angle at the third corner?", f"त्रिकोणाचे दोन आंतरकोन {a}° आणि {c}° आहेत. तिसऱ्या कोपऱ्यातील बाह्यकोन किती?", f"एक त्रिभुज के दो अंतःकोण {a}° और {c}° हैं। तीसरे शीर्ष पर बहिष्कोण कितना है?"), f"{a + c}°",
                [f"{v}°" for v in near(a + c, r, [180 - a - c, 360 - a - c, 180 - (a + c) + 10 if 180 - (a + c) + 10 != a + c else a + c + 10], 10, lo=1)],
                L("An exterior angle equals the sum of the two opposite interior angles.", "बाह्यकोन = दोन विरुद्ध आंतरकोनांची बेरीज.", "बहिष्कोण = दो सम्मुख अंतःकोणों का योग।"))

    def composite(r):
        l, w = r.randint(10, 20), r.randint(6, 12)
        s = r.randint(2, 4)
        ans = l * w - s * s
        return (L(f"A {l} cm by {w} cm rectangle has a {s} cm by {s} cm square cut out of it. What is the area left?", f"{l} सेमी × {w} सेमी आयतातून {s} सेमी × {s} सेमी चौरस कापला. उरलेले क्षेत्रफळ किती?", f"{l} सेमी × {w} सेमी के आयत में से {s} सेमी × {s} सेमी का वर्ग काट लिया गया। शेष क्षेत्रफल कितना है?"), f"{ans} cm²",
                [f"{v} cm²" for v in near(ans, r, [l * w + s * s, l * w - s, l * w], 4, lo=1)],
                L("Area left = big area − area cut out.", "उरलेले क्षेत्रफळ = मोठे क्षेत्रफळ − कापलेले क्षेत्रफळ.", "शेष क्षेत्रफल = बड़ा क्षेत्रफल − काटा हुआ क्षेत्रफल।"))

    def cube_sa(r):
        s = r.randint(3, 12)
        ans = 6 * s * s
        return (L(f"What is the total surface area of a cube with edge {s} cm?", f"{s} सेमी कडा असलेल्या घनाचे एकूण पृष्ठफळ किती?", f"{s} सेमी किनारे वाले घन का कुल पृष्ठीय क्षेत्रफल कितना है?"), f"{ans} cm²",
                [f"{v} cm²" for v in near(ans, r, [s ** 3, 4 * s * s, 6 * s], 6, lo=1)],
                L("A cube has 6 equal square faces: 6 × edge × edge.", "घनाला 6 समान चौरस पृष्ठे असतात: 6 × कडा × कडा.", "घन के 6 समान वर्ग फलक होते हैं: 6 × किनारा × किनारा।"))

    def cuboid_vol(r):
        l, w, h = r.randint(4, 15), r.randint(3, 10), r.randint(2, 9)
        ans = l * w * h
        return (L(f"A tank is {l} m long, {w} m wide and {h} m deep. How many cubic metres of water can it hold?", f"एका टाकीची लांबी {l} मी, रुंदी {w} मी आणि खोली {h} मी आहे. त्यात किती घनमीटर पाणी मावेल?", f"एक टंकी {l} मी लंबी, {w} मी चौड़ी और {h} मी गहरी है। उसमें कितने घन मीटर पानी आएगा?"), f"{ans} m³",
                [f"{v} m³" for v in near(ans, r, [2 * (l * w + w * h + h * l), l * w + h, l + w + h], max(3, ans // 20), lo=1)],
                L("Volume = length × width × depth.", "घनफळ = लांबी × रुंदी × खोली.", "आयतन = लंबाई × चौड़ाई × गहराई।"))

    out[7].append(build_chapter(b, cid(7, 4), L("Geometry and Mensuration Challenge", "भूमिती व क्षेत्रमापन आव्हान", "ज्यामिति और क्षेत्रमिति चुनौती"), L("Circles, Pythagoras, angles, volume.", "वर्तुळ, पायथागोरस, कोन, घनफळ.", "वृत्त, पाइथागोरस, कोण, आयतन।"), "ruler",
                                [circ_area, pyth_hyp, circ_len, pyth_leg, ext_angle, composite, cube_sa, cuboid_vol]))

    # ---------------------------------------------------------------- 7.5 powers & number sense
    def pw_mul(r):
        b_ = r.randint(2, 9)
        m, n = r.randint(2, 7), r.randint(2, 7)
        return (L(f"Simplify {b_}^{m} × {b_}^{n} using the laws of exponents.", f"घातांकाचे नियम वापरून {b_}^{m} × {b_}^{n} सोपे करा.", f"घातांक के नियम से {b_}^{m} × {b_}^{n} सरल कीजिए।"), f"{b_}^{m + n}",
                [f"{b_}^{m * n}", f"{b_ * b_}^{m + n}", f"{b_}^{abs(m - n) or m + n + 1}", f"{b_}^{m + n + 1}"],
                L("Same base: add the powers (a^m × a^n = a^(m+n)).", "आधार सारखा: घात बेरीज करा (a^m × a^n = a^(m+n)).", "आधार समान: घात जोड़िए (a^m × a^n = a^(m+n))।"))

    def pw_div(r):
        b_ = r.randint(2, 9)
        m = r.randint(5, 10)
        n = r.randint(2, m - 2)
        return (L(f"Simplify {b_}^{m} ÷ {b_}^{n}.", f"{b_}^{m} ÷ {b_}^{n} सोपे करा.", f"{b_}^{m} ÷ {b_}^{n} सरल कीजिए।"), f"{b_}^{m - n}",
                [f"{b_}^{m + n}", f"{b_}^{m // n if n else 1}", f"{b_}^{m * n}", f"1^{m - n}"],
                L("Same base: subtract the powers (a^m ÷ a^n = a^(m−n)).", "आधार सारखा: घात वजा करा (a^m ÷ a^n = a^(m−n)).", "आधार समान: घात घटाइए (a^m ÷ a^n = a^(m−n))।"))

    def pw_neg(r):
        b_ = r.choice([2, 3, 4, 5])
        e = r.choice([3, 5]) if b_ in (2, 3) else 3
        ans = (-b_) ** e
        return (L(f"What is (-{b_})^{e}?", f"(-{b_})^{e} ची किंमत किती?", f"(-{b_})^{e} का मान क्या है?"), str(ans),
                [str(v) for v in near(ans, r, [-ans, b_ * e, -b_ * e], b_, fmt=int) ] if False else [str(-ans), str(b_ * e), str(-b_ * e)],
                L("A negative number to an odd power stays negative; to an even power it becomes positive.", "ऋण संख्येला विषम घात असेल तर उत्तर ऋण; सम घात असेल तर धन.", "ऋणात्मक संख्या का विषम घात ऋणात्मक और सम घात धनात्मक होता है।"))

    def pw_pow_pow(r):
        b_ = r.choice([2, 3, 5])
        m, n = r.randint(2, 3), r.randint(2, 3)
        ans = b_ ** (m * n)
        return (L(f"What is the value of ({b_}^{m})^{n}?", f"({b_}^{m})^{n} ची किंमत किती?", f"({b_}^{m})^{n} का मान क्या है?"), str(ans),
                [str(v) for v in (b_ ** (m + n), b_ ** m * n, (b_ * m) ** n) if v != ans][:3] + [str(ans + b_)],
                L("(a^m)^n = a^(m×n). Multiply the powers, then work out the number.", "(a^m)^n = a^(m×n). घात गुणा करा, मग किंमत काढा.", "(a^m)^n = a^(m×n)। घात गुणा कीजिए, फिर मान निकालिए।"))

    def roots(r):
        if r.random() < 0.5:
            n = r.choice([14, 18, 21, 25, 32, 37, 41, 45])
            return (L(f"What is the square root of {n * n}?", f"{n * n} चे वर्गमूळ किती?", f"{n * n} का वर्गमूल क्या है?"), str(n),
                    [str(v) for v in near(n, r, [n * 2, n - 2, n + 2, n * n // 10], 1, lo=1)],
                    L("Think of a number that multiplied by itself gives this. Use the last digit to guess.", "कोणती संख्या स्वतःशी गुणल्यावर ही संख्या मिळते? शेवटच्या अंकावरून अंदाज करा.", "कौन-सी संख्या अपने से गुणा होकर यह संख्या देती है? अंतिम अंक से अनुमान लगाइए।"))
        n = r.choice([4, 5, 6, 7, 8, 9, 10, 12])
        return (L(f"What is the cube root of {n ** 3}?", f"{n ** 3} चे घनमूळ किती?", f"{n ** 3} का घनमूल क्या है?"), str(n),
                [str(v) for v in near(n, r, [n * 3 // 2, n - 1, n + 1, n ** 3 // 3], 1, lo=1)],
                L("Which number multiplied by itself three times gives this?", "कोणती संख्या तीनदा स्वतःशी गुणल्यावर ही संख्या मिळते?", "कौन-सी संख्या तीन बार अपने से गुणा होकर यह संख्या देती है?"))

    def hcf_lcm(r):
        base = r.choice([6, 7, 9, 12, 14, 15])
        a, c = r.sample([2, 3, 4, 5, 6, 7], 2)
        x, y = base * a, base * c
        if r.random() < 0.5:
            ans = gcd(x, y)
            return (L(f"What is the HCF of {x} and {y}?", f"{x} आणि {y} चा मसावि किती?", f"{x} और {y} का म.स. क्या है?"), str(ans),
                    [str(v) for v in near(ans, r, [x * y // ans, ans * 2 if ans * 2 != ans else ans + 1, x - y if x != y else ans + 2], max(1, ans // 3), lo=1)],
                    L("List the prime factors of both and multiply the common ones.", "दोन्ही संख्यांचे मूळ अवयव लिहा आणि समान अवयव गुणा.", "दोनों संख्याओं के अभाज्य गुणनखंड लिखिए और उभयनिष्ठ गुणनखंड गुणा कीजिए।"))
        ans = x * y // gcd(x, y)
        return (L(f"What is the LCM of {x} and {y}?", f"{x} आणि {y} चा लसावि किती?", f"{x} और {y} का ल.स. क्या है?"), str(ans),
                [str(v) for v in near(ans, r, [x * y if x * y != ans else ans + x, gcd(x, y), ans // 2 if ans % 2 == 0 else ans + 3], max(2, ans // 10), lo=1)],
                L("LCM = (first × second) ÷ HCF.", "लसावि = (पहिली × दुसरी) ÷ मसावि.", "ल.स. = (पहली × दूसरी) ÷ म.स.।"))

    def sci(r):
        m = Fraction(r.randint(11, 99), 10)
        e = r.randint(3, 6)
        n = int(m * 10 ** e)
        return (L(f"Write {n:,} in standard form.", f"{n:,} प्रमाणित रूपात लिहा.", f"{n:,} को मानक रूप में लिखिए।"), f"{dec(m)} × 10^{e}",
                [f"{dec(m)} × 10^{e + 1}", f"{dec(m * 10)} × 10^{e}", f"{dec(m)} × 10^{e - 1}", f"{dec(m / 10)} × 10^{e + 2}"],
                L("Standard form is a number between 1 and 10 times a power of 10. Count how many places the point moves.", "प्रमाणित रूप म्हणजे 1 ते 10 मधील संख्या × 10 चा घात. दशांश चिन्ह किती स्थाने सरकते ते मोजा.", "मानक रूप यानी 1 से 10 के बीच की संख्या × 10 की घात। दशमलव कितने स्थान खिसकता है, गिनिए।"))

    out[7].append(build_chapter(b, cid(7, 5), L("Powers and Number Sense Challenge", "घात व संख्याज्ञान आव्हान", "घात और संख्या ज्ञान चुनौती"), L("Exponent laws, roots, HCF, LCM, standard form.", "घातांकाचे नियम, मूळ, मसावि, लसावि, प्रमाणित रूप.", "घातांक नियम, मूल, म.स., ल.स., मानक रूप।"), "rocket",
                                [pw_mul, pw_div, pw_neg, pw_pow_pow, roots, hcf_lcm, sci]))
    return out
