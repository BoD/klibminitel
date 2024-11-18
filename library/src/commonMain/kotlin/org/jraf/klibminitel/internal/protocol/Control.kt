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

internal object Control {
  const val ESC: Byte = 0x1B

  // See https://jbellue.github.io/stum1b/#2-6-2
  private const val PRO2: Byte = 0x3A
  private const val PRO3: Byte = 0x3B

  // See https://jbellue.github.io/stum1b/#2-6-3-2
  private const val ROUTING_OFF: Byte = 0x60
  private const val ROUTING_ON: Byte = 0x61

  // See https://jbellue.github.io/stum1b/#2-6-1
  private const val SCREEN_SEND: Byte = 0x50
  private const val SCREEN_RECEIVE: Byte = 0x58
  private const val MODEM_SEND: Byte = 0x52
  private const val SOCKET_SEND: Byte = 0x53

  // This references the modem, because the keyboard goes through the modem and then to the screen in "local" mode
  // See schema in https://jbellue.github.io/stum1b/#3-1
  val LOCAL_ECHO_OFF = byteArrayOf(ESC, PRO3, ROUTING_OFF, SCREEN_RECEIVE, MODEM_SEND)
  val LOCAL_ECHO_ON = byteArrayOf(ESC, PRO3, ROUTING_ON, SCREEN_RECEIVE, MODEM_SEND)

  // See https://jbellue.github.io/stum1b/#2-6-11-1
  private const val START: Byte = 0x69
  private const val STOP: Byte = 0x6A
  private const val SCROLL: Byte = 0x43
  val SCROLL_ON = byteArrayOf(ESC, PRO2, START, SCROLL)
  val SCROLL_OFF = byteArrayOf(ESC, PRO2, STOP, SCROLL)

  // See https://jbellue.github.io/stum1b/#2-6-4-2
  private const val ACK_OFF: Byte = 0x64
  private val ACKNOWLEDGE_OFF_SCREEN = byteArrayOf(ESC, PRO2, ACK_OFF, SCREEN_SEND)
  private val ACKNOWLEDGE_OFF_SOCKET = byteArrayOf(ESC, PRO2, ACK_OFF, SOCKET_SEND)
  val ACKNOWLEDGE_OFF = ACKNOWLEDGE_OFF_SCREEN + ACKNOWLEDGE_OFF_SOCKET

  val BEEP = byteArrayOf(ESC, 0x07)

  // See https://jbellue.github.io/stum1b/#2-2-1-2-6-1
  private const val REP: Byte = 0x12
  fun repeatCharacter(c: Char, times: Int): ByteArray {
    val byte = c.code.toByte()
    return when (times.coerceAtLeast(0)) {
      0 -> {
        byteArrayOf()
      }

      1 -> {
        byteArrayOf(byte)
      }

      2 -> {
        byteArrayOf(byte, byte)
      }

      3 -> {
        byteArrayOf(byte, byte, byte)
      }

      else -> {
        byteArrayOf(byte, REP, (0x40 + times - 1).toByte())
      }
    }
  }

  fun repeatLastCharacter(times: Int): ByteArray = byteArrayOf(REP, (0x40 + times).toByte())
}
