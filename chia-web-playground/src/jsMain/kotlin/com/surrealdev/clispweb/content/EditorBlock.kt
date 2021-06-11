package com.surrealdev.clispweb.content

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun EditorBlock(id: String = "editor") {
    Pre({
        style {
            property("overflow", value("auto"))
            height(300.px)
            display(DisplayStyle.Flex)
        }
    }) {
        Div({
            id(id)
            style {
                property("font-family", value("'JetBrains Mono', monospace"))
                property("tab-size", value(4))
                property("width", value("100%"))
                property("height", value("100%"))
               backgroundColor(Color("transparent"))
            }
        })
    }
}