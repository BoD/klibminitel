/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jraf.klibminitel.escaping

import org.jraf.klibminitel.color.AwtColor
import org.jraf.klibminitel.color.rgbToHsl
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

// See http://millevaches.hydraule.org/info/minitel/specs/codes.htm

const val CLEAR_SCREEN_AND_HOME = '\u000C'

const val ESC = '\u001B'

private const val PARAM_SIZE_NORMAL = 'L'
private const val PARAM_SIZE_TALL = 'M'
private const val PARAM_SIZE_WIDE = 'N'
private const val PARAM_SIZE_DOUBLE = 'O'

const val SIZE_NORMAL = "$ESC$PARAM_SIZE_NORMAL"
const val SIZE_TALL = "$ESC$PARAM_SIZE_TALL"
const val SIZE_WIDE = "$ESC$PARAM_SIZE_WIDE"
const val SIZE_DOUBLE = "$ESC$PARAM_SIZE_DOUBLE"


const val COLOR_FOREGROUND_BLACK = "$ESC\u0040"
const val COLOR_FOREGROUND_RED = "$ESC\u0041"
const val COLOR_FOREGROUND_GREEN = "$ESC\u0042"
const val COLOR_FOREGROUND_YELLOW = "$ESC\u0043"
const val COLOR_FOREGROUND_BLUE = "$ESC\u0044"
const val COLOR_FOREGROUND_PURPLE = "$ESC\u0045"
const val COLOR_FOREGROUND_CYAN = "$ESC\u0046"
const val COLOR_FOREGROUND_WHITE = "$ESC\u0047"

const val COLOR_FOREGROUND_0 = COLOR_FOREGROUND_BLACK
const val COLOR_FOREGROUND_1 = COLOR_FOREGROUND_BLUE
const val COLOR_FOREGROUND_2 = COLOR_FOREGROUND_RED
const val COLOR_FOREGROUND_3 = COLOR_FOREGROUND_PURPLE
const val COLOR_FOREGROUND_4 = COLOR_FOREGROUND_GREEN
const val COLOR_FOREGROUND_5 = COLOR_FOREGROUND_CYAN
const val COLOR_FOREGROUND_6 = COLOR_FOREGROUND_YELLOW
const val COLOR_FOREGROUND_7 = COLOR_FOREGROUND_WHITE

fun colorForeground(lightness: Float): String {
    return when {
        lightness < 1F / 8F -> COLOR_FOREGROUND_0
        lightness < 2F / 8F -> COLOR_FOREGROUND_1
        lightness < 3F / 8F -> COLOR_FOREGROUND_2
        lightness < 4F / 8F -> COLOR_FOREGROUND_3
        lightness < 5F / 8F -> COLOR_FOREGROUND_4
        lightness < 6F / 8F -> COLOR_FOREGROUND_5
        lightness < 7F / 8F -> COLOR_FOREGROUND_6
        else -> COLOR_FOREGROUND_7
    }
}

fun colorForeground(color: AwtColor) = colorForeground(rgbToHsl(color).third)

fun colorBackground(lightness: Float): String {
    return when {
        lightness < 1F / 8F -> COLOR_BACKGROUND_0
        lightness < 2F / 8F -> COLOR_BACKGROUND_1
        lightness < 3F / 8F -> COLOR_BACKGROUND_2
        lightness < 4F / 8F -> COLOR_BACKGROUND_3
        lightness < 5F / 8F -> COLOR_BACKGROUND_4
        lightness < 6F / 8F -> COLOR_BACKGROUND_5
        lightness < 7F / 8F -> COLOR_BACKGROUND_6
        else -> COLOR_BACKGROUND_7
    }
}

fun colorBackground(color: AwtColor) = colorBackground(rgbToHsl(color).third)


const val COLOR_BACKGROUND_BLACK = "$ESC\u0050"
const val COLOR_BACKGROUND_RED = "$ESC\u0051"
const val COLOR_BACKGROUND_GREEN = "$ESC\u0052"
const val COLOR_BACKGROUND_YELLOW = "$ESC\u0053"
const val COLOR_BACKGROUND_BLUE = "$ESC\u0054"
const val COLOR_BACKGROUND_PURPLE = "$ESC\u0055"
const val COLOR_BACKGROUND_CYAN = "$ESC\u0056"
const val COLOR_BACKGROUND_WHITE = "$ESC\u0057"

const val COLOR_BACKGROUND_0 = COLOR_BACKGROUND_BLACK
const val COLOR_BACKGROUND_1 = COLOR_BACKGROUND_BLUE
const val COLOR_BACKGROUND_2 = COLOR_BACKGROUND_RED
const val COLOR_BACKGROUND_3 = COLOR_BACKGROUND_PURPLE
const val COLOR_BACKGROUND_4 = COLOR_BACKGROUND_GREEN
const val COLOR_BACKGROUND_5 = COLOR_BACKGROUND_CYAN
const val COLOR_BACKGROUND_6 = COLOR_BACKGROUND_YELLOW
const val COLOR_BACKGROUND_7 = COLOR_BACKGROUND_WHITE


const val UNDERLINE_START = "$ESC\u005A"
const val UNDERLINE_END = "$ESC\u0059"


const val ACCENT = '\u0019'
private const val ACCENT_GRAVE = "$ACCENT\u0041"
private const val ACCENT_ACUTE = "$ACCENT\u0042"
private const val ACCENT_CIRCUMFLEX = "$ACCENT\u0043"
private const val ACCENT_UMLAUT = "$ACCENT\u0048"

