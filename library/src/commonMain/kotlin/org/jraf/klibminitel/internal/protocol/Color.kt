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

package org.jraf.klibminitel.internal.protocol

import org.jraf.klibminitel.internal.protocol.Control.ESC
import org.jraf.klibminitel.internal.util.color.AwtColor
import org.jraf.klibminitel.internal.util.color.rgbToHsl

// See https://jbellue.github.io/stum1b/#2-2-1-2-4-2
internal object Color {
  val COLOR_FOREGROUND_BLACK = byteArrayOf(ESC, 0x40)
  val COLOR_FOREGROUND_RED = byteArrayOf(ESC, 0x41)
  val COLOR_FOREGROUND_GREEN = byteArrayOf(ESC, 0x42)
  val COLOR_FOREGROUND_YELLOW = byteArrayOf(ESC, 0x43)
  val COLOR_FOREGROUND_BLUE = byteArrayOf(ESC, 0x44)
  val COLOR_FOREGROUND_PURPLE = byteArrayOf(ESC, 0x45)
  val COLOR_FOREGROUND_CYAN = byteArrayOf(ESC, 0x46)
  val COLOR_FOREGROUND_WHITE = byteArrayOf(ESC, 0x47)

  val COLOR_FOREGROUND_0 = COLOR_FOREGROUND_BLACK
  val COLOR_FOREGROUND_1 = COLOR_FOREGROUND_BLUE
  val COLOR_FOREGROUND_2 = COLOR_FOREGROUND_RED
  val COLOR_FOREGROUND_3 = COLOR_FOREGROUND_PURPLE
  val COLOR_FOREGROUND_4 = COLOR_FOREGROUND_GREEN
  val COLOR_FOREGROUND_5 = COLOR_FOREGROUND_CYAN
  val COLOR_FOREGROUND_6 = COLOR_FOREGROUND_YELLOW
  val COLOR_FOREGROUND_7 = COLOR_FOREGROUND_WHITE

  val COLOR_BACKGROUND_BLACK = byteArrayOf(ESC, 0x50)
  val COLOR_BACKGROUND_RED = byteArrayOf(ESC, 0x51)
  val COLOR_BACKGROUND_GREEN = byteArrayOf(ESC, 0x52)
  val COLOR_BACKGROUND_YELLOW = byteArrayOf(ESC, 0x53)
  val COLOR_BACKGROUND_BLUE = byteArrayOf(ESC, 0x54)
  val COLOR_BACKGROUND_PURPLE = byteArrayOf(ESC, 0x55)
  val COLOR_BACKGROUND_CYAN = byteArrayOf(ESC, 0x56)
  val COLOR_BACKGROUND_WHITE = byteArrayOf(ESC, 0x57)

  val COLOR_BACKGROUND_0 = COLOR_BACKGROUND_BLACK
  val COLOR_BACKGROUND_1 = COLOR_BACKGROUND_BLUE
  val COLOR_BACKGROUND_2 = COLOR_BACKGROUND_RED
  val COLOR_BACKGROUND_3 = COLOR_BACKGROUND_PURPLE
  val COLOR_BACKGROUND_4 = COLOR_BACKGROUND_GREEN
  val COLOR_BACKGROUND_5 = COLOR_BACKGROUND_CYAN
  val COLOR_BACKGROUND_6 = COLOR_BACKGROUND_YELLOW
  val COLOR_BACKGROUND_7 = COLOR_BACKGROUND_WHITE

  val INVERSE_ON = byteArrayOf(ESC, 0x5D)
  val INVERSE_OFF = byteArrayOf(ESC, 0x5C)

  fun colorForeground(lightness: Float): ByteArray {
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

  fun colorForeground(color0To7: Int): ByteArray {
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

  fun colorForeground(awtColor: AwtColor): ByteArray = colorForeground(rgbToHsl(awtColor).third)

  fun colorBackground(lightness: Float): ByteArray {
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

  fun colorBackground(color0To7: Int): ByteArray {
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

  fun colorBackground(awtColor: AwtColor): ByteArray =
    colorBackground(rgbToHsl(awtColor).third)
}
