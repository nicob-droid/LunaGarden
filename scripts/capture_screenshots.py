"""Automation to capture LunaGarden screenshots for the Play Store.

Uses adb to dump the UI, tap buttons by text, and pull screenshots.
"""
import re
import subprocess
import time
from pathlib import Path

OUT = Path(r"C:\Development\Android\LunaGarden\play-store-assets\screenshots")
OUT.mkdir(parents=True, exist_ok=True)


def adb(*args, capture=True):
    return subprocess.run(["adb", *args], capture_output=capture, text=True)


def dump_ui():
    adb("shell", "uiautomator", "dump", "/sdcard/ui.xml")
    adb("pull", "/sdcard/ui.xml", str(OUT / "ui.xml"))
    return (OUT / "ui.xml").read_text(encoding="utf-8", errors="ignore")


def center_of(bounds):
    m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", bounds)
    x1, y1, x2, y2 = map(int, m.groups())
    return (x1 + x2) // 2, (y1 + y2) // 2


def find_bounds_by_text(xml, text):
    m = re.search(r'<node[^>]*text="' + re.escape(text) + r'"[^>]*bounds="(\[\d+,\d+\]\[\d+,\d+\])"', xml)
    if not m:
        m = re.search(r'<node[^>]*bounds="(\[\d+,\d+\]\[\d+,\d+\])"[^>]*text="' + re.escape(text) + r'"', xml)
    return m.group(1) if m else None


def tap_text(text):
    xml = dump_ui()
    b = find_bounds_by_text(xml, text)
    if b:
        x, y = center_of(b)
        adb("shell", "input", "tap", str(x), str(y))
        return True
    return False


def screenshot(name):
    adb("shell", "screencap", "-p", f"/sdcard/{name}.png")
    adb("pull", f"/sdcard/{name}.png", str(OUT / f"{name}.png"))
    print(f"captured {name}")


def texts(xml):
    return sorted(set(re.findall(r'text="([^"]+)"', xml)))


def main():
    time.sleep(1)
    xml = dump_ui()
    print("SCREEN TEXTS:", texts(xml)[:20])


if __name__ == "__main__":
    main()

