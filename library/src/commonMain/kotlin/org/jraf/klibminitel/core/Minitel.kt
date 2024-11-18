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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.jraf.klibminitel.internal.protocol.Color
import org.jraf.klibminitel.internal.protocol.Control
import org.jraf.klibminitel.internal.protocol.Cursor
import org.jraf.klibminitel.internal.protocol.Cursor.HIDE_CURSOR
import org.jraf.klibminitel.internal.protocol.Cursor.MOVE_CURSOR_DOWN
import org.jraf.klibminitel.internal.protocol.Cursor.MOVE_CURSOR_LEFT
import org.jraf.klibminitel.internal.protocol.Cursor.MOVE_CURSOR_RIGHT
import org.jraf.klibminitel.internal.protocol.Cursor.MOVE_CURSOR_UP
import org.jraf.klibminitel.internal.protocol.Cursor.SHOW_CURSOR
import org.jraf.klibminitel.internal.protocol.Formatting
import org.jraf.klibminitel.internal.protocol.Graphics
import org.jraf.klibminitel.internal.protocol.Graphics.GRAPHICS_MODE_OFF
import org.jraf.klibminitel.internal.protocol.Graphics.GRAPHICS_MODE_ON
import org.jraf.klibminitel.internal.protocol.Screen.CLEAR_BOTTOM_OF_SCREEN
import org.jraf.klibminitel.internal.protocol.Screen.CLEAR_END_OF_LINE
import org.jraf.klibminitel.internal.protocol.Screen.CLEAR_SCREEN_AND_HOME
import org.jraf.klibminitel.internal.protocol.SpecialCharacters.replaceSpecialCharacters

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Minitel(
  private val keyboard: Source,
  private val screen: Sink,
) {
  constructor(filePath: String) : this(
    SystemFileSystem.source(Path(filePath)).buffered(),
    SystemFileSystem.sink(Path(filePath)).buffered(),
  )

  private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  suspend fun connect(block: suspend Connection.() -> Unit) {
    val keyboardChannel = Channel<KeyboardEvent>(10)
    val systemChannel = Channel<SystemEvent>(10)
    val screen = Screen(screen)
    val readLoopJob = coroutineScope.launch {
      readLoop(screen, systemChannel, keyboardChannel)
    }
    try {
      block(
        Connection(
          keyboard = keyboardChannel.receiveAsFlow(),
          system = systemChannel.receiveAsFlow(),
          screen = screen,
        ),
      )
    } finally {
      readLoopJob.cancel()
    }
  }

  private suspend fun readKeyboard(): Byte = withContext(Dispatchers.IO) { keyboard.readByte() }

  private suspend fun readLoop(
    screen: Screen,
    systemChannel: Channel<SystemEvent>,
    keyboardChannel: Channel<KeyboardEvent>,
  ) {
    while (true) {
      val read0 = readKeyboard()
      if (screen.isReadingCursorPosition) {
        // See https://jbellue.github.io/stum1b/#2-6-6-2
        val read1 = readKeyboard()
        val read2 = readKeyboard()
        val x = read2 - 0x41
        val y = read1 - 0x41
        screen.cursorPositionResultChannel.send(x to y)
        continue
      }

      if (read0 == FunctionKey.SEP) {
        // Here's the sequence we receive when turning on: SEP 0x59 SEP 0x53 SEP 0x54 (See https://jbellue.github.io/stum1b/#2-6-13-1)
        val read1 = readKeyboard()
        if (read1 == FunctionKey.CONNEXION_FIN.code) {
          val read2 = readKeyboard()
          if (read2 == FunctionKey.SEP) {
            val read3 = readKeyboard()
            if (read3 == FunctionKey.TURN_ON_2.code) {
              val read4 = readKeyboard()
              if (read4 == FunctionKey.SEP) {
                val read5 = readKeyboard()
                if (read5 == FunctionKey.TURN_ON_3.code) {
                  screen.reset()
                  systemChannel.send(SystemEvent.TurnedOnEvent)
                } else {
                  val functionKey = FunctionKey.fromCode(read5)
                  keyboardChannel.send(KeyboardEvent.FunctionKeyEvent(functionKey))
                }
              } else {
                keyboardChannel.send(KeyboardEvent.CharacterEvent(Char(read4.toInt())))
              }
            } else {
              val functionKey = FunctionKey.fromCode(read3)
              keyboardChannel.send(KeyboardEvent.FunctionKeyEvent(functionKey))
            }
          } else {
            keyboardChannel.send(KeyboardEvent.CharacterEvent(Char(read2.toInt())))
          }
        } else {
          val functionKey = FunctionKey.fromCode(read1)
          keyboardChannel.send(KeyboardEvent.FunctionKeyEvent(functionKey))
        }
      } else {
        keyboardChannel.send(KeyboardEvent.CharacterEvent(Char(read0.toInt())))
      }
    }
  }

  sealed class KeyboardEvent {
    data class CharacterEvent(val char: Char) : KeyboardEvent() {
      override fun toString(): String {
        return "$char (${@OptIn(ExperimentalStdlibApi::class) char.code.toByte().toHexString()})"
      }
    }

    data class FunctionKeyEvent(val functionKey: FunctionKey) : KeyboardEvent()
  }

  fun interface KeyboardListener {
    fun onKeyboardEvent(keyboardEvent: KeyboardEvent)
  }

  sealed class SystemEvent {
    data object TurnedOnEvent : SystemEvent()
  }

  fun interface SystemListener {
    fun onSystemEvent(systemEvent: SystemEvent)
  }

  class Connection(
    val keyboard: Flow<KeyboardEvent>,
    val system: Flow<SystemEvent>,
    val screen: Screen,
  )

  class Screen internal constructor(
    private val screen: Sink,
  ) {
    private var isCursorVisible: Boolean? = null
    private var isLocalEcho: Boolean? = null
    private var isScroll: Boolean? = null

    internal fun reset() {
      isCursorVisible = null
      isLocalEcho = null
      isScroll = null
    }

    internal var isReadingCursorPosition = false
    internal val cursorPositionResultChannel: Channel<Pair<Int, Int>> = Channel(1)

    private suspend fun writeToScreen(s: ByteArray) = withContext(Dispatchers.IO) {
      screen.apply {
        write(s)
        flush()
      }
    }

    private suspend fun writeToScreen(b: Byte) = withContext(Dispatchers.IO) {
      screen.apply {
        writeByte(b)
        flush()
      }
    }

    suspend fun raw(s: String): Int {
      writeToScreen(s.encodeToByteArray())
      return s.length
    }

    suspend fun raw(c: Char): Int {
      return raw("$c")
    }

    suspend fun print(s: String): Int {
      val replaced = s.replaceSpecialCharacters()
      writeToScreen(replaced.encodeToByteArray())
      return replaced.length
    }

    suspend fun print(c: Char): Int {
      return print("$c")
    }

    suspend fun clearScreenAndHome() {
      writeToScreen(CLEAR_SCREEN_AND_HOME)
    }

    suspend fun graphicsMode(graphicsMode: Boolean) {
      writeToScreen(if (graphicsMode) GRAPHICS_MODE_ON else GRAPHICS_MODE_OFF)
    }

    /**
     * Pass a value made of 3 rows of 2 bits each, from top to bottom, left to right.
     * For example, the value 0b00_11_00 will display the character ⠒, whereas 0b11_11_10 will display the character ⠟.
     */
    suspend fun graphicsCharacter(value: Byte) {
      writeToScreen(Graphics.graphicsCharacter(value))
    }

    suspend fun colorForeground(color0To7: Int) {
      writeToScreen(Color.colorForeground(color0To7))
    }

    suspend fun colorBackground(color0To7: Int) {
      writeToScreen(Color.colorBackground(color0To7))
    }

    /**
     * Note: the background color works only when printing at least one space.
     */
    suspend fun color(background0To7: Int, foreground0To7: Int) {
      colorBackground(background0To7)
      colorForeground(foreground0To7)
    }

    suspend fun inverse(inverse: Boolean) {
      writeToScreen(if (inverse) Color.INVERSE_ON else Color.INVERSE_OFF)
    }

    suspend fun blink(blink: Boolean) {
      writeToScreen(if (blink) Formatting.BLINK_ON else Formatting.BLINK_OFF)
    }

    suspend fun underline(underline: Boolean) {
      writeToScreen(if (underline) Formatting.UNDERLINE_ON else Formatting.UNDERLINE_OFF)
    }

    suspend fun characterSize(characterSize: CharacterSize) {
      writeToScreen(characterSize.characterSizeCode)
    }

    suspend fun showCursor(showCursor: Boolean) {
      if (isCursorVisible == showCursor) return
      isCursorVisible = showCursor
      writeToScreen(if (showCursor) SHOW_CURSOR else HIDE_CURSOR)
    }

    suspend fun moveCursor(x: Int, y: Int) {
      writeToScreen(Cursor.moveCursor(x, y))
    }

    suspend fun moveCursorLeft() {
      writeToScreen(MOVE_CURSOR_LEFT)
    }

    suspend fun moveCursorRight() {
      writeToScreen(MOVE_CURSOR_RIGHT)
    }

    suspend fun moveCursorUp() {
      writeToScreen(MOVE_CURSOR_UP)
    }

    suspend fun moveCursorDown() {
      writeToScreen(MOVE_CURSOR_DOWN)
    }

    suspend fun getCursorPosition(): Pair<Int, Int> {
      isReadingCursorPosition = true
      writeToScreen(Cursor.GET_CURSOR_POSITION)
      return try {
        cursorPositionResultChannel.receive()
      } finally {
        isReadingCursorPosition = false
      }
    }

    suspend fun clearEndOfLine() {
      writeToScreen(CLEAR_END_OF_LINE)
    }

    suspend fun clearBottomOfScreen() {
      writeToScreen(CLEAR_BOTTOM_OF_SCREEN)
    }

    suspend fun repeatCharacter(c: Char, times: Int) {
      writeToScreen(Control.repeatCharacter(c, times))
    }

    suspend fun repeatLastCharacter(times: Int) {
      writeToScreen(Control.repeatLastCharacter(times))
    }

    suspend fun localEcho(localEcho: Boolean) {
      if (isLocalEcho == localEcho) return
      isLocalEcho = localEcho
      writeToScreen(if (localEcho) Control.LOCAL_ECHO_ON else Control.LOCAL_ECHO_OFF)
    }

    suspend fun scroll(scroll: Boolean) {
      if (isScroll == scroll) return
      isScroll = scroll
      writeToScreen(if (scroll) Control.SCROLL_ON else Control.SCROLL_OFF)
    }

    suspend fun beep() {
      writeToScreen(Control.BEEP)
    }

    suspend fun disableAcknowledgement() {
      writeToScreen(Control.ACKNOWLEDGE_OFF)
    }
  }
}
