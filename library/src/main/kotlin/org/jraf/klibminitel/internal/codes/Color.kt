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

package org.jraf.klibminitel.internal.codes

import org.jraf.klibminitel.internal.util.color.AwtColor
import org.jraf.klibminitel.internal.util.color.rgbToHsl

internal object Color {
    const val COLOR_FOREGROUND_BLACK = "${Codes.ESC}\u0040"
    const val COLOR_FOREGROUND_RED = "${Codes.ESC}\u0041"
    const val COLOR_FOREGROUND_GREEN = "${Codes.ESC}\u0042"
    const val COLOR_FOREGROUND_YELLOW = "${Codes.ESC}\u0043"
    const val COLOR_FOREGROUND_BLUE = "${Codes.ESC}\u0044"
    const val COLOR_FOREGROUND_PURPLE = "${Codes.ESC}\u0045"
    const val COLOR_FOREGROUND_CYAN = "${Codes.ESC}\u0046"
    const val COLOR_FOREGROUND_WHITE = "${Codes.ESC}\u0047"

    const val COLOR_FOREGROUND_0 = COLOR_FOREGROUND_BLACK
    const val COLOR_FOREGROUND_1 = COLOR_FOREGROUND_BLUE
    const val COLOR_FOREGROUND_2 = COLOR_FOREGROUND_RED
    const val COLOR_FOREGROUND_3 = COLOR_FOREGROUND_PURPLE
    const val COLOR_FOREGROUND_4 = COLOR_FOREGROUND_GREEN
    const val COLOR_FOREGROUND_5 = COLOR_FOREGROUND_CYAN
    const val COLOR_FOREGROUND_6 = COLOR_FOREGROUND_YELLOW
    const val COLOR_FOREGROUND_7 = COLOR_FOREGROUND_WHITE

    const val COLOR_BACKGROUND_BLACK = "${Codes.ESC}\u0050"
    const val COLOR_BACKGROUND_RED = "${Codes.ESC}\u0051"
    const val COLOR_BACKGROUND_GREEN = "${Codes.ESC}\u0052"
    const val COLOR_BACKGROUND_YELLOW = "${Codes.ESC}\u0053"
    const val COLOR_BACKGROUND_BLUE = "${Codes.ESC}\u0054"
    const val COLOR_BACKGROUND_PURPLE = "${Codes.ESC}\u0055"
    const val COLOR_BACKGROUND_CYAN = "${Codes.ESC}\u0056"
    const val COLOR_BACKGROUND_WHITE = "${Codes.ESC}\u0057"

    const val COLOR_BACKGROUND_0 = COLOR_BACKGROUND_BLACK
    const val COLOR_BACKGROUND_1 = COLOR_BACKGROUND_BLUE
    const val COLOR_BACKGROUND_2 = COLOR_BACKGROUND_RED
    const val COLOR_BACKGROUND_3 = COLOR_BACKGROUND_PURPLE
    const val COLOR_BACKGROUND_4 = COLOR_BACKGROUND_GREEN
    const val COLOR_BACKGROUND_5 = COLOR_BACKGROUND_CYAN
    const val COLOR_BACKGROUND_6 = COLOR_BACKGROUND_YELLOW
    const val COLOR_BACKGROUND_7 = COLOR_BACKGROUND_WHITE


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

    fun colorForeground(color0To7: Int): String {
        return when (color0To7) {
            0 -> COLOR_FOREGROUND_0
            1 -> COLOR_FOREGROUND_1
            2 -> COLOR_FOREGROUND_2
            3 -> COLOR_FOREGROUND_3
            4 -> COLOR_FOREGROUND_4
            5 -> COLOR_FOREGROUND_5
            6 -> COLOR_FOREGROUND_6
            else -> COLOR_FOREGROUND_7
        }
    }

    fun colorForeground(awtColor: AwtColor): String = colorForeground(rgbToHsl(awtColor).third)

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

    fun colorBackground(color0To7: Int): String {
        return when (color0To7) {
            0 -> COLOR_BACKGROUND_0
            1 -> COLOR_BACKGROUND_1
            2 -> COLOR_BACKGROUND_2
            3 -> COLOR_BACKGROUND_3
            4 -> COLOR_BACKGROUND_4
            5 -> COLOR_BACKGROUND_5
            6 -> COLOR_BACKGROUND_6
            else -> COLOR_BACKGROUND_7
        }
    }

    fun colorBackground(awtColor: AwtColor): String =
        colorBackground(org.jraf.klibminitel.internal.util.color.rgbToHsl(awtColor).third)

}