package com.surrealdev.clispweb.content


import androidx.compose.runtime.*
import chia.clisp_high_level.Clvmc
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.runFromString
import chia.clisp_low_level.assemble
import chia.clisp_low_level.dissasemble
import chia.types.blockchain.Program
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.surrealdev.clispweb.CodeContext
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.attributes.Tag
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.surrealdev.clispweb.components.ContainerInSection
import com.surrealdev.clispweb.style.*
import kotlinx.coroutines.*
import org.w3c.dom.HTMLElement


@Composable
fun CodeEditorArea(codeContext: CodeContext) {
    ContainerInSection {
        Div({
            classes(WtRows.wtRow, WtRows.wtRowSizeM, WtRows.wtRowSmAlignItemsCenter)
        }) {

            Div({
                classes(
                    WtCols.wtCol10,
                    WtCols.wtColMd8,
                    WtCols.wtColSm12,
                    WtOffsets.wtTopOffsetSm12
                )
            }) {
                H1(attrs = { classes(WtTexts.wtHero) }) {
                    Text("Chia Lisp")
                    Span({
                        classes(WtTexts.wtHero)
                        style {
                            display(DisplayStyle.InlineBlock)
                            property("white-space", value("nowrap"))
                        }
                    }) {
                        Text("Web")

                        Span(attrs = { classes(AppStylesheet.composeTitleTag) }) {
                            Text("Technology preview")
                        }
                    }
                }
                Div({
                    classes(WtContainer.wtContainer)
                }) {
                    EditorWithRunner(codeContext)
                }
            }

            // TODO
            // programSection()

        }

    }
}

@Composable
private fun ProgramSection(codeContext: CodeContext) {
    Div({
        classes(
            WtCols.wtColMd3,
            WtCols.wtColAutoFill,
            WtCols.wtColSmAutoFill,
            WtOffsets.wtTopOffsetSm12,
        )
        style {
            alignContent(AlignContent.Start)
        }
    }) {
        Span({
            classes(WtTexts.wtSubtitle2)
            style {
                display(DisplayStyle.InlineBlock)
                property("white-space", value("nowrap"))
            }
        }) {
            Text("Programs")

        }
    }
}

@Composable
private fun EditorWithRunner(codeContext: CodeContext) {
    Div({
        classes(WtRows.wtRow, WtRows.wtRowSizeM)
    }) {

        Div({
            classes(WtCols.wtCol9, WtCols.wtColMd9, WtCols.wtColSm12)
        }) {

            ComposeWebStatusMessage()

            CodeRunner(codeContext)

        }
    }
}



@Composable
private fun CodeRunner(codeContext: CodeContext) {

    Div({
        style {
            marginTop(24.px)
            backgroundColor(Color.RGBA(39, 40, 44, 0.05))
            borderRadius(8.px)
            property("font-family", value("'JetBrains Mono', monospace"))
        }
    }) {
        Div({
            style {
                property("padding", value("12px 16px"))
            }
        }) {
            EditorBlock()
        }

        Hr {
            style {
                height(1.px)
                border(width = 0.px)
                backgroundColor(Color.RGBA(39, 40, 44, 0.15))
            }
        }



        Hr {
            style {
                height(1.px)
                border(width = 0.px)
                backgroundColor(Color.RGBA(39, 40, 44, 0.15))
            }
        }
        Div({
            style {
                property("padding", value("12px 16px"))
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                alignItems(AlignItems.Center)
            }
        }) {
            Span({
                classes(WtTexts.wtText2)
                style {
                    property("margin-right", value(8.px))
                }
            }) {
                Text("Args: ")
            }

            Span ({
                classes(WtTexts.wtText2)
                style {
                    property("margin-right", value(8.px))
                    property("overflow-wrap", value("anywhere"))
                    width(100.pc)
                }
                contentEditable(true)
            }) {
                DomSideEffect { element ->
                    element.innerText = codeContext.args
                    element.oninput = {
                        codeContext.updateArgs(element.innerText)
                    }
                }
            }
        }

        ChiaCodeResult(codeContext)
    }
}

@Composable
private fun ResultBox(name: String, value: String) {
    Div({
        style {
            property("padding", value("12px 16px"))
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            alignItems(AlignItems.Center)
        }
    }) {
        Span({
            classes(WtTexts.wtText2)
            style {
                property("margin-right", value(8.px))
            }
        }) {
            Text("$name:")
        }

        Span ({
            classes(WtTexts.wtText3)
            style {
                property("margin-right", value(8.px))
                property("overflow-wrap", value("anywhere"))
            }
        }) {
            Text(value)
        }
    }
}

