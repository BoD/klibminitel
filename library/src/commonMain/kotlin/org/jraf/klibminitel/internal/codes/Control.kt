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

internal object Control {
  const val ESC = '\u001B'

  // See https://jbellue.github.io/stum1b/#2-6-2
  private const val PRO2 = '\u003A'
  private const val PRO3 = '\u003B'

  // See https://jbellue.github.io/stum1b/#2-6-3-2
  private const val ROUTING_OFF = '\u0060'
  private const val ROUTING_ON = '\u0061'

  // See https://jbellue.github.io/stum1b/#2-6-1
  private const val SCREEN_SEND = '\u0050'
  private const val SCREEN_RECEIVE = '\u0058'
  private const val MODEM_SEND = '\u0052'
  private const val SOCKET_SEND = '\u0053'

  // This references the modem, because the keyboard goes through the modem and then to the screen in "local" mode
  // See schema in https://jbellue.github.io/stum1b/#3-1
  const val LOCAL_ECHO_OFF = "$ESC$PRO3$ROUTING_OFF$SCREEN_RECEIVE$MODEM_SEND"
  const val LOCAL_ECHO_ON = "$ESC$PRO3$ROUTING_ON$SCREEN_RECEIVE$MODEM_SEND"

  // See https://jbellue.github.io/stum1b/#2-6-11-1
  private const val START = '\u0069'
  private const val STOP = '\u006A'
  private const val SCROLL = '\u0043'
  const val SCROLL_ON = "$ESC$PRO2$START$SCROLL"
  const val SCROLL_OFF = "$ESC$PRO2$STOP$SCROLL"

  // See https://jbellue.github.io/stum1b/#2-6-4-2
  private const val ACK_OFF = '\u0064'
  private const val ACKNOWLEDGE_OFF_SCREEN = "$ESC$PRO2$ACK_OFF$SCREEN_SEND"
  private const val ACKNOWLEDGE_OFF_SOCKET = "$ESC$PRO2$ACK_OFF$SOCKET_SEND"
  const val ACKNOWLEDGE_OFF = "$ACKNOWLEDGE_OFF_SCREEN$ACKNOWLEDGE_OFF_SOCKET"

  const val BEEP = "$ESC\u0007"

  // See https://jbellue.github.io/stum1b/#2-2-1-2-6-1
  private const val REP = '\u0012'
  fun repeatCharacter(c: Char, times: Int): String = "$c$REP${(0x40 + times - 1).toChar()}"
  fun repeatLastCharacter(times: Int): String = "$REP${(0x40 + times).toChar()}"
}
