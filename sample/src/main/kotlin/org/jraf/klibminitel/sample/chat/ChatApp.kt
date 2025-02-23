/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2025-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.klibminitel.sample.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.jraf.klibminitel.core.CharacterSize
import org.jraf.klibminitel.core.FunctionKey
import org.jraf.klibminitel.core.Minitel
import org.jraf.klibminitel.core.SCREEN_HEIGHT_NORMAL
import org.jraf.klibminitel.core.SCREEN_WIDTH_NORMAL
import org.jraf.klibminitel.sample.logd
import org.jraf.klibopenai.client.OpenAIClient
import org.jraf.klibopenai.client.configuration.ClientConfiguration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MinitelApp(
  private val filePath: String,
  authBearerToken: String,
) {
  private val openAIClient = OpenAIClient(
    ClientConfiguration(authBearerToken = authBearerToken),
  )

  enum class Mode {
    DRAWING,
    WAIT_FOR_INPUT,
  }

  private var mode = Mode.DRAWING

  private var input: String = ""
  private val buffer = mutableListOf<Line>()
  private val messages = mutableListOf<OpenAIClient.Message>()

  private var lastReadEvent = 0L

  private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  suspend fun start() {
    logd("MinitelApp start")
//    val minitel = Minitel(filePath)
    val minitel = Minitel(
      keyboard = System.`in`.asSource().buffered(),
      screen = System.out.asSink().buffered(),
    )
    minitel.connect {
      coroutineScope.launch {
        system.collect { e ->
          onSystemEvent(e, screen)
        }
      }

      // Clock
      coroutineScope.launch {
        while (true) {
          delay(System.currentTimeMillis() % 60_000)
          if (mode == Mode.WAIT_FOR_INPUT) {
            // Avoid moving the cursor while the user is typing
            if (System.currentTimeMillis() - lastReadEvent < 1_000) {
              continue
            }
            val savedCursorPosition = screen.getCursorPosition()
            screen.showCursor(false)
            screen.drawDateTime()

            // Go back to the input
            screen.moveCursor(savedCursorPosition.first, savedCursorPosition.second)
            screen.inverse(true)
            screen.color(background0To7 = 0, foreground0To7 = 5)
            screen.showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
          }
        }
      }

      screen.disableAcknowledgement()
      screen.localEcho(false)
      screen.drawScreen()

      keyboard.collect { e ->
        onKeyboardEvent(e, screen)
      }
    }
  }

  private suspend fun onSystemEvent(e: Minitel.SystemEvent, screen: Minitel.Screen) {
    logd("System: ${e}")
    when (e) {
      is Minitel.SystemEvent.TurnedOnEvent -> {
        screen.disableAcknowledgement()
        screen.localEcho(false)
        screen.drawScreen()
      }
    }
  }

  private suspend fun onKeyboardEvent(e: Minitel.KeyboardEvent, screen: Minitel.Screen) {
    logd("Keyboard: ${e}")
    lastReadEvent = System.currentTimeMillis()

    if (mode == Mode.WAIT_FOR_INPUT) {
      when (e) {
        is Minitel.KeyboardEvent.CharacterEvent -> {
          if (e.char.isISOControl()) return
          if (input.length >= SCREEN_WIDTH_NORMAL * 3) {
            screen.beep()
            return
          }
          val c = e.char.invertCase()
          input += c
          screen.print(c)

          screen.showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
        }

        is Minitel.KeyboardEvent.FunctionKeyEvent -> {
          when (e.functionKey) {
            FunctionKey.CORRECTION -> {
              if (input.isNotEmpty()) {
                input = input.dropLast(1)
                screen.moveCursorLeft()
                screen.clearEndOfLine()

                screen.showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
              }
            }

            FunctionKey.ENVOI -> {
              screen.handleInput()
            }

            FunctionKey.REPETITION -> {
              screen.drawScreen()
            }

            else -> {}
          }
        }
      }
    }
  }

  private suspend fun Minitel.Screen.drawScreen() {
    mode = Mode.DRAWING
    showCursor(false)
    clearScreenAndHome()
    drawHeader()
    drawDateTime()
    bufferCursor = 0
    drawBuffer()
    drawInputWindow()
    drawInput()
    waitForInput()
  }

  private suspend fun Minitel.Screen.drawHeader() {
    moveCursor(0, 1)
    color(background0To7 = 2, foreground0To7 = 5)
    characterSize(CharacterSize.TALL)
    print(" 3615 ")
    characterSize(CharacterSize.DOUBLE)
    print("Chat")
    characterSize(CharacterSize.TALL)
    print("!")
    clearEndOfLine()
  }

  private suspend fun Minitel.Screen.drawDateTime() {
    val date = getDate()
    moveCursor(SCREEN_WIDTH_NORMAL - date.length - 1, 0)
    color(background0To7 = 2, foreground0To7 = 0)
    print(date)

    val time = getTime()
    moveCursor(SCREEN_WIDTH_NORMAL - time.length - 1, 1)
    color(background0To7 = 2, foreground0To7 = 0)
    print(time)
  }

  private suspend fun Minitel.Screen.drawInputWindow() {
    moveCursor(0, SCREEN_HEIGHT_NORMAL - 3)
    inverse(true)
    color(background0To7 = 0, foreground0To7 = 5)
    clearEndOfLine()

    moveCursorDown()
    clearEndOfLine()

    moveCursorDown()
    clearEndOfLine()
  }

  private suspend fun Minitel.Screen.drawInput() {
    moveCursor(0, SCREEN_HEIGHT_NORMAL - 3)
    inverse(true)
    color(background0To7 = 0, foreground0To7 = 5)
    print(input)
    showCursor(input.length < SCREEN_WIDTH_NORMAL * 3)
  }

  private var bufferCursor = 0

  private suspend fun Minitel.Screen.drawBuffer() {
    showCursor(false)
    val bufferWindow = buffer.takeLast(SCREEN_HEIGHT_NORMAL - 3 - 2)
    if (bufferWindow.size < SCREEN_HEIGHT_NORMAL - 3 - 2) {
      val currentBufferCursor = bufferCursor
      for (i in bufferWindow.indices) {
        if (i >= currentBufferCursor) {
          moveCursor(0, i + 2)
          clearEndOfLine()
          val line = bufferWindow[i]
          colorForeground(if (line.isBot) 7 else 3)
          print(line.text)
          bufferCursor++
        }
      }
    } else {
      for (i in bufferWindow.indices.reversed()) {
        moveCursor(0, i + 2)
        clearEndOfLine()
        val line = bufferWindow[i]
        colorForeground(if (line.isBot) 7 else 3)
        print(line.text)
      }
    }
  }

  private fun waitForInput() {
    mode = Mode.WAIT_FOR_INPUT
  }

  private suspend fun Minitel.Screen.handleInput() {
    logd("Input: $input")
    val input = input
    this@MinitelApp.input = ""
    mode = Mode.DRAWING
    showCursor(false)
    drawInputWindow()

    messages += OpenAIClient.Message.User(input)
    buffer += Line(input, isBot = false).splitIfTooLong(SCREEN_WIDTH_NORMAL)

    val response = runBlocking {
      openAIClient.chatCompletion(
        model = "gpt-4o",
        systemMessage = getSystemMessage(),
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

private fun getSystemMessage() =
  "Tu es un bot Français qui tourne sur Minitel. Fais comme si on était le${getDate()}. Tes réponses doivent être courtes."

private fun getDate(): String = SimpleDateFormat("' 'dd/MM/yyyy").format(
  Calendar.getInstance()
    .apply { add(Calendar.YEAR, -30) }
    .time,
)

private fun getTime(): String = SimpleDateFormat("' 'HH:mm").format(Date())
