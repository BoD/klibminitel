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

import org.jraf.klibminitel.internal.codes.Control.ESC

internal object Character {
  const val GRAPHICS_MODE_ON = '\u000E'
  const val GRAPHICS_MODE_OFF = '\u000F'

  const val INVERT_ON = "$ESC\u005D"
  const val INVERT_OFF = "$ESC\u005C"

  const val UNDERLINE_ON = "$ESC\u005A"
  const val UNDERLINE_OFF = "$ESC\u0059"

  fun graphicsCharacter(value: Int, alreadyInGraphicsMode: Boolean): String =
    "${if (alreadyInGraphicsMode) "" else GRAPHICS_MODE_ON}${(32 + value).toChar()}"
}
