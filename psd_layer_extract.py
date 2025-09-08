import sys
import os
from psd_tools import PSDImage
from PIL import Image

def sanitize_filename(name: str) -> str:
    # remove or replace characters not allowed in filenames
    return "".join(c if c.isalnum() or c in (" ", "_", "-") else "_" for c in name).strip()

def extract_layers(psd_path, out_dir):
    psd = PSDImage.open(psd_path)
    canvas_width, canvas_height = psd.width, psd.height
    os.makedirs(out_dir, exist_ok=True)

    for i, layer in enumerate(psd.descendants()):
        if layer.is_group():
            continue  # skip folders/groups

        layer_image = layer.topil()
        if layer_image is not None:
            # Create a full-size transparent canvas
            full_canvas = Image.new("RGBA", (canvas_width, canvas_height), (0, 0, 0, 0))

            # Paste the layer in its PSD position
            full_canvas.paste(layer_image, (layer.left, layer.top), layer_image)

            # Sanitize layer name
            layer_name = sanitize_filename(layer.name)
            if not layer_name:
                layer_name = f"layer_{i}"  # fallback if name is empty

            filename = os.path.join(out_dir, f"{layer_name}.png")
            full_canvas.save(filename)
            #print(filename)

if __name__ == "__main__":
    psd_path = sys.argv[1]
    out_dir = sys.argv[2]
    extract_layers(psd_path, out_dir)