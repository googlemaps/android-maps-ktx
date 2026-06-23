/*
 * Copyright 2026 Google LLC
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

package com.google.maps.android.ktx.demo.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.maps.android.ktx.demo.R
import com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity

class SnippetMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Request edge-to-edge window decor layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_snippet_menu)

        // 2. Apply top system insets + cutout safe padding to the Toolbar to prevent EPERM notch clipping!
        val appBarLayout: View = findViewById(R.id.menu_appbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(
                view.paddingLeft,
                systemBarInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        // Flatten group items to display inside the single scrolling list
        val allSnippets = SnippetRegistry.groups.flatMap { it.items }

        val recyclerView: RecyclerView = findViewById(R.id.snippets_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SnippetAdapter(allSnippets) { item ->
            val intent = Intent(this, SnippetExecutionActivity::class.java).apply {
                putExtra("EXTRA_SNIPPET_TITLE", item.title)
                putExtra("EXTRA_SNIPPET_DESCRIPTION", item.description)
            }
            startActivity(intent)
        }
    }
}
