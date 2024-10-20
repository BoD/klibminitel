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

package org.jraf.klibminitel.core

import org.jraf.klibminitel.internal.codes.Color
import org.jraf.klibminitel.internal.codes.Control
import org.jraf.klibminitel.internal.codes.Cursor
import org.jraf.klibminitel.internal.codes.Cursor.HIDE_CURSOR
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_BOTTOM
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_LEFT
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_RIGHT
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_TOP
import org.jraf.klibminitel.internal.codes.Cursor.SHOW_CURSOR
import org.jraf.klibminitel.internal.codes.Formatting
import org.jraf.klibminitel.internal.codes.Graphics
import org.jraf.klibminitel.internal.codes.Graphics.GRAPHICS_MODE_OFF
import org.jraf.klibminitel.internal.codes.Graphics.GRAPHICS_MODE_ON
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_BOTTOM_OF_SCREEN
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_END_OF_LINE
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_SCREEN_AND_HOME
import org.jraf.klibminitel.internal.codes.SpecialCharacters.replaceSpecialCharacters
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.PrintWriter
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import kotlin.concurrent.thread

class Minitel(filePath: String) {
  private val fileOutputStream = FileOutputStream(filePath)
  private val output: PrintWriter = PrintWriter(fileOutputStream)
  private val input = FileInputStream(File(filePath))

  private val readListeners = mutableSetOf<ReadListener>()
  private val systemListeners = mutableSetOf<SystemListener>()

  private var isCursorVisible: Boolean? = null

  private var isBlink: Boolean? = null
  private var isUnderline: Boolean? = null

  private var isLocalEcho: Boolean? = null

  private var isScroll: Boolean? = null

  private var readAcknowledgements: Boolean = true

  private var skipRead = 0

  private var isReadingCursorPosition = false
  private var getCursorPositionBlockingQueue: BlockingQueue<Pair<Int, Int>> = LinkedBlockingDeque()

  init {
    startReadLoop()
  }

  private fun reset() {
    isCursorVisible = null
    isBlink = null
    isUnderline = null
    isLocalEcho = null
    isScroll = null
    readAcknowledgements = true
    isReadingCursorPosition = false
    skipRead = 0
  }

