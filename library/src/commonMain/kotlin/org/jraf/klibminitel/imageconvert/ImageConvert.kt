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

package org.jraf.klibminitel.imageconvert

import org.jraf.klibminitel.core.SCREEN_HEIGHT_NORMAL
import org.jraf.klibminitel.core.SCREEN_WIDTH_NORMAL
import org.jraf.klibminitel.internal.codes.Color.colorBackground
import org.jraf.klibminitel.internal.codes.Color.colorForeground
import org.jraf.klibminitel.internal.codes.Cursor.moveCursor
import org.jraf.klibminitel.internal.codes.Graphics.GRAPHICS_MODE_ON
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_SCREEN_AND_HOME
import java.io.File
import javax.imageio.ImageIO

object App {
  fun convertImage(inputPath: String, outputPath: String, colorAlgorithm: ColorAlgorithm) {
    val image = ImageIO.read(File(inputPath))
    val lines = mutableListOf<MutableList<MinitelCharacter>>()

    for (y in 0 until SCREEN_HEIGHT_NORMAL) {
      val curLine = mutableListOf<MinitelCharacter>()
      lines += curLine
      for (x in 0 until SCREEN_WIDTH_NORMAL) {
        var char = 0
        val pixels6 = intArrayOf(
          image.getRGB(x * 2, y * 3) and 0xFF,
          image.getRGB(x * 2 + 1, y * 3) and 0xFF,
          image.getRGB(x * 2, y * 3 + 1) and 0xFF,
          image.getRGB(x * 2 + 1, y * 3 + 1) and 0xFF,
          image.getRGB(x * 2, y * 3 + 2) and 0xFF,
          image.getRGB(x * 2 + 1, y * 3 + 2) and 0xFF
        )
        val (bgColor, fgColor) = when (colorAlgorithm) {
          ColorAlgorithm.ACCURATE -> computeBgFgColorsAccurate(pixels6)
          ColorAlgorithm.CONTRAST -> computeBgFgColorsContrast(pixels6)
        }
        val avg = (bgColor + fgColor).toDouble() / 2.0

        if (pixels6[0] > avg) char += 1
        if (pixels6[1] > avg) char += 2
        if (pixels6[2] > avg) char += 4
        if (pixels6[3] > avg) char += 8
        if (pixels6[4] > avg) char += 16
        if (pixels6[5] > avg) char += 32

        curLine += MinitelCharacter(char, bgColor, fgColor)
      }
    }

    // Remove empty leading lines
    var emptyLeadingLines = 0
    val iter = lines.iterator()
    while (iter.hasNext()) {
      val line = iter.next()
      if (line.all { it.isEmptyCharacter }) {
        emptyLeadingLines++
        iter.remove()
      } else {
        break
      }
    }

    // Remove empty trailing lines
    for (i in lines.size - 1 downTo 0) {
      val line = lines[i]
      if (line.all { it.isEmptyCharacter }) {
        lines.removeAt(i)
      } else {
        break
      }
    }

    // Remove empty trailing characters
    for (line in lines) {
      for (i in line.size - 1 downTo 0) {
        val char = line[i]
        if (char.isEmptyCharacter) {
          line.removeAt(i)
        } else {
          break
        }
      }
    }

    val outputFile = File(outputPath)
    val output = outputFile.printWriter()
    output.print(CLEAR_SCREEN_AND_HOME)
    output.print(GRAPHICS_MODE_ON)

    var y = emptyLeadingLines - 1

    var curBg = -1
    var curFg = -1
    var needMoveCursor = false
    for (line in lines) {
      var x = 0
      y++

      // Remove empty leading characters
      while (line.isNotEmpty() && line[0].isEmptyCharacter) {
        line.removeAt(0)
        x++
      }

      if (line.isEmpty()) continue

      if (needMoveCursor) {
        output.print(moveCursor(x, y))
        output.print(GRAPHICS_MODE_ON)
      }
      for (c in line) {
        if (curBg != c.bgColor) {
          output.print(colorBackground(c.bgColor))
          curBg = c.bgColor
        }
        if (curFg != c.fgColor) {
          output.print(colorForeground(c.fgColor))
          curFg = c.fgColor
        }
        output.print((c.character + 32).toChar())
      }
      needMoveCursor = line.size != SCREEN_WIDTH_NORMAL
    }

    output.flush()
    output.close()
  }

  private fun computeBgFgColorsContrast(pixels6: IntArray): Pair<Int, Int> {
    return pixels6.min()!! to pixels6.max()!!
  }

  private fun computeBgFgColorsAccurate(pixels6: IntArray): Pair<Int, Int> {
    // Make a histogram
    val histogram = IntArray(8)
    for (c in pixels6) {
      histogram[c]++
    }

    val firstIdx = histogram.indexOfFirst { it != 0 }
    val lastIdx = histogram.indexOfLast { it != 0 }

    if (firstIdx == lastIdx) return firstIdx to firstIdx

    val middleIdx = (lastIdx - firstIdx) / 2 + firstIdx

    var bgSum = 0
    var bgTotal = 0
    for (i in firstIdx..middleIdx) {
      val histValue = histogram[i]
      bgSum += (i - firstIdx) * histValue
      bgTotal++
    }
    val bgColor = bgSum / bgTotal + firstIdx

    var fgSum = 0
    var fgTotal = 0
    for (i in middleIdx + 1..lastIdx) {
      val histValue = histogram[i]
      fgSum += (i - (middleIdx + 1)) * histValue
      fgTotal++
    }
    val fgColor = fgSum / fgTotal + middleIdx + 1
    return Pair(bgColor, fgColor)
  }
}

data class MinitelCharacter(
    val character: Int,
    val bgColor: Int,
    val fgColor: Int,
) {
  val isEmptyCharacter = character == 0 && fgColor == 0
}

enum class ColorAlgorithm {
  ACCURATE,
  CONTRAST
}

fun main() = App.convertImage(
  "/Users/bod/Tmp/smallq.png",
  "/Users/bod/Tmp/smallq.minitel",
  ColorAlgorithm.CONTRAST
)
