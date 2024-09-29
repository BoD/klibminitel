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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.jraf.klibminitel.core

import org.jraf.klibminitel.internal.codes.Character
import org.jraf.klibminitel.internal.codes.Character.GRAPHICS_MODE_OFF
import org.jraf.klibminitel.internal.codes.Character.GRAPHICS_MODE_ON
import org.jraf.klibminitel.internal.codes.Color
import org.jraf.klibminitel.internal.codes.Control
import org.jraf.klibminitel.internal.codes.Cursor
import org.jraf.klibminitel.internal.codes.Cursor.HIDE_CURSOR
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_BOTTOM
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_LEFT
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_RIGHT
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_TOP
import org.jraf.klibminitel.internal.codes.Cursor.SHOW_CURSOR
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_BOTTOM_OF_SCREEN
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_END_OF_LINE
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_SCREEN_AND_HOME
import org.jraf.klibminitel.internal.codes.escapeAccents
import org.jraf.klibminitel.internal.codes.escapeSpecialChars
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.PrintWriter
import kotlin.concurrent.thread

class Minitel(filePath: String) {
  private val fileOutputStream = FileOutputStream(filePath)
  private val output: PrintWriter = PrintWriter(fileOutputStream)
  private val input = FileInputStream(File(filePath))

  private val readListeners = mutableSetOf<ReadListener>()
  private val systemListeners = mutableSetOf<SystemListener>()

  init {
    startReadLoop()
  }

  private var skipRead = 0

  private fun startReadLoop() {
    thread(name = "Minitel-read-loop") {
      while (true) {
        val read0 = input.read()
        if (read0 == -1) break
        if (skipRead > 0) {
          skipRead--
          continue
        }
        if (read0 == FunctionKey.CONTROL_KEY_ESCAPE) {
          val read1 = input.read()
          val functionKey = FunctionKey.fromCode(read1)
          dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey))
        } else if (read0 == '~'.code) {
          // Here's the sequence we receive when turning on: ~ ESC CONNEXION_FIN ESC 83 ESC 84
          val read1 = input.read()
          if (read1 == FunctionKey.CONTROL_KEY_ESCAPE) {
            val read2 = input.read()
            val functionKey2 = FunctionKey.fromCode(read2)
            if (functionKey2 == FunctionKey.CONNEXION_FIN) {
              val read3 = input.read()
              if (read3 == FunctionKey.CONTROL_KEY_ESCAPE) {
                val read4 = input.read()
                val functionKey4 = FunctionKey.fromCode(read4)
                if (functionKey4 == FunctionKey.UNKNOWN(83)) {
                  val read5 = input.read()
                  if (read5 == FunctionKey.CONTROL_KEY_ESCAPE) {
                    val read6 = input.read()
                    val functionKey6 = FunctionKey.fromCode(read6)
                    if (functionKey6 == FunctionKey.UNKNOWN(84)) {
                      dispatchSystemEvent(SystemEvent.TurnedOnEvent)
                    } else {
                      dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
                      dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey2))
                      dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey4))
                      dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey6))
                    }
                  } else {
                    dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
                    dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey2))
                    dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey4))
                    dispatchReadEvent(ReadEvent.CharacterReadEvent(read5.toChar()))
                  }
                } else {
                  dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
                  dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey2))
                  dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey4))
                }
              } else {
                dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
                dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey2))
                dispatchReadEvent(ReadEvent.CharacterReadEvent(read3.toChar()))
              }
            } else {
              dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
              dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey2))
            }
          } else {
            dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
            dispatchReadEvent(ReadEvent.CharacterReadEvent(read1.toChar()))
          }
        } else {
          dispatchReadEvent(ReadEvent.CharacterReadEvent(read0.toChar()))
        }
      }
    }
  }

  fun addReadListener(listener: ReadListener) {
    readListeners += listener
  }

  fun removeReadListener(listener: ReadListener) {
    readListeners -= listener
  }

  private fun dispatchReadEvent(readEvent: ReadEvent) {
    for (it in readListeners) {
      it.onReadEvent(readEvent)
    }
  }

  fun addSystemListener(listener: SystemListener) {
    systemListeners += listener
  }

  fun removeSystemListener(listener: SystemListener) {
    systemListeners -= listener
  }

  private fun dispatchSystemEvent(systemEvent: SystemEvent) {
    for (it in systemListeners) {
      it.onSystemEvent(systemEvent)
    }
  }

  private fun out(s: String): Int {
    output.apply {
      print(s)
      flush()
    }
    return s.length
  }

  private fun out(c: Char): Int {
    output.apply {
      print(c)
      flush()
    }
    return 1
  }

  fun print(s: String): Int = out(s.escapeAccents().escapeSpecialChars())
  fun print(c: Char): Int = out(c)

  fun print(inputStream: InputStream) {
    inputStream.copyTo(fileOutputStream)
    fileOutputStream.flush()
  }

  fun clearScreenAndHome() = out(CLEAR_SCREEN_AND_HOME)
  fun graphicsMode(on: Boolean) = out(if (on) GRAPHICS_MODE_ON else GRAPHICS_MODE_OFF)
  fun moveCursor(x: Int, y: Int) = out(Cursor.moveCursor(x, y))
  fun showCursor(visible: Boolean) = out(if (visible) SHOW_CURSOR else HIDE_CURSOR)
  fun colorForeground(color: Int) = out(Color.colorForeground(color))
  fun colorBackground(color: Int) = out(Color.colorBackground(color))
  fun color(background: Int, foreground: Int) {
    colorBackground(background)
    colorForeground(foreground)
  }

  fun characterSize(characterSize: CharacterSize) = out(characterSize.characterSizeCode)

  fun moveCursorLeft() = out(MOVE_CURSOR_LEFT)
  fun moveCursorRight() = out(MOVE_CURSOR_RIGHT)
  fun moveCursorTop() = out(MOVE_CURSOR_TOP)
  fun moveCursorBottom() = out(MOVE_CURSOR_BOTTOM)

  fun clearEndOfLine() = out(CLEAR_END_OF_LINE)
  fun clearBottomOfScreen() = out(CLEAR_BOTTOM_OF_SCREEN)

  fun repeatCharacter(c: Char, times: Int) = out(Control.repeatCharacter(c, times))
  fun repeatLastCharacter(times: Int) = out(Control.repeatLastCharacter(times))

  fun localEcho(localEcho: Boolean): Int {
    // We get 5 characters back when we change the local echo setting, ignore them
    skipRead += 5
    return out(if (localEcho) Control.LOCAL_ECHO_ON else Control.LOCAL_ECHO_OFF)
  }

  fun graphicsCharacter(value: Int, alreadyInGraphicsMode: Boolean = false) = out(
    Character.graphicsCharacter(
      value,
      alreadyInGraphicsMode
    )
  )

  sealed class ReadEvent {
    data class CharacterReadEvent(val char: Char) : ReadEvent()
    data class FunctionKeyReadEvent(val functionKey: FunctionKey) : ReadEvent()
  }

  fun interface ReadListener {
    fun onReadEvent(readEvent: ReadEvent)
  }

  sealed class SystemEvent {
    data object TurnedOnEvent : SystemEvent()
  }

  fun interface SystemListener {
    fun onSystemEvent(systemEvent: SystemEvent)
  }
}
