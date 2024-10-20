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

internal object Graphics {
  // See https://jbellue.github.io/stum1b/#2-2-1-2-3-3
  const val GRAPHICS_MODE_ON = '\u000E'
  const val GRAPHICS_MODE_OFF = '\u000F'

  /**
   * Pass a value made of 3 rows of 2 bits each, from top to bottom, left to right.
   * For example, the value 0b00_11_00 will display the character ⠒, whereas 0b11_11_10 will display the character ⠟.
   */
  // See https://jbellue.github.io/stum1b/#schema2-6
  fun graphicsCharacter(value: Int): String = "${(32 + value).toChar()}"
}
