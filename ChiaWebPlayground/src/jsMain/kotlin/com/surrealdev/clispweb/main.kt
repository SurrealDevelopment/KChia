package com.surrealdev.clispweb
import chia.types.blockchain.Program
import com.surrealdev.clispweb.ace.ace
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import com.surrealdev.clispweb.components.*
import com.surrealdev.clispweb.content.*
import com.surrealdev.clispweb.style.AppStylesheet
import kotlinx.coroutines.Job

val startCode = """
                (mod (password new_puzhash amount)
                    (defconstant CREATE_COIN 51)
                
                    (if (= (sha256 password) (q . 0x2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824))
                        (list (list CREATE_COIN new_puzhash amount))
                        (x)
                    )
                )
            """.trimIndent()

data class CodeContext(
    var code: String = startCode,
    var runnerJob: Job? = null,
    var lastProgramCode: String = "",
    var args: String = "(hello 0x351A85E8CBF8C6BC1619C6653B1930F975A806574FBEB6D06C61F33AB4BEA82B 2)",
    var program: Program? = null

) {
    fun updateArgs(args: String) {
        this.args  = args
    }

}
fun main() {
    val codeContext = CodeContext()
    renderComposable(rootElementId = "root") {
        Style(AppStylesheet)

        Layout {
            Header()
            MainContentLayout {
                CodeEditorArea(codeContext)
            }
            PageFooter()
        }
    }
    // this must be done after render
    val edit = ace.edit("editor")
    edit.setTheme("ace/theme/chrome")
    edit.setValue(codeContext.code) // initial value
    edit.session.setMode(("ace/mode/lisp"))
    edit.session.on("change") {
        codeContext.code = edit.getValue()
        @Suppress("RedundantUnitExpression")
        Unit // explicit
    }
}