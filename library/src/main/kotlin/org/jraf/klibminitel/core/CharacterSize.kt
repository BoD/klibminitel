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

import org.jraf.klibminitel.internal.codes.Accents
import org.jraf.klibminitel.internal.codes.CharacterSize.PARAM_SIZE_DOUBLE
import org.jraf.klibminitel.internal.codes.CharacterSize.PARAM_SIZE_NORMAL
import org.jraf.klibminitel.internal.codes.CharacterSize.PARAM_SIZE_TALL
import org.jraf.klibminitel.internal.codes.CharacterSize.PARAM_SIZE_WIDE
import org.jraf.klibminitel.internal.codes.CharacterSize.SIZE_DOUBLE
import org.jraf.klibminitel.internal.codes.CharacterSize.SIZE_NORMAL
import org.jraf.klibminitel.internal.codes.CharacterSize.SIZE_TALL
import org.jraf.klibminitel.internal.codes.CharacterSize.SIZE_WIDE
import org.jraf.klibminitel.internal.codes.Codes

enum class CharacterSize(
    internal val characterSizeCode: String,
    val maxCharactersHorizontal: Int,
    val maxCharactersVertical: Int,
    val characterWidth: Int,
    val characterHeight: Int
) {
    NORMAL(
        SIZE_NORMAL,
        SCREEN_WIDTH_NORMAL,
        SCREEN_HEIGHT_NORMAL,
        1,
        1
    ),
    TALL(
        SIZE_TALL,
        SCREEN_WIDTH_TALL,
        SCREEN_HEIGHT_TALL,
        1,
        2
    ),
    WIDE(
        SIZE_WIDE,
        SCREEN_WIDTH_WIDE,
        SCREEN_HEIGHT_WIDE,
        2,
        1
    ),
    DOUBLE(
        SIZE_DOUBLE,
        SCREEN_WIDTH_DOUBLE,
        SCREEN_HEIGHT_DOUBLE,
        2,
        2
    ),

    ;

    companion object {
        fun String.getWidth(baseCharacterSize: CharacterSize): Int {
            var res = 0
            var i = 0
            var curCharWidth = baseCharacterSize.characterWidth
            while (i < length) {
                val c = this[i]
                when (c) {
                    Codes.ESC -> {
                        when (this[i + 1]) {
                            PARAM_SIZE_NORMAL -> curCharWidth = CharacterSize.NORMAL.characterWidth
                            PARAM_SIZE_TALL -> curCharWidth = CharacterSize.TALL.characterWidth
                            PARAM_SIZE_WIDE -> curCharWidth = CharacterSize.WIDE.characterWidth
                            PARAM_SIZE_DOUBLE -> curCharWidth = CharacterSize.DOUBLE.characterWidth
                        }
                        i += 2
                    }

                    Accents.ACCENT -> {
                        res += curCharWidth
                        i += 2
                    }

                    else -> {
                        res += curCharWidth
                        i++
                    }
                }
            }
            return res
        }
    }
}
