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

Atlases are available for 24x24 (small) and 32x32 (mid) sizes of OpenMoji. They come in (currently) full-color and
black-line-only versions. You can choose from [atlas-small-black](atlas-small-black/), [atlas-small-color](atlas-small-color/),
[atlas-mid-black](atlas-mid-black/), and [atlas-mid-color](atlas-mid-color/). The black-line-only versions can't be
recolored using a typical SpriteBatch with its default shader; I'm working on white-line-only versions.

# License

[CC-BY-SA 4.0](LICENSE.txt).