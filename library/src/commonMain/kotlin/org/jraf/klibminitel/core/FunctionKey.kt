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

package org.jraf.klibminitel.core

sealed class FunctionKey(private val code: Int) {
  companion object {
    const val CONTROL_KEY_ESCAPE = 0x13

    private val knownKeys = arrayOf(ENVOI, RETOUR, REPETITION, GUIDE, ANNULATION, SOMMAIRE, CORRECTION, SUITE, CONNEXION_FIN)

    fun fromCode(code: Int) = knownKeys.firstOrNull { it.code == code } ?: UNKNOWN(code)
  }

  data object ENVOI : FunctionKey(0x41)
  data object RETOUR : FunctionKey(0x42)
  data object REPETITION : FunctionKey(0x43)
  data object GUIDE : FunctionKey(0x44)
  data object ANNULATION : FunctionKey(0x45)
  data object SOMMAIRE : FunctionKey(0x46)
  data object CORRECTION : FunctionKey(0x47)
  data object SUITE : FunctionKey(0x48)
  data object CONNEXION_FIN : FunctionKey(0x59)
  data class UNKNOWN(val code: Int) : FunctionKey(code)
}
