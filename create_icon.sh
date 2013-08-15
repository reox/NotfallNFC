NAME="ic_launcher.png"
INPUT=./AppLogo.svg

# Notfall/res/drawable-hdpi/ic_launcher.png:   PNG image data, 72 x 72, 8-bit/color RGBA, non-interlaced
# Notfall/res/drawable-mdpi/ic_launcher.png:   PNG image data, 48 x 48, 8-bit/color RGBA, non-interlaced
# Notfall/res/drawable-xhdpi/ic_launcher.png:  PNG image data, 96 x 96, 8-bit/color RGBA, non-interlaced
# Notfall/res/drawable-xxhdpi/ic_launcher.png: PNG image data, 144 x 144, 8-bit/color RGBA, non-interlaced

rsvg-convert $INPUT -o Notfall/res/drawable-mdpi/$NAME -w 48 -h 48
rsvg-convert $INPUT -o Notfall/res/drawable-hdpi/$NAME -w 72 -h 72
rsvg-convert $INPUT -o Notfall/res/drawable-xhdpi/$NAME -w 96 -h 96
rsvg-convert $INPUT -o Notfall/res/drawable-xxhdpi/$NAME -w 144 -h 144

