/*
 * Copyright (c) 2022 Tommy Ettinger.
 * The parent project is
 * https://github.com/tommyettinger/game-icons-net-atlas
 * Licensed identically to Game-Icons.net's images; for the list of contributors who must be credited, see:
 * https://github.com/tommyettinger/game-icons-net-atlas/blob/main/Game-Icons-License.txt
 */

package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PNG8;
import com.github.tommyettinger.anim8.QualityPalette;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Main extends ApplicationAdapter {
    //    public static final String MODE = "EMOJI_MID"; // run this first
//    public static final String MODE = "EMOJI_SMALL";
    public static final String MODE = "EMOJI_LARGE";
//    public static final String MODE = "EMOJI_HTML";
//    public static final String MODE = "FLAG";
//    public static final String MODE = "MODIFY_JSON";
//    public static final String MODE = "ALTERNATE_PALETTES";

//    public static final String TYPE = "color";
    public static final String TYPE = "black";
    public static final String RAW_DIR = "openmoji-72x72-" + TYPE;

    @Override
    public void create() {
        JsonReader reader = new JsonReader();
        if("MODIFY_JSON".equals(MODE)) {
            //To locate any names with non-ASCII chars in openmoji.json, use this regex:
            //"annotation": "[^"]*[^\u0000-\u007F][^"]*",
            //To locate any names with characters that could be a problem, use this regex (may need expanding):
            //"annotation": "[^"]*[^0-9a-zA-Z' ,!-][^"]*",
            //Might be useful for locating intermediate things that need replacement?
            //"annotation": "[^"]*[^0-9a-zA-Z' ,:\(\)!-][^"]*",
            JsonValue json = reader.parse(Gdx.files.internal("openmoji.json"));
            for (JsonValue entry = json.child; entry != null; entry = entry.next) {
                String name = removeAccents(entry.getString("annotation"))
                        .replace(':', ',').replace('“', '\'').replace('”', '\'').replace('’', '\'')
                        .replace(".", "").replace("&", "and");
                entry.addChild("name", new JsonValue(name));
                for(String s : new String[]{
                        "annotation","subgroups","tags","openmoji_tags","openmoji_author","openmoji_date",
                        "skintone","skintone_combination","skintone_base_emoji","skintone_base_hexcode","unicode"}){
                    entry.remove(s);
                }
            }

            Gdx.files.local("openmoji-ascii-names.json").writeString(json.toJson(JsonWriter.OutputType.json).replace("{", "\n{"), false);
        }
        else if("ALTERNATE_PALETTES".equals(MODE)) {
            FileHandle paletteDir = Gdx.files.local("../../alt-palette/");
            FileHandle[] paletteImages = paletteDir.list(".png");
            QualityPalette qp = new QualityPalette();
            PNG8 png = new PNG8();
            png.setCompression(7);
            png.setFlipY(false);
            for (FileHandle pi : paletteImages) {
                String paletteName = pi.nameWithoutExtension();
                System.out.println("Working on " + paletteName);
                FileHandle current = paletteDir.child(paletteName + "/");
                current.mkdirs();
                Pixmap pm = new Pixmap(pi);
                qp.exact(QualityPalette.colorsFrom(pm));
                pm.dispose();
                png.setDitherAlgorithm(Dithered.DitherAlgorithm.NONE);
                png.setDitherStrength(1f);
                png.setPalette(qp);
                Pixmap large = new Pixmap(Gdx.files.local("../../atlas/OpenMoji"+TYPE+".png"));
                png.write(current.child("atlas/OpenMoji"+TYPE+".png"), large, false, true);
                large.dispose();
                for (int i = 2; i <= 5; i++) {
                    Pixmap largeN = new Pixmap(Gdx.files.local("../../atlas/OpenMoji"+TYPE+i+".png"));
                    png.write(current.child("atlas/OpenMoji"+TYPE+i+".png"), largeN, false, true);
                    largeN.dispose();
                }
                Gdx.files.local("../../atlas/OpenMoji.atlas").copyTo(current.child("atlas"));
                Pixmap mid = new Pixmap(Gdx.files.local("../../atlas-mid/OpenMoji"+TYPE+".png"));
                png.write(current.child("atlas-mid/OpenMoji"+TYPE+".png"), mid, false, true);
                mid.dispose();
                Gdx.files.local("../../atlas-mid/OpenMoji"+TYPE+".atlas").copyTo(current.child("atlas-mid"));
                Pixmap small = new Pixmap(Gdx.files.local("../../atlas-small/OpenMoji"+TYPE+".png"));
                png.write(current.child("atlas-small/OpenMoji"+TYPE+".png"), small, false, true);
                small.dispose();
                Gdx.files.local("../../atlas-small/OpenMoji"+TYPE+".atlas").copyTo(current.child("atlas-small"));
            }
        }
        else if("EMOJI_MID".equals(MODE)) {
            JsonValue json = reader.parse(Gdx.files.internal("openmoji-ascii-names.json"));
            ObjectSet<String> used = new ObjectSet<>(json.size);
            for (JsonValue entry = json.child; entry != null; entry = entry.next) {
                String name = entry.getString("name");
                if(used.add(name)) {
                    String codename = entry.getString("hexcode");
                    String charString = entry.getString("emoji") + ".png";
                    entry.get("name").set(name);
                    name += ".png";
                    entry.remove("hexcode");
                    FileHandle original = Gdx.files.local("../../scaled-mid/" + codename + ".png");
                    if (original.exists()) {
                        original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-mid/emoji/" + charString));
                        original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-mid/name/" + name));
                    }
                    else {
                        original = Gdx.files.local("../../scaled-mid/" + codename + ".png");
                        if (original.exists()) {
                            original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-mid/emoji/" + charString));
                            original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-mid/name/" + name));
                        }
                    }
                } else {
                    entry.remove();
                }
            }
            Gdx.files.local("emoji-info.json").writeString(json.toJson(JsonWriter.OutputType.json).replace("{", "\n{"), false);
        }
        else if("EMOJI_SMALL".equals(MODE)) {
            JsonValue json = reader.parse(Gdx.files.internal("openmoji-ascii-names.json"));
            ObjectSet<String> used = new ObjectSet<>(json.size);
            for (JsonValue entry = json.child; entry != null; entry = entry.next) {
                String name = entry.getString("name");
                if(used.add(name)) {
                    String codename = entry.getString("hexcode");
                    String charString = entry.getString("emoji") + ".png";
                    name += ".png";
                    FileHandle original = Gdx.files.local("../../scaled-small/" + codename + ".png");
                    if (original.exists()) {
                        original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-small/emoji/" + charString));
                        original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-small/name/" + name));
                    }
                    else {
                        original = Gdx.files.local("../../scaled-small/" + codename + ".png");
                        if (original.exists()) {
                            original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-small/emoji/" + charString));
                            original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"-small/name/" + name));
                        }
                    }
                } else {
                    entry.remove();
                }
            }
        }
        else if("EMOJI_LARGE".equals(MODE)) {
            JsonValue json = reader.parse(Gdx.files.internal("openmoji-ascii-names.json"));
            ObjectSet<String> used = new ObjectSet<>(json.size);
            for (JsonValue entry = json.child; entry != null; entry = entry.next) {
                String name = entry.getString("name");
                if(used.add(name)) {
                    String codename = entry.getString("hexcode");
                    String charString = entry.getString("emoji") + ".png";
                    name += ".png";
                    FileHandle original = Gdx.files.local("../../"+RAW_DIR+"/" + codename + ".png");
                    if (original.exists()) {
                        if(!entry.getString("order", "").isEmpty())
                            original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"/emoji/" + charString));
                        original.copyTo(Gdx.files.local("../../renamed-"+TYPE+"/name/" + name));
                    }
                } else {
                    entry.remove();
                }
            }
        }
        else if("EMOJI_HTML".equals(MODE)) {
            JsonValue json = reader.parse(Gdx.files.internal("emoji-info.json"));
            StringBuilder sb = new StringBuilder(4096);
            sb.append("""
                    <!doctype html>
                    <html>
                    <head>
                    \t<title>OpenMoji Preview</title>
                    \t<meta http-equiv="content-type" content="text/html; charset=UTF-8">
                    \t<meta id="gameViewport" name="viewport" content="width=device-width initial-scale=1">
                    \t<link href="styles.css" rel="stylesheet" type="text/css">
                    </head>
                    
                    """);
            sb.append("<body>\n");
            sb.append("<h1>OpenMoji Preview</h1>\n");
            sb.append("<p>This shows all emoji supported by " +
                    "<a href=\"https://github.com/tommyettinger/openmoji-atlas\">OpenMojiAtlas</a>, " +
                    "along with the two names each can be looked up by.</p>\n");
            sb.append("<p>The atlases and all image assets are licensed under CC-BY-SA 4.0.</p>\n");
            sb.append("<p>Thanks to the entire <a href=\"https://openmoji.org/\">OpenMoji project</a>!</p>\n");
            sb.append("<div class=\"box\">\n");
            for (JsonValue entry = json.child; entry != null; entry = entry.next) {
                String emojiChar = entry.getString("emoji");
                String name = entry.getString("name");
                String emojiFile = "name/" + name + ".png";
                sb.append("\t<div class=\"item\">\n" +
                                "\t\t<img src=\"")
                        .append(emojiFile).append("\" alt=\"").append(name).append("\" />\n")
                        .append("\t\t<p>").append(emojiChar).append("</p>\n")
                        .append("\t\t<p>").append(name).append("</p>\n")
                        .append("\t</div>\n");
            }
            sb.append("</div>\n</body>\n");
            sb.append("</html>\n");
            Gdx.files.local("index.html").writeString(sb.toString(), false, "UTF8");
        }
        else if("FLAG".equals(MODE)) {
            JsonValue json = reader.parse(Gdx.files.internal("openmoji-ascii-names.json"));
            char[] buffer = new char[2];
            for (JsonValue entry = json.child; entry != null; entry = entry.next) {
                if(!"Flags (country-flag)".equals(entry.getString("category"))) continue;

                String codename = entry.getString("hexcode") + ".png";
                String charString = entry.getString("emoji") + ".png";
                String name = entry.getString("name");
                String countryUnicode = entry.getString("emoji");
                buffer[0] = (char)(countryUnicode.codePointAt(1) - 56806 + 'A');
                buffer[1] = (char)(countryUnicode.codePointAt(3) - 56806 + 'A');
                String countryCode = String.valueOf(buffer);
                FileHandle original = Gdx.files.local("../../scaled-tiny/" + codename);
                if (original.exists()) {
                    original.copyTo(Gdx.files.local("../../flags-tiny/emoji/" + charString));
                    original.copyTo(Gdx.files.local("../../flags-tiny/name/" + name));
                    original.copyTo(Gdx.files.local("../../flags-tiny/code/" + countryCode + ".png"));
                }
            }
        }
    }

    private static final Pattern diacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    /**
     * Removes accented characters from a string; if the "base" characters are non-English anyway then the result won't
     * be an ASCII string, but otherwise it probably will be.
     * <br>
     * This version can contain ligatures such as "æ" and "œ", but not with diacritics.
     * <br>
     * <a href="http://stackoverflow.com/a/1215117">Credit to StackOverflow user hashable</a>.
     *
     * @param str a string that may contain accented characters
     * @return a string with all accented characters replaced with their (possibly ASCII) counterparts
     */
    public static String removeAccents(String str) {
        String alteredString = Normalizer.normalize(str, Normalizer.Form.NFD);
        return diacritics.matcher(alteredString).replaceAll("");
    }

}