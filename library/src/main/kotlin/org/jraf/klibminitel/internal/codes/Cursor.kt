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

package org.jraf.klibminitel.internal.codes

internal object Cursor {
    const val MOVE_CURSOR = '\u001F'
    const val MOVE_CURSOR_LEFT = '\u0008'
    const val MOVE_CURSOR_RIGHT = '\u0009'
    const val MOVE_CURSOR_BOTTOM = '\u000A'
    const val MOVE_CURSOR_TOP = '\u000B'

    const val SHOW_CURSOR = '\u0011'
    const val HIDE_CURSOR = '\u0014'

    fun moveCursor(x: Int, y: Int): String = "$MOVE_CURSOR${(0x41 + y).toChar()}${(0x41 + x).toChar()}"
}