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

private const val SPECIAL = '\u0019'

private const val ACCENT_GRAVE = "$SPECIAL\u0041"
private const val ACCENT_ACUTE = "$SPECIAL\u0042"
private const val ACCENT_CIRCUMFLEX = "$SPECIAL\u0043"
private const val ACCENT_UMLAUT = "$SPECIAL\u0048"
private const val ACCENT_CEDILLA = "$SPECIAL\u004B"

const val ACCENT_GRAVE_A_UP = "${ACCENT_GRAVE}A"
const val ACCENT_GRAVE_E_UP = "${ACCENT_GRAVE}E"
const val ACCENT_GRAVE_I_UP = "${ACCENT_GRAVE}I"
const val ACCENT_GRAVE_O_UP = "${ACCENT_GRAVE}O"
const val ACCENT_GRAVE_U_UP = "${ACCENT_GRAVE}U"

const val ACCENT_GRAVE_A_LOW = "${ACCENT_GRAVE}a"
const val ACCENT_GRAVE_E_LOW = "${ACCENT_GRAVE}e"
const val ACCENT_GRAVE_I_LOW = "${ACCENT_GRAVE}i"
const val ACCENT_GRAVE_O_LOW = "${ACCENT_GRAVE}o"
const val ACCENT_GRAVE_U_LOW = "${ACCENT_GRAVE}u"

const val ACCENT_ACUTE_A_UP = "${ACCENT_ACUTE}A"
const val ACCENT_ACUTE_E_UP = "${ACCENT_ACUTE}E"
const val ACCENT_ACUTE_I_UP = "${ACCENT_ACUTE}I"
const val ACCENT_ACUTE_O_UP = "${ACCENT_ACUTE}O"
const val ACCENT_ACUTE_U_UP = "${ACCENT_ACUTE}U"

const val ACCENT_ACUTE_A_LOW = "${ACCENT_ACUTE}a"
const val ACCENT_ACUTE_E_LOW = "${ACCENT_ACUTE}e"
const val ACCENT_ACUTE_I_LOW = "${ACCENT_ACUTE}i"
const val ACCENT_ACUTE_O_LOW = "${ACCENT_ACUTE}o"
const val ACCENT_ACUTE_U_LOW = "${ACCENT_ACUTE}u"

const val ACCENT_CIRCUMFLEX_A_UP = "${ACCENT_CIRCUMFLEX}A"
const val ACCENT_CIRCUMFLEX_E_UP = "${ACCENT_CIRCUMFLEX}E"
const val ACCENT_CIRCUMFLEX_I_UP = "${ACCENT_CIRCUMFLEX}I"
const val ACCENT_CIRCUMFLEX_O_UP = "${ACCENT_CIRCUMFLEX}O"
const val ACCENT_CIRCUMFLEX_U_UP = "${ACCENT_CIRCUMFLEX}U"

const val ACCENT_CIRCUMFLEX_A_LOW = "${ACCENT_CIRCUMFLEX}a"
const val ACCENT_CIRCUMFLEX_E_LOW = "${ACCENT_CIRCUMFLEX}e"
const val ACCENT_CIRCUMFLEX_I_LOW = "${ACCENT_CIRCUMFLEX}i"
const val ACCENT_CIRCUMFLEX_O_LOW = "${ACCENT_CIRCUMFLEX}o"
const val ACCENT_CIRCUMFLEX_U_LOW = "${ACCENT_CIRCUMFLEX}u"

const val ACCENT_UMLAUT_A_UP = "${ACCENT_UMLAUT}A"
const val ACCENT_UMLAUT_E_UP = "${ACCENT_UMLAUT}E"
const val ACCENT_UMLAUT_I_UP = "${ACCENT_UMLAUT}I"
const val ACCENT_UMLAUT_O_UP = "${ACCENT_UMLAUT}O"
const val ACCENT_UMLAUT_U_UP = "${ACCENT_UMLAUT}U"

const val ACCENT_UMLAUT_A_LOW = "${ACCENT_UMLAUT}a"
const val ACCENT_UMLAUT_E_LOW = "${ACCENT_UMLAUT}e"
const val ACCENT_UMLAUT_I_LOW = "${ACCENT_UMLAUT}i"
const val ACCENT_UMLAUT_O_LOW = "${ACCENT_UMLAUT}o"
const val ACCENT_UMLAUT_U_LOW = "${ACCENT_UMLAUT}u"

