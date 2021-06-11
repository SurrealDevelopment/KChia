package com.surrealdev.clispweb.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Section
import com.surrealdev.clispweb.style.WtContainer
import com.surrealdev.clispweb.style.WtOffsets
import com.surrealdev.clispweb.style.WtSections

@Composable
fun Layout(content: @Composable () -> Unit) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            height(100.percent)
            margin(0.px)
            property("box-sizing", StylePropertyValue("border-box"))
        }
    }) {
        content()
    }
}

@Composable
fun MainContentLayout(content: @Composable () -> Unit) {
    Main({
        style {
            property("flex", value("auto"))
            property("box-sizing", value("border-box"))
        }
    }) {
        content()
    }
}

@Composable
fun ContainerInSection(sectionThemeStyleClass: String? = null, content: @Composable () -> Unit) {
    Section({
        if (sectionThemeStyleClass != null) {
            classes(WtSections.wtSection, sectionThemeStyleClass)
        } else {
            classes(WtSections.wtSection)
        }
    }) {
        Div({
            classes(WtContainer.wtContainer, WtOffsets.wtTopOffset96)
        }) {
            content()
        }
    }
}