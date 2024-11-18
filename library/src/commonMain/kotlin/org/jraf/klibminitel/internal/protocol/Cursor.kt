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

package org.jraf.klibminitel.internal.protocol

import org.jraf.klibminitel.internal.protocol.Control.ESC

// See https://jbellue.github.io/stum1b/#2-2-1-2-5-2
internal object Cursor {
  /** Also known as Backspace */
  const val MOVE_CURSOR_LEFT: Byte = 0x08

  /** Also known as Horizontal Tab */
  const val MOVE_CURSOR_RIGHT: Byte = 0x09

  /* Also known as Line Feed */
  const val MOVE_CURSOR_DOWN: Byte = 0x0A

  /* Also known as Vertical Tabulation */
  const val MOVE_CURSOR_UP: Byte = 0x0B

  const val SHOW_CURSOR: Byte = 0x11
  const val HIDE_CURSOR: Byte = 0x14

  // See https://jbellue.github.io/stum1b/#2-6-6-2
  val GET_CURSOR_POSITION = byteArrayOf(ESC, 0x61)

  /** Also known as Unit Separator */
  // See https://jbellue.github.io/stum1b/#2-2-1-2-5-3
  const val MOVE_CURSOR: Byte = 0x1F
  fun moveCursor(x: Int, y: Int): ByteArray = byteArrayOf(MOVE_CURSOR, (0x41 + y).toByte(), (0x41 + x).toByte())
}
