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
import org.jraf.klibminitel.internal.codes.Cursor
import org.jraf.klibminitel.internal.codes.Cursor.HIDE_CURSOR
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_BOTTOM
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_LEFT
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_RIGHT
import org.jraf.klibminitel.internal.codes.Cursor.MOVE_CURSOR_TOP
import org.jraf.klibminitel.internal.codes.Cursor.SHOW_CURSOR
import org.jraf.klibminitel.internal.codes.Misc
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_BOTTOM_OF_SCREEN
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_END_OF_LINE
import org.jraf.klibminitel.internal.codes.Screen.CLEAR_SCREEN_AND_HOME
import org.jraf.klibminitel.internal.codes.escapeAccents
import org.jraf.klibminitel.internal.codes.escapeSpecialChars
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintWriter
import kotlin.concurrent.thread

class Minitel(filePath: String) {
    private val output: PrintWriter = PrintWriter(FileOutputStream(filePath))
    private val input = FileInputStream(File(filePath))

    private val readListeners = mutableSetOf<ReadListener>()

    init {
        startReadLoop()
    }

    private fun startReadLoop() {
        thread(name = "Minitel-read-loop") {
            while (true) {
                val read0 = input.read()
                if (read0 == -1) break
                dispatchReadEvent(
                    if (read0 == FunctionKey.CONTROL_KEY_ESCAPE) {
                        val read1 = input.read()
                        val functionKey = FunctionKey.fromCode(read1)
                        ReadEvent.FunctionKeyReadEvent(functionKey)
                    } else {
                        ReadEvent.CharacterReadEvent(read0.toChar())
                    }
                )
            }
        }
    }

    fun addReadListener(listener: ReadListener) {
        readListeners += listener
    }

    fun removeReadListener(listener: ReadListener) {
        readListeners -= listener
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

    private fun dispatchReadEvent(readEvent: ReadEvent) {
        readListeners.toList().forEach { it.onReadEvent(readEvent) }
    }

    fun moveCursorLeft() = out(MOVE_CURSOR_LEFT)
    fun moveCursorRight() = out(MOVE_CURSOR_RIGHT)
    fun moveCursorTop() = out(MOVE_CURSOR_TOP)
    fun moveCursorBottom() = out(MOVE_CURSOR_BOTTOM)

    fun clearEndOfLine() = out(CLEAR_END_OF_LINE)
    fun clearBottomOfScreen() = out(CLEAR_BOTTOM_OF_SCREEN)

    fun repeatCharacter(c: Char, times: Int) = out(Misc.repeatCharacter(c, times))
    fun repeatLastCharacter(times: Int) = out(Misc.repeatLastCharacter(times))

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

    interface ReadListener {
        fun onReadEvent(readEvent: ReadEvent)
    }
}