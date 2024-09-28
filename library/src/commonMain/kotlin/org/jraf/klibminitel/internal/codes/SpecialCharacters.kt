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

internal fun String.escapeSpecialChars() = replace("฿", " btc")
  .replace("€", " eur")
  .replace("☀", "*")
  .replace("\uD83C\uDF19", "(")
  .replace("☂", "///")
  .replace("☃", "oo")
  .replace("\uD83D\uDCA8", ">>")
  .replace("\uD83C\uDF2B", "##")
  .replace("☁", "#")
  .replace("\uD83C\uDF24", "*#")
  .replace("☁\uD83C\uDF19", "#(")
  .replace("▾", "min :")
  .replace("▴", "max :")
  .replace("º", "${Accents.ACCENT}\u0030")
  .replace("’", "'")
  .replace("…", "...")
  .replace("”", "\"")
