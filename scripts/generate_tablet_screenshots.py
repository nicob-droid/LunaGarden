"""Generate tablet screenshots for the Play Store from existing phone captures.

Play Store accepts tablet screenshots as PNG/JPEG with each side between
320px and 3840px. This script composes each phone screenshot onto a themed
tablet-sized canvas (7-inch and 10-inch, portrait) so the store listing has
proper tablet assets without needing a tablet device.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter

SRC = Path(r"C:\Development\Android\LunaGarden\play-store-assets\screenshots")
OUT10 = SRC / "tablet-10"
OUT7 = SRC / "tablet-7"
OUT10.mkdir(parents=True, exist_ok=True)
OUT7.mkdir(parents=True, exist_ok=True)

TEAL_TOP = (12, 92, 104)
TEAL_BOTTOM = (7, 58, 66)

PHONES = [
    "phone-01-accueil.png",
    "phone-02-question.png",
    "phone-03-selection-legumes.png",
    "phone-04-config-terminee.png",
    "phone-05-calendrier.png",
]


def lerp(a, b, t):
    return int(a + (b - a) * t)


def gradient(w, h, top, bottom):
    g = Image.new("RGB", (1, h))
    for y in range(h):
        t = y / (h - 1)
        g.putpixel((0, y), (lerp(top[0], bottom[0], t),
                            lerp(top[1], bottom[1], t),
                            lerp(top[2], bottom[2], t)))
    return g.resize((w, h)).convert("RGBA")


def compose(canvas_w, canvas_h, phone_path, out_path):
    canvas = gradient(canvas_w, canvas_h, TEAL_TOP, TEAL_BOTTOM)
    phone = Image.open(phone_path).convert("RGBA")

    # Scale the phone shot to occupy ~82% of the canvas height.
    target_h = int(canvas_h * 0.82)
    scale = target_h / phone.height
    target_w = int(phone.width * scale)
    # Guard: keep within width.
    if target_w > int(canvas_w * 0.7):
        target_w = int(canvas_w * 0.7)
        scale = target_w / phone.width
        target_h = int(phone.height * scale)
    phone = phone.resize((target_w, target_h), Image.LANCZOS)

    x = (canvas_w - target_w) // 2
    y = (canvas_h - target_h) // 2

    # Rounded corners + shadow for a device-like look.
    radius = int(target_w * 0.05)
    mask = Image.new("L", (target_w, target_h), 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, target_w, target_h), radius=radius, fill=255)

    shadow = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((x + 14, y + 22, x + target_w + 14, y + target_h + 22),
                         radius=radius, fill=(0, 0, 0, 130))
    shadow = shadow.filter(ImageFilter.GaussianBlur(26))
    canvas.alpha_composite(shadow)

    canvas.paste(phone, (x, y), mask)

    # Thin light border around the screen.
    bd = ImageDraw.Draw(canvas)
    bd.rounded_rectangle((x, y, x + target_w, y + target_h), radius=radius,
                         outline=(255, 255, 255, 60), width=2)

    canvas.convert("RGB").save(out_path, format="PNG", optimize=True)
    return out_path.stat().st_size


def main():
    log = []
    for name in PHONES:
        src = SRC / name
        if not src.exists():
            log.append(f"MISSING {name}")
            continue
        # 10-inch portrait: 1600x2560. 7-inch portrait: 1200x1920.
        s10 = compose(1600, 2560, src, OUT10 / name)
        s7 = compose(1200, 1920, src, OUT7 / name)
        log.append(f"OK {name} 10in={s10}B 7in={s7}B")
    result = "\n".join(log)
    (SRC / "tablet_generation.log").write_text(result, encoding="utf-8")
    print(result)


if __name__ == "__main__":
    main()

