/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2025-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.klibminitel.sample.debug

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jraf.klibminitel.core.Minitel
import org.jraf.klibminitel.sample.logd

class DebugApp(
  private val filePath: String,
) {
  private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  suspend fun start() {
    logd("DebugApp start")
    val minitel = Minitel(filePath)
    minitel.connect {
      screen.disableAcknowledgement()
      screen.localEcho(false)
      screen.clearScreenAndHome()
      screen.print("Hello, World!")

      coroutineScope.launch {
        system.collect { e ->
          onSystemEvent(e, screen)
        }
      }

      keyboard.collect { e ->
        onKeyboardEvent(e, screen)
      }
    }
  }

  private suspend fun onSystemEvent(e: Minitel.SystemEvent, screen: Minitel.Screen) {
    logd("System: ${e}")
  }

  private suspend fun onKeyboardEvent(e: Minitel.KeyboardEvent, screen: Minitel.Screen) {
    logd("Keyboard: ${e}")
  }
}
