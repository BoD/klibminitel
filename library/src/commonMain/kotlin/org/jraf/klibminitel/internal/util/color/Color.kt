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

package org.jraf.klibminitel.internal.util.color

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal typealias AwtColor = java.awt.Color

internal fun rgbToHsl(color: AwtColor): Triple<Float, Float, Float> {
  val components = color.getRGBColorComponents(null)
  return rgbToHsl(components[0], components[1], components[2])
}

internal fun rgbToHsl(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
  val max = max(max(r, g), b)
  val min = min(min(r, g), b)
  val c = (max - min)

  var h_ = 0f
  if (c == 0f) {
    h_ = 0f
  } else if (max == r) {
    h_ = (g - b) / c
    if (h_ < 0) h_ += 6f
  } else if (max == g) {
    h_ = (b - r) / c + 2f
  } else if (max == b) {
    h_ = (r - g) / c + 4f
  }
  val h = 60f * h_

  val l = (max + min) * 0.5f

  val s: Float
  if (c == 0f) {
    s = 0f
  } else {
    s = c / (1 - abs(2f * l - 1f))
  }

  return Triple(h, s, l)
}