  private fun startReadLoop() {
    thread(name = "Minitel-read-loop") {
      while (true) {
        val read0 = input.read()
        if (isReadingCursorPosition) {
          // See https://jbellue.github.io/stum1b/#2-6-6-2
          val read1 = input.read()
          val read2 = input.read()
          if (read0 == -1 || read1 == -1 || read2 == -1) {
            getCursorPositionBlockingQueue.offer(-1 to -1)
            break
          }
          val x = read2 - 0x41
          val y = read1 - 0x41
          getCursorPositionBlockingQueue.offer(x to y)
          continue
        }

        if (read0 == -1) break

        if (skipRead > 0) {
          skipRead--
          continue
        }
        if (read0 == FunctionKey.SEP) {
          // Here's the sequence we receive when turning on: SEP 0x59 SEP 0x53 SEP 0x54 (See https://jbellue.github.io/stum1b/#2-6-13-1)
          val read1 = input.read()
          if (read1 == 0x59) {
            val read2 = input.read()
            if (read2 == FunctionKey.SEP) {
              val read3 = input.read()
              if (read3 == 0x53) {
                val read4 = input.read()
                if (read4 == FunctionKey.SEP) {
                  val read5 = input.read()
                  if (read5 == 0x54) {
                    reset()
                    dispatchSystemEvent(SystemEvent.TurnedOnEvent)
                  } else {
                    val functionKey = FunctionKey.fromCode(read5.toUByte())
                    dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey))
                  }
                } else {
                  dispatchReadEvent(ReadEvent.CharacterReadEvent(read4.toChar()))
                }
              } else {
                val functionKey = FunctionKey.fromCode(read3.toUByte())
                dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey))
              }
            } else {
              dispatchReadEvent(ReadEvent.CharacterReadEvent(read2.toChar()))
            }
          } else {
            val functionKey = FunctionKey.fromCode(read1.toUByte())
            dispatchReadEvent(ReadEvent.FunctionKeyReadEvent(functionKey))
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

  fun print(s: String): Int = out(s.replaceSpecialCharacters())
  fun print(c: Char): Int = out("$c".replaceSpecialCharacters())

  fun print(inputStream: InputStream) {
    inputStream.copyTo(fileOutputStream)
    fileOutputStream.flush()
  }

  fun clearScreenAndHome() {
    out(CLEAR_SCREEN_AND_HOME)
  }

  fun graphicsMode(graphicsMode: Boolean) {
    out(if (graphicsMode) GRAPHICS_MODE_ON else GRAPHICS_MODE_OFF)
  }

  /**
   * Pass a value made of 3 rows of 2 bits each, from top to bottom, left to right.
   * For example, the value 0b00_11_00 will display the character ⠒, whereas 0b11_11_10 will display the character ⠟.
   */
  fun graphicsCharacter(value: Int) {
    out(Graphics.graphicsCharacter(value))
  }

  fun colorForeground(color0To7: Int) {
    out(Color.colorForeground(color0To7))
  }

  fun colorBackground(color0To7: Int) {
    out(Color.colorBackground(color0To7))
  }

  /**
   * Weird behavior: the background color works only when printing at least one space.
   * This does not happen with inverse colors.
   * For this reason, prefer calling colorWithInverse() instead.
   */
  fun color(background0To7: Int, foreground0To7: Int) {
    colorBackground(background0To7)
    colorForeground(foreground0To7)
  }

  fun inverse(inverse: Boolean) {
    out(if (inverse) Color.INVERSE_ON else Color.INVERSE_OFF)
  }

  fun colorWithInverse(background0To7: Int, foreground0To7: Int) {
    inverse(true)
    color(foreground0To7, background0To7)
  }

  fun blink(blink: Boolean) {
    if (isBlink == blink) return
    isBlink = blink
    out(if (blink) Formatting.BLINK_ON else Formatting.BLINK_OFF)
  }

  fun underline(underline: Boolean) {
    if (isUnderline == underline) return
    isUnderline = underline
    out(if (underline) Formatting.UNDERLINE_ON else Formatting.UNDERLINE_OFF)
  }

  fun characterSize(characterSize: CharacterSize) {
    out(characterSize.characterSizeCode)
  }

  fun showCursor(showCursor: Boolean) {
    if (isCursorVisible == showCursor) return
    isCursorVisible = showCursor
    out(if (showCursor) SHOW_CURSOR else HIDE_CURSOR)
  }

  fun moveCursor(x: Int, y: Int) {
    out(Cursor.moveCursor(x, y))
  }

  fun moveCursorLeft() {
    out(MOVE_CURSOR_LEFT)
  }

  fun moveCursorRight() {
    out(MOVE_CURSOR_RIGHT)
  }

  fun moveCursorTop() {
    out(MOVE_CURSOR_TOP)
  }

  fun moveCursorBottom() {
    out(MOVE_CURSOR_BOTTOM)
  }

  fun getCursorPosition(): Pair<Int, Int> {
    isReadingCursorPosition = true
    out(Cursor.GET_CURSOR_POSITION)
    return try {
      getCursorPositionBlockingQueue.take()
    } finally {
      isReadingCursorPosition = false
    }
  }

  fun clearEndOfLine() {
    out(CLEAR_END_OF_LINE)
  }

  fun clearBottomOfScreen() {
    out(CLEAR_BOTTOM_OF_SCREEN)
  }

  fun repeatCharacter(c: Char, times: Int) {
    out(Control.repeatCharacter(c, times))
  }

  fun repeatLastCharacter(times: Int) {
    out(Control.repeatLastCharacter(times))
  }

  fun localEcho(localEcho: Boolean) {
    if (isLocalEcho == localEcho) return
    isLocalEcho = localEcho
    if (readAcknowledgements) {
      // We get 5 bytes back when we change the local echo setting, ignore them
      skipRead += 5
    }
    out(if (localEcho) Control.LOCAL_ECHO_ON else Control.LOCAL_ECHO_OFF)
  }

  fun scroll(scroll: Boolean) {
    if (isScroll == scroll) return
    isScroll = scroll
    if (readAcknowledgements) {
      // We get 4 bytes back when we change the scroll setting, ignore them
      skipRead += 4
    }
    out(if (scroll) Control.SCROLL_ON else Control.SCROLL_OFF)
  }

  fun beep() {
    out(Control.BEEP)
  }

  fun disableAcknowledgement() {
    if (!readAcknowledgements) return
    readAcknowledgements = false
    out(Control.ACKNOWLEDGE_OFF)
  }

  sealed class ReadEvent {
    data class CharacterReadEvent(val char: Char) : ReadEvent() {
      @OptIn(ExperimentalStdlibApi::class)
      override fun toString(): String {
        return "$char (${char.code.toByte().toHexString()})"
      }
    }

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
