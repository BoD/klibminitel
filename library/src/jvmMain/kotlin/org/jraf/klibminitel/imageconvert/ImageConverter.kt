/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2024-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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
import org.jraf.klibminitel.internal.protocol.Color.colorBackground
import org.jraf.klibminitel.internal.protocol.Color.colorForeground
import org.jraf.klibminitel.internal.protocol.Control.repeatCharacter
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.experimental.or

class ImageConverter(
  private val input: File,
  private val output: File,
  private val colorAlgorithm: ColorAlgorithm,
) {
  enum class ColorAlgorithm {
    Contrast,
    Accurate,
  }

  private data class MinitelCharacter(
    val character: Byte,
    val colors: Colors,
  ) {
    val isEmptyCharacter = character == 0.toByte() && colors.foreground.value == 0
  }

  private data class RepeatedMinitelCharacter(
    val character: MinitelCharacter,
    val repeated: Int,
  )

  private data class Colors(
    val background: Color,
    val foreground: Color,
  )

  @JvmInline
  private value class Color(
    val value: Int,
  ) {
    init {
      require(value in 0..7)
    }
  }

  private data class Pixel6Block(
    val y0x0: Color,
    val y0x1: Color,
    val y1x0: Color,
    val y1x1: Color,
    val y2x0: Color,
    val y2x1: Color,
  ) {
    val values get(): IntArray = intArrayOf(y0x0.value, y0x1.value, y1x0.value, y1x1.value, y2x0.value, y2x1.value)
  }

  fun convert() {
    val lines: List<List<MinitelCharacter>> = imageToLines()
    val output = output.outputStream()
    var curBg = -1
    var curFg = -1
    for (line in lines) {
      // Special case: empty line
      if (line.all { it.isEmptyCharacter }) {
        output.write(colorBackground(0))
        output.write(colorForeground(0))
        output.write(byteArrayOf('\r'.code.toByte(), '\n'.code.toByte()))
        continue
      }

      for (c in line.compressed()) {
        val bg = c.character.colors.background.value
        if (curBg != bg) {
          output.write(colorBackground(bg))
          curBg = bg
        }
        val fg = c.character.colors.foreground.value
        if (curFg != fg) {
          output.write(colorForeground(fg))
          curFg = fg
        }
        output.write(repeatCharacter((0x20 + c.character.character).toChar(), c.repeated))
      }
      if (line.size < SCREEN_WIDTH_NORMAL) {
        output.write(byteArrayOf('\r'.code.toByte(), '\n'.code.toByte()))
      }
    }
    output.flush()
    output.close()
  }

  private fun imageToLines(): List<List<MinitelCharacter>> {
    val image = ImageIO.read(input)!!
    var lines: List<List<MinitelCharacter>> = buildList {
      for (y in 0..<SCREEN_HEIGHT_NORMAL) {
        val line: List<MinitelCharacter> = buildList {
          for (x in 0..<SCREEN_WIDTH_NORMAL) {
            val block = Pixel6Block(
              image.color(x = x * 2, y = y * 3),
              image.color(x = x * 2 + 1, y = y * 3),

              image.color(x = x * 2, y = y * 3 + 1),
              image.color(x = x * 2 + 1, y = y * 3 + 1),

              image.color(x = x * 2, y = y * 3 + 2),
              image.color(x = x * 2 + 1, y = y * 3 + 2),
            )
            val colors = when (colorAlgorithm) {
              ColorAlgorithm.Accurate -> computeColorsAccurate(block)
              ColorAlgorithm.Contrast -> computeColorsContrast(block)
            }
            val avg = (colors.background.value + colors.foreground.value).toDouble() / 2.0
            var b: Byte = 0
            if (block.y0x0.value > avg) b = b or 0b00_00_00_01
            if (block.y0x1.value > avg) b = b or 0b00_00_00_10
            if (block.y1x0.value > avg) b = b or 0b00_00_01_00
            if (block.y1x1.value > avg) b = b or 0b00_00_10_00
            if (block.y2x0.value > avg) b = b or 0b00_01_00_00
            if (block.y2x1.value > avg) b = b or 0b00_10_00_00
            add(MinitelCharacter(b, colors))
          }
        }
        add(line)
      }
    }

    // Remove empty leading lines
    lines = lines.dropWhile { line -> line.all { it.isEmptyCharacter } }

    // Remove empty trailing lines
    lines = lines.dropLastWhile { line -> line.all { it.isEmptyCharacter } }

    // Remove empty trailing characters
    lines = lines.map { line ->
      line.dropLastWhile { it.isEmptyCharacter }
    }
    return lines
  }

  /**
   * Returns the color of the pixel at the given coordinates, as a value between 0 and 7, by taking the blue component of the RGB value and dividing it by 32 (since 256 / 32 = 8).
   */
  private fun BufferedImage.color(
    x: Int,
    y: Int,
  ): Color = Color((getRGB(x, y) and 0xFF) / 32)

  private fun computeColorsContrast(block: Pixel6Block): Colors {
    return Colors(background = Color(block.values.min()), foreground = Color(block.values.max()))
  }

  /**
   * Computes the background and foreground colors of the block by making a histogram of the values and splitting it in two parts,
   * one for the background and one for the foreground, and computing the weighted average of each part.
   * The split is done by finding the first and last non-empty values in the histogram, and splitting the histogram in the middle.
   */
  private fun computeColorsAccurate(block: Pixel6Block): Colors {
    // Make a histogram
    val histogram = IntArray(8)
    for (c in block.values) {
      histogram[c]++
    }
    val firstIdx = histogram.indexOfFirst { it != 0 }
    val lastIdx = histogram.indexOfLast { it != 0 }

    if (firstIdx == lastIdx) return Colors(background = Color(firstIdx), foreground = Color(firstIdx))

    val middleIdx = (lastIdx - firstIdx) / 2 + firstIdx
    val backgroundHistogram = buildList {
      for (i in firstIdx..<middleIdx) {
        add(i to histogram[i])
      }
    }
    val backgroundValue: Int = weightedAverage(backgroundHistogram)

    val foregroundHistogram = buildList {
      for (i in middleIdx..lastIdx) {
        add(i to histogram[i])
      }
    }
    val foregroundValue: Int = weightedAverage(foregroundHistogram)

    return Colors(background = Color(backgroundValue), foreground = Color(foregroundValue))
  }

  /**
   * Computes the weighted average of the values, where the first element of the pair is the value and the second element is the weight.
   * The result is rounded to the nearest integer.
   */
  private fun weightedAverage(values: List<Pair<Int, Int>>): Int {
    return (values.sumOf { it.first.toDouble() * it.second.toDouble() } / values.sumOf { it.second.toDouble() }).toInt()
  }

  private fun List<MinitelCharacter>.compressed(): List<RepeatedMinitelCharacter> {
    if (this.isEmpty()) return emptyList()
    val compressed = mutableListOf<RepeatedMinitelCharacter>()
    var current = RepeatedMinitelCharacter(this[0], 1)
    for (i in 1..<this.size) {
      val c = this[i]
      if (c == current.character) {
        current = current.copy(repeated = current.repeated + 1)
      } else {
        compressed.add(current)
        current = RepeatedMinitelCharacter(c, 1)
      }
    }
    compressed.add(current)
    return compressed
  }
}

fun main() {
  ImageConverter(
    // Input image must be 80x72, grayscale
    File("/Users/bod/Tmp/flag.png"),
    File("/Users/bod/Tmp/Untitled.vdt"),
    ImageConverter.ColorAlgorithm.Accurate,
  ).convert()
}