const val ACCENT_CEDILLA_C_UP = "${ACCENT_CEDILLA}C"
const val ACCENT_CEDILLA_C_LOW = "${ACCENT_CEDILLA}c"

const val POUND_STERLING = "$SPECIAL\u0023"
const val DOLLAR = "$SPECIAL\u0024"
const val DEGREE = "$SPECIAL\u0030"
const val OE_UP = "$SPECIAL\u006A"
const val OE_LOW = "$SPECIAL\u007A"
const val ESSZET = "$SPECIAL\u007B"

internal fun String.replaceSpecialCharacters() = replace("À", ACCENT_GRAVE_A_UP)
  .replace("È", ACCENT_GRAVE_E_UP)
  .replace("Ì", ACCENT_GRAVE_I_UP)
  .replace("Ò", ACCENT_GRAVE_O_UP)
  .replace("Ù", ACCENT_GRAVE_U_UP)
  .replace("à", ACCENT_GRAVE_A_LOW)
  .replace("è", ACCENT_GRAVE_E_LOW)
  .replace("ì", ACCENT_GRAVE_I_LOW)
  .replace("ò", ACCENT_GRAVE_O_LOW)
  .replace("ù", ACCENT_GRAVE_U_LOW)
  .replace("Á", ACCENT_ACUTE_A_UP)
  .replace("É", ACCENT_ACUTE_E_UP)
  .replace("Í", ACCENT_ACUTE_I_UP)
  .replace("Ó", ACCENT_ACUTE_O_UP)
  .replace("Ú", ACCENT_ACUTE_U_UP)
  .replace("á", ACCENT_ACUTE_A_LOW)
  .replace("é", ACCENT_ACUTE_E_LOW)
  .replace("í", ACCENT_ACUTE_I_LOW)
  .replace("ó", ACCENT_ACUTE_O_LOW)
  .replace("ú", ACCENT_ACUTE_U_LOW)
  .replace("Â", ACCENT_CIRCUMFLEX_A_UP)
  .replace("Ê", ACCENT_CIRCUMFLEX_E_UP)
  .replace("Î", ACCENT_CIRCUMFLEX_I_UP)
  .replace("Ô", ACCENT_CIRCUMFLEX_O_UP)
  .replace("Û", ACCENT_CIRCUMFLEX_U_UP)
  .replace("â", ACCENT_CIRCUMFLEX_A_LOW)
  .replace("ê", ACCENT_CIRCUMFLEX_E_LOW)
  .replace("î", ACCENT_CIRCUMFLEX_I_LOW)
  .replace("ô", ACCENT_CIRCUMFLEX_O_LOW)
  .replace("û", ACCENT_CIRCUMFLEX_U_LOW)
  .replace("Ä", ACCENT_UMLAUT_A_UP)
  .replace("Ë", ACCENT_UMLAUT_E_UP)
  .replace("Ï", ACCENT_UMLAUT_I_UP)
  .replace("Ö", ACCENT_UMLAUT_O_UP)
  .replace("Ü", ACCENT_UMLAUT_U_UP)
  .replace("ä", ACCENT_UMLAUT_A_LOW)
  .replace("ë", ACCENT_UMLAUT_E_LOW)
  .replace("ï", ACCENT_UMLAUT_I_LOW)
  .replace("ö", ACCENT_UMLAUT_O_LOW)
  .replace("ü", ACCENT_UMLAUT_U_LOW)
  .replace("Ç", ACCENT_CEDILLA_C_UP)
  .replace("ç", ACCENT_CEDILLA_C_LOW)
  .replace("£", POUND_STERLING)
  .replace("$", DOLLAR)
  .replace("°", DEGREE)
  .replace("º", DEGREE)
  .replace("Œ", OE_UP)
  .replace("œ", OE_LOW)
  .replace("ß", ESSZET)
  .replace("’", "'")
  .replace("…", "...")
  .replace("”", "\"")
  .replace("€", "e")
