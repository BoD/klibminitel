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

enum class FunctionKey(private val code: Int) {
    ENVOI(0x41),
    RETOUR(0x42),
    REPETITION(0x43),
    GUIDE(0x44),
    ANNULATION(0x45),
    SOMMAIRE(0x46),
    CORRECTION(0x47),
    SUITE(0x48),
    CONNEXION_FIN(0x59),

    UNKNOWN(-1),
    ;

    companion object {
        const val CONTROL_KEY_ESCAPE = 0x13

        fun fromCode(code: Int) = values().firstOrNull { it.code == code } ?: UNKNOWN
    }
}
