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

import kotlinx.coroutines.runBlocking
import org.jraf.klibminitel.core.CharacterSize
import org.jraf.klibminitel.core.FunctionKey
import org.jraf.klibminitel.core.Minitel
import org.jraf.klibminitel.core.SCREEN_HEIGHT_NORMAL
import org.jraf.klibminitel.core.SCREEN_WIDTH_NORMAL
import org.jraf.klibopenai.client.OpenAIClient
import org.jraf.klibopenai.client.configuration.ClientConfiguration

class MinitelApp(
  filePath: String,
  authBearerToken: String,
) {
  private val minitel = Minitel(filePath)
  private val openAIClient = OpenAIClient(
    ClientConfiguration(authBearerToken = authBearerToken)
  )

  enum class Mode {
    DRAWING,
    WAIT_FOR_INPUT,
  }

  private var mode = Mode.DRAWING

  private var input: String = ""
  private val buffer = mutableListOf<Line>()
  private val messages = mutableListOf<OpenAIClient.Message>()

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
    drawHeader()
    drawBuffer()
    drawInputWindow()
    drawInput()
    waitForInput()
  }

  private fun drawHeader() {
    minitel.moveCursor(0, 1)
    minitel.colorWithInverse(2, 5)
    minitel.characterSize(CharacterSize.TALL)
    minitel.print(" 3615 ")
    minitel.characterSize(CharacterSize.DOUBLE)
    minitel.print("Chat")
    minitel.characterSize(CharacterSize.TALL)
    minitel.print("!")
    minitel.clearEndOfLine()
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

  private var bufferCursor = 0

  private fun drawBuffer() {
    minitel.showCursor(false)
    val bufferWindow = buffer.takeLast(SCREEN_HEIGHT_NORMAL - 3 - 2)
    if (bufferWindow.size < SCREEN_HEIGHT_NORMAL - 3 - 2) {
      val currentBufferCursor = bufferCursor
      for (i in bufferWindow.indices) {
        if (i >= currentBufferCursor) {
          minitel.moveCursor(0, i + 2)
          minitel.clearEndOfLine()
          val line = bufferWindow[i]
          minitel.colorForeground(if (line.isBot) 7 else 3)
          minitel.print(line.text)
          bufferCursor++
        }
      }
    } else {
      for (i in bufferWindow.indices.reversed()) {
        minitel.moveCursor(0, i + 2)
        minitel.clearEndOfLine()
        val line = bufferWindow[i]
        minitel.colorForeground(if (line.isBot) 7 else 3)
        minitel.print(line.text)
      }
    }
  }

  private fun waitForInput() {
    mode = Mode.WAIT_FOR_INPUT
  }

  private fun handleInput() {
    logd("Input: $input")
    messages += OpenAIClient.Message.User(input)
    buffer += Line(input, isBot = false).splitIfTooLong(SCREEN_WIDTH_NORMAL)

    val response = runBlocking {
      openAIClient.chatCompletion(
        model = "gpt-4o",
        systemMessage = SYSTEM_MESSAGE,
        messages = messages,
      )
    } ?: "Error! Check the logs"

    logd("OpenAI response: $response")
    messages += OpenAIClient.Message.Assistant(response)
    buffer += response.split('\n').map { Line(it, isBot = true) }.flatMap {
      it.splitIfTooLong(SCREEN_WIDTH_NORMAL)
    }

    val lastMessages = messages.takeLast(50)
    messages.clear()
    messages.addAll(lastMessages)

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
  return text.splitIfTooLong(maxLength).map { copy(text = it) }
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
  val isBot: Boolean,
)

private const val SYSTEM_MESSAGE =
  "Tu es un bot Français qui tourne sur Minitel. Fais comme si on était au début des années 90. Tes réponses doivent être courtes."
