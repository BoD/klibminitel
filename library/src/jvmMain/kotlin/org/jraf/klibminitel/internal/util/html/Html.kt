/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2024-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.klibminitel.internal.util.html

import org.jraf.klibminitel.internal.protocol.CharacterSize.SIZE_NORMAL
import org.jraf.klibminitel.internal.protocol.CharacterSize.SIZE_TALL
import org.jraf.klibminitel.internal.protocol.Color.COLOR_FOREGROUND_7
import org.jraf.klibminitel.internal.protocol.Color.colorForeground
import org.jraf.klibminitel.internal.util.color.RgbColor
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

internal fun String.escapeHtml(defaultColor: ByteArray = COLOR_FOREGROUND_7, defaultSize: ByteArray = SIZE_TALL): ByteArray {
  val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
  val document = documentBuilder.parse(InputSource(StringReader("<root>${this.replace("&", "&amp;")}</root>")))
  return escapeHtml(document.childNodes, defaultColor, defaultSize)
}

private fun escapeHtml(nodeList: NodeList, defaultColor: ByteArray, defaultSize: ByteArray): ByteArray {
  var res = ByteArray(0)
  for (i in 0 until nodeList.length) {
    val node = nodeList.item(i)
    res += when (node.nodeType) {
      Node.TEXT_NODE -> node.textContent.toByteArray()

      else -> {
        val element = node as Element
        when (node.nodeName) {
          "font" -> {
            val color = element.getAttribute("color")
            colorForeground(RgbColor.decodeHtml(color)) + escapeHtml(
              node.childNodes,
              defaultColor,
              defaultSize,
            ) + defaultColor
          }

          "small" -> {
            SIZE_NORMAL + escapeHtml(
              node.childNodes,
              defaultColor,
              defaultSize,
            ) + defaultSize
          }

          else -> escapeHtml(node.childNodes, defaultColor, defaultSize)
        }
      }
    }
  }
  return res
}

private fun RgbColor.Companion.decodeHtml(color: String): RgbColor {
  val hex = color.substring(1)
  return RgbColor(
    r = hex.substring(0, 2).toInt(16) / 255f,
    g = hex.substring(2, 4).toInt(16) / 255f,
    b = hex.substring(4, 6).toInt(16) / 255f,
  )
}
