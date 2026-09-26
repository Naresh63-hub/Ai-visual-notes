import os
from PIL import Image, ImageOps
import numpy as np

src_path = r"C:/Users/NARESH/.gemini/antigravity/brain/c67122af-6267-4abf-8595-cef5063f3e52/.user_uploaded/media_1790009325203.jpg"
output_dir = r"D:/notes/frontend/public"
icons_dir = os.path.join(output_dir, "icons")
android_dir = os.path.join(output_dir, "android")

os.makedirs(output_dir, exist_ok=True)
os.makedirs(icons_dir, exist_ok=True)
os.makedirs(android_dir, exist_ok=True)

# Load original
original = Image.open(src_path).convert("RGBA")

# Function to remove white background with smooth alpha blending
def remove_white_bg(img, threshold=245, feather=10):
    arr = np.array(img, dtype=np.float32)
    rgb = arr[:, :, :3]
    # compute distance from pure white (255, 255, 255)
    diff = 255.0 - np.min(rgb, axis=2)
    # diff == 0 means pure white (alpha 0)
    # diff > 20 means fully opaque (alpha 255)
    alpha = np.clip((diff / 18.0) * 255.0, 0, 255).astype(np.uint8)
    
    # To keep crisp colors without white fringe, normalize RGB where alpha > 0
    result_arr = np.zeros_like(arr, dtype=np.uint8)
    result_arr[:, :, :3] = arr[:, :, :3].astype(np.uint8)
    result_arr[:, :, 3] = alpha
    return Image.fromarray(result_arr, mode="RGBA")

# 1. Full Logo (Transparent and White-bg)
full_transparent = remove_white_bg(original)
full_transparent.save(os.path.join(output_dir, "logo-full.png"), "PNG")
original.convert("RGB").save(os.path.join(output_dir, "logo-full-white.png"), "PNG")
original.convert("RGB").save(os.path.join(output_dir, "logo-full.jpg"), "JPEG", quality=95)

# 2. Crop Icon-Only (Notebook + AI + Pen)
# The icon bounding box is approx (211, 141, 810, 652) -> width 600, height 512
# Let's crop with a nice balanced margin and make it a centered square
icon_crop_box = (195, 125, 825, 665) # width 630, height 540
icon_img = full_transparent.crop(icon_crop_box)

# Create a square canvas (e.g. 700x700) and paste the icon centered
w, h = icon_img.size
size = max(w, h) + 60
icon_square = Image.new("RGBA", (size, size), (0, 0, 0, 0))
paste_x = (size - w) // 2
paste_y = (size - h) // 2
icon_square.paste(icon_img, (paste_x, paste_y), icon_img)

# Save high-res master icon
icon_square.save(os.path.join(output_dir, "logo-icon.png"), "PNG")
icon_square.save(os.path.join(output_dir, "app-icon.png"), "PNG")

# Also save solid background version for Android adaptive / PWA maskable
icon_square_white = Image.new("RGBA", (size, size), (255, 255, 255, 255))
icon_square_white.paste(icon_img, (paste_x, paste_y), icon_img)
icon_square_white.save(os.path.join(output_dir, "logo-icon-solid.png"), "PNG")

# 3. Generate standard PWA icons
sizes = [
    ("favicon-16x16.png", 16),
    ("favicon-32x32.png", 32),
    ("favicon-48x48.png", 48),
    ("apple-touch-icon.png", 180),
    ("icon-192x192.png", 192),
    ("icon-512x512.png", 512),
    ("maskable-icon-512x512.png", 512),
]

for filename, sz in sizes:
    if "maskable" in filename:
        # Maskable icon requires safe zone padding
        resized = icon_square_white.resize((sz, sz), Image.Resampling.LANCZOS)
    else:
        resized = icon_square.resize((sz, sz), Image.Resampling.LANCZOS)
    resized.save(os.path.join(output_dir, filename), "PNG")
    resized.save(os.path.join(icons_dir, filename), "PNG")

# 4. Generate multi-resolution favicon.ico
fav_sizes = [(16, 16), (32, 32), (48, 48), (64, 64)]
fav_images = [icon_square.resize(s, Image.Resampling.LANCZOS) for s in fav_sizes]
fav_images[0].save(
    os.path.join(output_dir, "favicon.ico"),
    format="ICO",
    sizes=fav_sizes,
    append_images=fav_images[1:]
)

# 5. Generate Android Launcher & Adaptive icons
android_densities = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

for folder, sz in android_densities.items():
    density_dir = os.path.join(android_dir, folder)
    os.makedirs(density_dir, exist_ok=True)
    
    # ic_launcher.png (transparent)
    ic = icon_square.resize((sz, sz), Image.Resampling.LANCZOS)
    ic.save(os.path.join(density_dir, "ic_launcher.png"), "PNG")
    
    # ic_launcher_round.png
    ic.save(os.path.join(density_dir, "ic_launcher_round.png"), "PNG")
    
    # ic_launcher_foreground.png
    ic.save(os.path.join(density_dir, "ic_launcher_foreground.png"), "PNG")

# Splash Screen for mobile/Capacitor
splash = Image.new("RGBA", (1284, 2778), (255, 255, 255, 255))
# Place full logo centered in splash
full_logo_resized = full_transparent.resize((700, 700), Image.Resampling.LANCZOS)
splash_x = (1284 - 700) // 2
splash_y = (2778 - 700) // 2 - 100
splash.paste(full_logo_resized, (splash_x, splash_y), full_logo_resized)
splash.save(os.path.join(android_dir, "splash.png"), "PNG")

print("All branding assets generated successfully!")
