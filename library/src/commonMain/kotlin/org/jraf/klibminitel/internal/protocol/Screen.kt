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
internal object Screen {
  const val CLEAR_SCREEN_AND_HOME: Byte = 0x0C

  const val CLEAR_END_OF_LINE: Byte = 0x18

  // See https://jbellue.github.io/stum1b/#2-2-1-2-4-1
  val CSI = byteArrayOf(ESC, 0x5B)

  // See https://jbellue.github.io/stum1b/#2-2-1-2-5-2
  val CLEAR_BOTTOM_OF_SCREEN = CSI + 0x4A
}
