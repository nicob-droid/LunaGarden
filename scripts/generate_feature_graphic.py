from pathlib import Path
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

OUT = Path(r"C:\Development\Android\LunaGarden\play-store-assets\feature-graphic-1024x500.png")
ICON = Path(r"C:\Development\Android\LunaGarden\play-store-assets\icon-1024.png")
W, H = 1024, 500

# Palette
TEAL_TOP = (12, 92, 104)
TEAL_BOTTOM = (7, 58, 66)
ACCENT = (255, 176, 59)


def load_font(paths, size):
    for p in paths:
        if Path(p).exists():
            return ImageFont.truetype(p, size)
    return ImageFont.load_default()


def lerp(a, b, t):
    return int(a + (b - a) * t)


def vertical_gradient(size, top, bottom):
    w, h = size
    grad = Image.new("RGB", (1, h))
    for y in range(h):
        t = y / (h - 1)
        grad.putpixel((0, y), (lerp(top[0], bottom[0], t),
                               lerp(top[1], bottom[1], t),
                               lerp(top[2], bottom[2], t)))
    return grad.resize((w, h))


def radial_glow(size, center, radius, color, max_alpha):
    w, h = size
    glow = Image.new("L", (w, h), 0)
    gd = ImageDraw.Draw(glow)
    steps = 60
    for i in range(steps, 0, -1):
        r = int(radius * i / steps)
        a = int(max_alpha * (1 - i / steps))
        gd.ellipse((center[0] - r, center[1] - r, center[0] + r, center[1] + r), fill=a)
    colored = Image.new("RGBA", (w, h), color + (0,))
    colored.putalpha(glow)
    return colored


def draw_star(d, cx, cy, r, color):
    d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=color)
    for (lx, ly) in [(r * 3, r * 0.5), (r * 0.5, r * 3)]:
        d.ellipse((cx - lx, cy - ly, cx + lx, cy + ly), fill=color)


def main():
    base = vertical_gradient((W, H), TEAL_TOP, TEAL_BOTTOM).convert("RGBA")

    # Soft glows for depth.
    base.alpha_composite(radial_glow((W, H), (300, 250), 520, (18, 150, 165), 90))
    base.alpha_composite(radial_glow((W, H), (900, 120), 360, ACCENT, 60))
    base.alpha_composite(radial_glow((W, H), (940, 470), 320, (10, 40, 48), 120))

    deco = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dd = ImageDraw.Draw(deco)

    # Large moon on the right with crescent shadow.
    moon_c = (838, 250)
    moon_r = 118
    dd.ellipse((moon_c[0] - moon_r, moon_c[1] - moon_r, moon_c[0] + moon_r, moon_c[1] + moon_r),
               fill=(255, 246, 224, 255))
    off = 52
    dd.ellipse((moon_c[0] - moon_r + off, moon_c[1] - moon_r - 6,
                moon_c[0] + moon_r + off, moon_c[1] + moon_r - 6),
               fill=(8, 66, 74, 255))
    base.alpha_composite(radial_glow((W, H), moon_c, 210, (255, 240, 210), 70))

    # Tiny stars.
    for (sx, sy, sr) in [(690, 90, 3), (760, 150, 2), (930, 300, 3),
                          (880, 400, 2), (620, 200, 2), (720, 360, 2), (980, 180, 2)]:
        draw_star(dd, sx, sy, sr, (255, 255, 255, 210))

    # Ground hills at bottom.
    hill1 = []
    for x in range(W):
        y = 402 + 30 * math.sin(x / 150.0) - 10 * math.sin(x / 60.0)
        hill1.append((x, y))
    dd.polygon([(0, H)] + hill1 + [(W, H)], fill=(9, 70, 60, 255))
    hill2 = []
    for x in range(W):
        y = 452 + 22 * math.sin((x + 300) / 130.0)
        hill2.append((x, y))
    dd.polygon([(0, H)] + hill2 + [(W, H)], fill=(6, 52, 46, 255))

    # Simple sprout on the hill.
    stem_x, stem_base_y, stem_top_y = 160, 452, 388
    dd.line([(stem_x, stem_base_y), (stem_x, stem_top_y)], fill=(120, 200, 120, 255), width=6)
    dd.ellipse((stem_x - 46, stem_top_y - 20, stem_x - 2, stem_top_y + 22), fill=(96, 186, 104, 255))
    dd.ellipse((stem_x + 2, stem_top_y - 30, stem_x + 50, stem_top_y + 12), fill=(120, 206, 120, 255))

    base = Image.alpha_composite(base, deco)

    # Icon card (rounded, soft shadow).
    icon = Image.open(ICON).convert("RGBA").resize((196, 196), Image.LANCZOS)
    card_size = 240
    shadow = Image.new("RGBA", (card_size + 60, card_size + 60), (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((36, 44, card_size + 24, card_size + 32), radius=54, fill=(0, 0, 0, 120))
    shadow = shadow.filter(ImageFilter.GaussianBlur(18))
    base.alpha_composite(shadow, (44, 118))

    card = Image.new("RGBA", (card_size, card_size), (0, 0, 0, 0))
    cd = ImageDraw.Draw(card)
    cd.rounded_rectangle((0, 0, card_size, card_size), radius=48, fill=(255, 255, 255, 250))
    cd.rounded_rectangle((0, 0, card_size, card_size), radius=48, outline=(255, 255, 255, 60), width=2)
    card.alpha_composite(icon, ((card_size - 196) // 2, (card_size - 196) // 2))
    base.alpha_composite(card, (62, 130))

    # Text block.
    d = ImageDraw.Draw(base)
    title = load_font([
        r"C:\Windows\Fonts\SegoeUI-Bold.ttf",
        r"C:\Windows\Fonts\seguisb.ttf",
        r"C:\Windows\Fonts\arialbd.ttf",
    ], 68)
    subtitle = load_font([
        r"C:\Windows\Fonts\SegoeUI-Semibold.ttf",
        r"C:\Windows\Fonts\seguisb.ttf",
        r"C:\Windows\Fonts\arial.ttf",
    ], 28)
    tagline = load_font([
        r"C:\Windows\Fonts\SegoeUI.ttf",
        r"C:\Windows\Fonts\arial.ttf",
    ], 24)

    x0 = 340
    d.text((x0 + 2, 150 + 2), "LunaGarden", font=title, fill=(0, 0, 0, 90))
    d.text((x0, 150), "LunaGarden", font=title, fill=(255, 255, 255, 255))

    # Accent underline.
    d.rounded_rectangle((x0 + 3, 228, x0 + 150, 234), radius=3, fill=ACCENT + (255,))

    d.text((x0, 252), "Jardinez avec la Lune", font=subtitle, fill=(255, 236, 205, 255))
    d.text((x0, 296), "Calendrier lunaire des semis et récoltes", font=tagline, fill=(226, 240, 238, 235))

    OUT.parent.mkdir(parents=True, exist_ok=True)
    base.convert("RGB").save(OUT, format="PNG", optimize=True)
    print(f"saved={OUT}")
    print(f"size={OUT.stat().st_size}")
    with Image.open(OUT) as check:
        print(f"dimensions={check.size[0]}x{check.size[1]}")


if __name__ == "__main__":
    main()

