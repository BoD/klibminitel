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

// See http://millevaches.hydraule.org/info/minitel/specs/codes.htm
// and https://archive.org/stream/minitel-stum1b/minitel-stum1b_djvu.txt
// and https://grandzebu.net/informatique/utiles/videotex.rtf
internal object Control {
  const val ESC = '\u001B'

  private const val REPEAT = '\u0012'

  // See https://forum.arduino.cc/t/aller-plus-loin-avec-un-minitel/484922/9
  // ESC, PRO3, AIGUILLAGE_ON, RCPT_ECRAN, EMET_MODEM
  const val LOCAL_ECHO_OFF = "$ESC\u003B\u0060\u0058\u0052"

  // ESC, PRO3, AIGUILLAGE_OFF, RCPT_ECRAN, EMET_MODEM
  const val LOCAL_ECHO_ON = "$ESC\u003B\u0061\u0058\u0052"

  const val BEEP = "$ESC\u0007"

  fun repeatCharacter(c: Char, times: Int): String = "$c$REPEAT${(0x40 + times - 1).toChar()}"
  fun repeatLastCharacter(times: Int): String = "$REPEAT${(0x40 + times).toChar()}"
}