enum class CodeRunState{
    STABNDY,
    COMPILING,
    RUNNING
}

@Composable
private fun ChiaCodeResult(codeContext: CodeContext) {

    var lastProgHex by remember { mutableStateOf("") }
    var lastProgSexpOut by remember { mutableStateOf("") }
    var lastProgOutput by remember { mutableStateOf("") }
    var codeRunState by remember { mutableStateOf(CodeRunState.STABNDY) }


    ResultBox("Serialized", lastProgSexpOut)
    ResultBox("Run Output", lastProgOutput)

    Div({
        style {
            property("padding", value("12px 16px"))
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            alignItems(AlignItems.Center)
        }
    }) {

        Span({
            classes(WtTexts.wtText2)
            style {
                property("margin-right", value(8.px))
            }
        }) {
            Text("Actions:")
        }

        fun compile(): Deferred<Boolean> {
            codeContext.lastProgramCode = codeContext.code

            val def = MainScope().async {
                try {
                    val result = Clvmc.compileFromText(codeContext.code)
                    codeContext.program = Program(result)
                    lastProgOutput = "Compile Result: ${dissasemble(result)}"
                    lastProgSexpOut = result.hex
                    lastProgHex = assemble(codeContext.code).hex
                    codeRunState = CodeRunState.STABNDY
                    true
                } catch (e: Exception) {
                    lastProgHex = ""
                    lastProgSexpOut = ""
                    lastProgOutput = ""
                    lastProgOutput = e.message ?: "Unknown Null"
                    codeRunState = CodeRunState.STABNDY
                    false
                }
            }
            codeRunState = CodeRunState.COMPILING
            codeContext.runnerJob = def.job
            return def
        }

        fun runWithArgs(): Deferred<Boolean> {
            // check if need new compile
            val def = MainScope().async {
                if (codeContext.code != codeContext.lastProgramCode) {
                    if (!compile().await()) return@async false
                }

                try {
                    val argExp = assemble(codeContext.args)
                    val result = codeContext.program!!.run(argExp)
                    lastProgOutput = "Run Result: ${dissasemble(result)}"
                    codeRunState = CodeRunState.STABNDY
                    true
                } catch (e: Exception) {
                    lastProgOutput = e.message ?: "Unknown Null"
                    codeRunState = CodeRunState.STABNDY
                    false
                }

            }

            codeRunState = CodeRunState.COMPILING
            codeContext.runnerJob = def.job
            return def
        }

        when (codeRunState) {
            CodeRunState.STABNDY -> {
                Div({
                    id("actionsContainer")
                }) {
                    Button(attrs = { onClick {
                        compile()
                    } }) {
                        Text("Compile")
                    }
                    Button(attrs = { onClick {

                        runWithArgs()

                    } }) {
                        Text("Run")
                    }
                }
            }
            CodeRunState.COMPILING -> {
                Div({
                    id("actionsContainer")
                }) {
                    Button(attrs = { onClick {
                        codeContext.runnerJob?.cancel()
                    } }) {
                        Text("Stop Compile")
                    }
                }
            }
            else -> {
                Div({
                    id("actionsContainer")
                }) {
                    Text("Unused..")
                }
            }
        }





    }
}

@Composable
private fun ComposeWebStatusMessage() {
    Div({
        classes(WtRows.wtRow, WtRows.wtRowSizeXs, WtOffsets.wtTopOffset24)
    }) {
        Div({
            classes(WtCols.wtColInline)
        }) {
            Img(src = "ic_info.svg", attrs = {
                style {
                    width(24.px)
                    height(24.px)
                }
            }) {}
        }

        Div({
            classes(WtCols.wtColAutoFill)
        }) {
            P({
                classes(WtTexts.wtText3)
            }) {
                Text(
                    "Currently for experimentation only! Do not rely on for production.\n" +
                            "This is a browser based compiler. Run times may be long."
                )
            }
        }
    }
}

@Composable
fun Hr(
    attrs: (AttrsBuilder<Tag.Div>.() -> Unit) = {}
) {
    TagElement<Tag.Div, HTMLElement>(
        tagName = "hr",
        applyAttrs = attrs,
        content = { }
    )
}