const val SPECIAL_CHAR_A_GRAVE = "${ACCENT_GRAVE}a"
const val SPECIAL_CHAR_E_GRAVE = "${ACCENT_GRAVE}e"
const val SPECIAL_CHAR_U_GRAVE = "${ACCENT_GRAVE}u"
const val SPECIAL_CHAR_E_ACUTE = "${ACCENT_ACUTE}e"
const val SPECIAL_CHAR_A_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}a"
const val SPECIAL_CHAR_E_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}e"
const val SPECIAL_CHAR_I_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}i"
const val SPECIAL_CHAR_O_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}o"
const val SPECIAL_CHAR_U_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}u"
const val SPECIAL_CHAR_E_UMLAUT = "${ACCENT_UMLAUT}e"


const val MOVE_CURSOR = '\u001F'
const val SHOW_CURSOR = '\u0011'
const val HIDE_CURSOR = '\u0014'


fun moveCursor(x: Int, y: Int): String = "$MOVE_CURSOR${(0x41 + y).toChar()}${(0x41 + x).toChar()}"

const val SCREEN_WIDTH_NORMAL = 40
const val SCREEN_WIDTH_TALL = SCREEN_WIDTH_NORMAL
const val SCREEN_WIDTH_WIDE = SCREEN_WIDTH_NORMAL / 2
const val SCREEN_WIDTH_DOUBLE = SCREEN_WIDTH_WIDE

const val SCREEN_HEIGHT_NORMAL = 24
const val SCREEN_HEIGHT_TALL = SCREEN_HEIGHT_NORMAL / 2
const val SCREEN_HEIGHT_WIDE = SCREEN_HEIGHT_NORMAL
const val SCREEN_HEIGHT_DOUBLE = SCREEN_HEIGHT_TALL

fun String.escapeAccents() = replace("à", SPECIAL_CHAR_A_GRAVE)
    .replace("è", SPECIAL_CHAR_E_GRAVE)
    .replace("ù", SPECIAL_CHAR_U_GRAVE)
    .replace("é", SPECIAL_CHAR_E_ACUTE)
    .replace("â", SPECIAL_CHAR_A_CIRCUMFLEX)
    .replace("ê", SPECIAL_CHAR_E_CIRCUMFLEX)
    .replace("î", SPECIAL_CHAR_I_CIRCUMFLEX)
    .replace("ô", SPECIAL_CHAR_O_CIRCUMFLEX)
    .replace("û", SPECIAL_CHAR_U_CIRCUMFLEX)
    .replace("ü", SPECIAL_CHAR_E_UMLAUT)

fun String.escapeSpecialChars() = replace("฿", " btc")
    .replace("€", " eur")
    .replace("☀", "*")
    .replace("\uD83C\uDF19", "(")
    .replace("☂", "///")
    .replace("☃", "oo")
    .replace("\uD83D\uDCA8", ">>")
    .replace("\uD83C\uDF2B", "##")
    .replace("☁", "#")
    .replace("\uD83C\uDF24", "*#")
    .replace("☁\uD83C\uDF19", "#(")
    .replace("▾", "min :")
    .replace("▴", "max :")
    .replace("º", "$ACCENT\u0030")
    .replace("’", "'")
    .replace("…", "...")
    .replace("”", "\"")

fun String.escapeHtml(defaultColor: String = COLOR_FOREGROUND_7, defaultSize: String = SIZE_TALL): String {
    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = documentBuilder.parse(InputSource(StringReader("<root>${this.replace("&", "&amp;")}</root>")))
    return escapeHtml(document.childNodes, defaultColor, defaultSize)
}

private fun escapeHtml(nodeList: NodeList, defaultColor: String, defaultSize: String): String {
    var res = ""
    for (i in 0 until nodeList.length) {
        val node = nodeList.item(i)
        res += when (node.nodeType) {
            Node.TEXT_NODE -> node.textContent

            else -> {
                val element = node as Element
                when (node.nodeName) {
                    "font" -> {
                        val color = element.getAttribute("color")
                        colorForeground(AwtColor.decode(color)) + escapeHtml(
                            node.childNodes,
                            defaultColor,
                            defaultSize
                        ) + defaultColor
                    }

                    "small" -> {
                        SIZE_NORMAL + escapeHtml(
                            node.childNodes,
                            defaultColor,
                            defaultSize
                        ) + defaultSize
                    }

                    else -> escapeHtml(node.childNodes, defaultColor, defaultSize)
                }
            }
        }
    }
    return res
}

fun String.getWidth(baseCharacterSize: CharacterSize): Int {
    var res = 0
    var i = 0
    var curCharWidth = baseCharacterSize.characterWidth
    while (i < length) {
        val c = this[i]
        when (c) {
            ESC -> {
                when (this[i + 1]) {
                    PARAM_SIZE_NORMAL -> curCharWidth = CharacterSize.NORMAL.characterWidth
                    PARAM_SIZE_TALL -> curCharWidth = CharacterSize.TALL.characterWidth
                    PARAM_SIZE_WIDE -> curCharWidth = CharacterSize.WIDE.characterWidth
                    PARAM_SIZE_DOUBLE -> curCharWidth = CharacterSize.DOUBLE.characterWidth
                }
                i += 2
            }

            ACCENT -> {
                res += curCharWidth
                i += 2
            }

            else -> {
                res += curCharWidth
                i++
            }
        }
    }
    return res
}