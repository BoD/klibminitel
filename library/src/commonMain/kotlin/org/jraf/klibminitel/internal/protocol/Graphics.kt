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

internal object Graphics {
  // See https://jbellue.github.io/stum1b/#2-2-1-2-3-3
  const val GRAPHICS_MODE_ON: Byte = 0x0E
  const val GRAPHICS_MODE_OFF: Byte = 0x0F

  /**
   * Pass a value made of 3 rows of 2 bits each, from top to bottom, left to right.
   * For example, the value 0b00_11_00 will display the character ⠒, whereas 0b11_11_10 will display the character ⠟.
   */
  // See https://jbellue.github.io/stum1b/#schema2-6
  fun graphicsCharacter(value: Byte): Byte = (0x20 + swapBits(value)).toByte()

  /**
   * The way bytes are converted to graphics on the Minitel is like this:
   * ```
   * Bit 0   Bit 1
   * Bit 2   Bit 3
   * Bit 4   Bit 5
   * ```
   *
   * But what we want is:
   * ```
   * Bit 5   Bit 4
   * Bit 3   Bit 2
   * Bit 1   Bit 0
   * ```
   */
  private fun swapBits(value: Byte): Byte {
    // Not sure if there's a simpler way to do this
    val v = value.toInt()
    val bit0 = (v and 0b00_00_01) shl 5
    val bit1 = (v and 0b00_00_10) shl 3
    val bit2 = (v and 0b00_01_00) shl 1
    val bit3 = (v and 0b00_10_00) shr 1
    val bit4 = (v and 0b01_00_00) shr 3
    val bit5 = (v and 0b10_00_00) shr 5
    return (bit0 or bit1 or bit2 or bit3 or bit4 or bit5).toByte()
  }
}
