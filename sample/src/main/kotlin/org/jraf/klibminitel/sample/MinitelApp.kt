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

package org.jraf.klibminitel.sample

import org.jraf.klibminitel.core.FunctionKey
import org.jraf.klibminitel.core.Minitel
import org.jraf.klibminitel.core.SCREEN_HEIGHT_NORMAL
import org.jraf.klibminitel.core.SCREEN_WIDTH_NORMAL

class MinitelApp(filePath: String) {
  private val minitel = Minitel(filePath)

  enum class Mode {
    DRAWING,
    WAIT_FOR_INPUT,
  }

  private var mode = Mode.DRAWING

  private var input: String = ""
  private var buffer = mutableListOf<Line>()

  fun start() {
    logd("MinitelApp start")

    minitel.addReadListener { e ->
      logd("Read: ${e}")

      if (mode == Mode.WAIT_FOR_INPUT) {
        when (e) {
          is Minitel.ReadEvent.CharacterReadEvent -> {
            if (e.char.isISOControl()) return@addReadListener
            if (input.length >= SCREEN_WIDTH_NORMAL * 3) {
              minitel.beep()
              return@addReadListener
            }
            val c = e.char.invertCase()
            input += c
            minitel.print(c)

            minitel.showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
          }

          is Minitel.ReadEvent.FunctionKeyReadEvent -> {
            when (e.functionKey) {
              FunctionKey.CORRECTION -> {
                if (input.isNotEmpty()) {
                  input = input.dropLast(1)
                  minitel.moveCursorLeft()
                  minitel.clearEndOfLine()

                  minitel.showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
                }
              }

              FunctionKey.ENVOI -> {
                handleInput()
              }

              else -> {}
            }
          }
        }
      }
    }
    minitel.addSystemListener { e ->
      logd("System: ${e}")
      minitel.localEcho(false)
      drawScreen()
    }

    minitel.localEcho(false)
    drawScreen()
  }

  private fun drawScreen() {
    mode = Mode.DRAWING
    minitel.clearScreenAndHome()
    drawBuffer()
    drawInputWindow()
    drawInput()
    waitForInput()
  }

  private fun drawInputWindow() {
    minitel.moveCursor(0, SCREEN_HEIGHT_NORMAL - 3)
    minitel.colorWithInverse(5, 0)
    minitel.clearEndOfLine()

    minitel.moveCursorBottom()
    minitel.clearEndOfLine()

    minitel.moveCursorBottom()
    minitel.clearEndOfLine()
  }

  private fun drawInput() {
    minitel.moveCursor(0, SCREEN_HEIGHT_NORMAL - 3)
    minitel.colorWithInverse(5, 0)
    minitel.print(input)
    minitel.showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
  }

  private fun drawBuffer() {
    minitel.showCursor(false)
    minitel.moveCursor(0, 0)
    for (line in buffer.takeLast(SCREEN_HEIGHT_NORMAL - 3)) {
      minitel.colorForeground(line.color)
      minitel.print(line.text)
      minitel.clearEndOfLine()
      minitel.print("\r\n")
    }
  }

  private fun waitForInput() {
    mode = Mode.WAIT_FOR_INPUT
  }

  private fun handleInput() {
    logd("Input: $input")
    buffer += Line("<Me> $input", 3).splitIfTooLong(SCREEN_WIDTH_NORMAL)

    buffer += Line("<Bot> OK!", 7).splitIfTooLong(SCREEN_WIDTH_NORMAL)

    input = ""
    mode = Mode.DRAWING
    drawInputWindow()
    drawBuffer()
    drawInput()
    waitForInput()
  }
}

private fun Char.invertCase(): Char {
  return if (isLowerCase()) uppercaseChar() else lowercaseChar()
}

private fun Line.splitIfTooLong(maxLength: Int): List<Line> {
  return text.splitIfTooLong(maxLength).map { Line(it, color) }
}

private fun String.splitIfTooLong(maxLength: Int): List<String> {
  if (length <= maxLength) return listOf(this)

  val result = mutableListOf<String>()
  var current = ""
  for (word in split(" ")) {
    if (current.length + word.length + 1 > maxLength) {
      result += current
      current = ""
    }
    if (current.isNotEmpty()) current += " "
    current += word
  }
  if (current.isNotEmpty()) result += current

  return result
}

data class Line(
  val text: String,
  val color: Int,
)
