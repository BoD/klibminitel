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

internal object Accents {
    const val ACCENT = '\u0019'
    private const val ACCENT_GRAVE = "$ACCENT\u0041"
    private const val ACCENT_ACUTE = "$ACCENT\u0042"
    private const val ACCENT_CIRCUMFLEX = "$ACCENT\u0043"
    private const val ACCENT_UMLAUT = "$ACCENT\u0048"

    const val SPECIAL_CHAR_A_GRAVE = "${ACCENT_GRAVE}a"
    const val SPECIAL_CHAR_E_GRAVE = "${ACCENT_GRAVE}e"
    const val SPECIAL_CHAR_U_GRAVE = "${ACCENT_GRAVE}u"
    const val SPECIAL_CHAR_E_ACUTE = "${ACCENT_ACUTE}e"
    const val SPECIAL_CHAR_A_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}a"
    const val SPECIAL_CHAR_E_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}e"
    const val SPECIAL_CHAR_I_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}i"
    const val SPECIAL_CHAR_O_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}o"
    const val SPECIAL_CHAR_U_CIRCUMFLEX = "${ACCENT_CIRCUMFLEX}u"
    const val SPECIAL_CHAR_E_UMLAUT = "${ACCENT_UMLAUT}e"
}

internal fun String.escapeAccents() = replace("à", Accents.SPECIAL_CHAR_A_GRAVE)
    .replace("è", Accents.SPECIAL_CHAR_E_GRAVE)
    .replace("ù", Accents.SPECIAL_CHAR_U_GRAVE)
    .replace("é", Accents.SPECIAL_CHAR_E_ACUTE)
    .replace("â", Accents.SPECIAL_CHAR_A_CIRCUMFLEX)
    .replace("ê", Accents.SPECIAL_CHAR_E_CIRCUMFLEX)
    .replace("î", Accents.SPECIAL_CHAR_I_CIRCUMFLEX)
    .replace("ô", Accents.SPECIAL_CHAR_O_CIRCUMFLEX)
    .replace("û", Accents.SPECIAL_CHAR_U_CIRCUMFLEX)
    .replace("ü", Accents.SPECIAL_CHAR_E_UMLAUT)
