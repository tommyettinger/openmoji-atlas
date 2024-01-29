# openmoji-atlas
Texture atlases for the [OpenMoji](https://openmoji.org/) emoji set; [libGDX](https://libgdx.com/)-compatible.

# What is it?

Texture atlases are a convenient and efficient way of accessing any of a large number of images without incurring
performance penalties due to texture swaps. In libGDX games, texture atlases tend to be used heavily, but creating
them can be a hassle (especially for large atlases). The [OpenMoji](https://openmoji.org/) emoji are a nicely-designed
set of consistent emoji with wide coverage for Unicode (including the latest standard at the time of writing, 15.0),
as well as some extra special-use emoji coverage for symbols not in Unicode.

This project exists to put the OpenMoji emoji into texture atlasees so games can use them more easily. This includes
games that use [TextraTypist](https://github.com/tommyettinger/textratypist/), which can load emoji atlases as a main
feature. This project also does some work to resize the initially-72x72 OpenMoji images to 32x32, 24x24, or even 16x16.
This involved thickening lines and sharpening blur on resize, and should produce more legible emoji at small sizes
compared to naively scaling down with a default filter.

# How do I get it?

Atlases are available for 24x24 (small) and 32x32 (mid) sizes of OpenMoji. They come in full-color, black-line-only, and
white-line-only versions. For the atlases that include all OpenMoji, including those that don't have a Unicode emoji to
represent them, you can choose from [atlas-small-color](expanded/atlas-small-color/), [atlas-small-black](expanded/atlas-small-black/),
[atlas-small-white](expanded/atlas-small-white/), [atlas-mid-color](expanded/atlas-mid-color/), [atlas-mid-black](expanded/atlas-mid-black/), and
[atlas-mid-white](expanded/atlas-mid-white/). The black-line-only versions can't be recolored using a typical SpriteBatch with
its default shader, but the white-line-only versions can be colored normally using `Batch#setColor(Color)`. The
full-color versions sometimes use larger textures than the black or white line versions, but this is because the line
versions show almost all flags identically (as an empty rectangle), and those get merged in the atlas. The full-color
version has many flags in relatively high detail. The white-line-only version describes the names of colors as if black
describes the current color of the emoji, so `⚫️`, or `black circle`, will draw as a circle with white fill if the Batch
color is white, a red circle if the Batch color is red, or a black circle of the Batch color is black, for example.

# License

[CC-BY-SA 4.0](LICENSE.txt).