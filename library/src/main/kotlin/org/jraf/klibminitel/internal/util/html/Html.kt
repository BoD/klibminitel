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

package org.jraf.klibminitel.internal.util.html

import org.jraf.klibminitel.internal.codes.CharacterSize.SIZE_NORMAL
import org.jraf.klibminitel.internal.codes.CharacterSize.SIZE_TALL
import org.jraf.klibminitel.internal.codes.Color.COLOR_FOREGROUND_7
import org.jraf.klibminitel.internal.codes.Color.colorForeground
import org.jraf.klibminitel.internal.util.color.AwtColor
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

internal fun String.escapeHtml(defaultColor: String = COLOR_FOREGROUND_7, defaultSize: String = SIZE_TALL): String {
    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = documentBuilder.parse(InputSource(StringReader("<root>${this.replace("&", "&amp;")}</root>")))
    return escapeHtml(document.childNodes, defaultColor, defaultSize)
}

private fun escapeHtml(nodeList: NodeList, defaultColor: String, defaultSize: String): String {
    var res = ""
    for (i in 0 until nodeList.length) {
        val node = nodeList.item(i)
        res += when (node.nodeType) {
            Node.TEXT_NODE -> node.textContent

            else -> {
                val element = node as Element
                when (node.nodeName) {
                    "font" -> {
                        val color = element.getAttribute("color")
                        colorForeground(AwtColor.decode(color)) + escapeHtml(
                            node.childNodes,
                            defaultColor,
                            defaultSize
                        ) + defaultColor
                    }

                    "small" -> {
                        SIZE_NORMAL + escapeHtml(
                            node.childNodes,
                            defaultColor,
                            defaultSize
                        ) + defaultSize
                    }

                    else -> escapeHtml(node.childNodes, defaultColor, defaultSize)
                }
            }
        }
    }
    return res
}
