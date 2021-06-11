@file:Suppress("unused")

package com.surrealdev.clispweb.ace

import org.jetbrains.compose.web.css.Position
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement
import org.w3c.dom.Range
import kotlin.js.Json
import kotlin.js.RegExp

@JsModule("ace-builds")
@JsNonModule
external val ace: Ace


@JsModule("ace-builds/webpack-resolver")
@JsNonModule
external val resolver: Any


@JsNonModule
@JsModule("ace-builds")
external interface Ace {
    fun require(moduleName: String): Any
    fun edit(el: String): Editor
    fun edit(el: HTMLElement): Editor
    fun createEditSession(text: Document, mode: String): IEditSession
    fun createEditSession(text: String, mode: String): IEditSession
}

@JsModule("ace-builds")
@JsNonModule
external interface EditorChangeEvent {
    var start: Position
    var end: Position
    var action: String
    var lines: Array<Any>
}

@JsModule("ace-builds")
@JsNonModule
external interface OptionProvider {
    fun setOption(optionName: String, optionValue: Any)
    fun setOptions(keyValueTuples: Json)
    fun getOption(name: String): Any
    fun getOptions(optionNames: Array<String> = definedExternally): Json
    fun getOptions(): Json
    fun getOptions(optionNames: Json = definedExternally): Json
}

@JsModule("ace-builds")
@JsNonModule
external interface IEditSession : OptionProvider {
    //var selection: Selection
    //var bgTokenizer: BackgroundTokenizer
    var doc: Document
    fun on(event: String, fn: (e: Any) -> Any)
    fun findMatchingBracket(position: Position)
    fun addFold(text: String, range: Range)
    fun getFoldAt(row: Number, column: Number): Any
    fun removeFold(arg: Any)
    fun expandFold(arg: Any)
    fun foldAll(startRow: Number = definedExternally, endRow: Number = definedExternally, depth: Number = definedExternally)
    fun unfold(arg1: Any, arg2: Boolean)
    fun screenToDocumentColumn(row: Number, column: Number)
    fun getFoldDisplayLine(foldLine: Any, docRow: Number, docColumn: Number): Any
    fun getFoldsInRange(range: Range): Any
    fun highlight(text: String)
    fun highlightLines(startRow: Number, endRow: Number, clazz: String, inFront: Boolean): Range
    fun setDocument(doc: Document)
    fun getDocument(): Document
    fun `$resetRowCache`(row: Number)
    fun setValue(text: String)
    fun setMode(mode: String)
    fun getValue(): String
    //fun getSelection(): Selection
    fun getState(row: Number): String
    //fun getTokens(row: Number): Array<TokenInfo>
    //fun getTokenAt(row: Number, column: Number): TokenInfo?
    //fun setUndoManager(undoManager: UndoManager)
    //fun getUndoManager(): UndoManager
    fun getTabString(): String
    fun setUseSoftTabs(useSoftTabs: Boolean)
    fun getUseSoftTabs(): Boolean
    fun setTabSize(tabSize: Number)
    fun getTabSize(): Number
    fun isTabStop(position: Any): Boolean
    fun setOverwrite(overwrite: Boolean)
    fun getOverwrite(): Boolean
    fun toggleOverwrite()
    fun addGutterDecoration(row: Number, className: String)
    fun removeGutterDecoration(row: Number, className: String)
    fun getBreakpoints(): Array<Number>
    fun setBreakpoints(rows: Array<Any>)
    fun clearBreakpoints()
    fun setBreakpoint(row: Number, className: String)
    fun clearBreakpoint(row: Number)
    fun addMarker(range: Range, clazz: String, type: Function<*>, inFront: Boolean): Number
    fun addMarker(range: Range, clazz: String, type: String, inFront: Boolean): Number
    fun addDynamicMarker(marker: Any, inFront: Boolean)
    fun removeMarker(markerId: Number)
    fun getMarkers(inFront: Boolean): Array<Any>
    fun setAnnotations(annotations: Array<Annotation>)
    fun getAnnotations(): Any
    fun clearAnnotations()
    fun `$detectNewLine`(text: String)
    fun getWordRange(row: Number, column: Number): Range
    fun getAWordRange(row: Number, column: Number): Any
    fun setNewLineMode(newLineMode: String)
    fun getNewLineMode(): String
    fun setUseWorker(useWorker: Boolean)
    fun getUseWorker(): Boolean
    fun onReloadTokenizer()
    //fun `$mode`(mode: TextMode)
   // fun getMode(): TextMode
    fun setScrollTop(scrollTop: Number)
    fun getScrollTop(): Number
    fun setScrollLeft(scrollLeft: Number)
    fun getScrollLeft(): Number
    fun getScreenWidth(): Number
    fun getLine(row: Number): String
    fun getLines(firstRow: Number, lastRow: Number): Array<String>
    fun getLength(): Number
    fun getTextRange(range: Range): String
    fun insert(position: Position, text: String): Any
    fun remove(range: Range): Any
    fun undoChanges(deltas: Array<Any>, dontSelect: Boolean): Range
    fun redoChanges(deltas: Array<Any>, dontSelect: Boolean): Range
    fun setUndoSelect(enable: Boolean)
    fun replace(range: Range, text: String): Any
    fun moveText(fromRange: Range, toPosition: Any): Range
    fun indentRows(startRow: Number, endRow: Number, indentString: String)
    fun outdentRows(range: Range)
    fun moveLinesUp(firstRow: Number, lastRow: Number): Number
    fun moveLinesDown(firstRow: Number, lastRow: Number): Number
    fun duplicateLines(firstRow: Number, lastRow: Number): Number
    fun setUseWrapMode(useWrapMode: Boolean)
    fun getUseWrapMode(): Boolean
    fun setWrapLimitRange(min: Number, max: Number)
    fun adjustWrapLimit(desiredLimit: Number): Boolean
    fun getWrapLimit(): Number
    fun getWrapLimitRange(): Any
    fun `$getDisplayTokens`(str: String, offset: Number)
    fun `$getStringScreenWidth`(str: String, maxScreenColumn: Number, screenColumn: Number): Array<Number>
    fun getRowLength(row: Number): Number
    fun getScreenLastRowColumn(screenRow: Number): Number
    fun getDocumentLastRowColumn(docRow: Number, docColumn: Number): Number
    fun getDocumentLastRowColumnPosition(docRow: Number, docColumn: Number): Number
    fun getRowSplitData(): String
    fun getScreenTabSize(screenColumn: Number): Number
    fun screenToDocumentPosition(screenRow: Number, screenColumn: Number): Any
    fun documentToScreenPosition(docRow: Number, docColumn: Number): Any
    fun documentToScreenColumn(row: Number, docColumn: Number): Number
    fun documentToScreenRow(docRow: Number, docColumn: Number)
    fun getScreenLength(): Number
}

@JsModule("ace-builds")
@JsNonModule
external interface Editor : OptionProvider {
    fun on(ev: String, callback: (e: Any) -> Any)
    fun addEventListener(ev: String /* "change" */, callback: (ev: EditorChangeEvent) -> Any)
    fun addEventListener(ev: String, callback: Function<*>)
    fun off(ev: String, callback: Function<*>)
    fun removeListener(ev: String, callback: Function<*>)
    fun removeEventListener(ev: String, callback: Function<*>)
    var inMultiSelectMode: Boolean
    fun selectMoreLines(n: Number)
    fun onTextInput(text: String)
    fun onCommandKey(e: Any, hashId: Number, keyCode: Number)
    //var commands: CommandManager
    var session: IEditSession
    //var selection: Selection
    //var renderer: VirtualRenderer
    //var keyBinding: KeyBinding
    var container: HTMLElement
    fun onSelectionChange(e: Any)
    fun onChangeMode(e: Any = definedExternally)
    fun execCommand(command: String, args: Any = definedExternally)
    var `$blockScrolling`: Number
    fun setKeyboardHandler(keyboardHandler: String)
    fun getKeyboardHandler(): String
    //fun setSession(session: IEditSession)
    //fun getSession(): IEditSession
    fun setValue(param_val: String, cursorPos: Number = definedExternally): String
    fun getValue(): String
    //fun getSelection(): Selection
    fun resize(force: Boolean = definedExternally)
    fun setTheme(theme: String)
    fun getTheme(): String
    fun setStyle(style: String)
    fun unsetStyle()
    fun setFontSize(size: String)
    fun focus()
    fun isFocused(): Boolean
    fun blur()
    fun onFocus()
    fun onBlur()
    fun onDocumentChange(e: Any)
    fun onCursorChange()
    fun getCopyText(): String
    fun onCopy()
    fun onCut()
    fun onPaste(text: String)
    fun insert(text: String)
    fun setOverwrite(overwrite: Boolean)
    fun getOverwrite(): Boolean
    fun toggleOverwrite()
    fun setScrollSpeed(speed: Number)
    fun getScrollSpeed(): Number
    fun setDragDelay(dragDelay: Number)
    fun getDragDelay(): Number
    fun setSelectionStyle(style: String)
    fun getSelectionStyle(): String
    fun setHighlightActiveLine(shouldHighlight: Boolean)
    fun getHighlightActiveLine(): Boolean
    fun setHighlightSelectedWord(shouldHighlight: Boolean)
    fun getHighlightSelectedWord(): Boolean
    fun setShowInvisibles(showInvisibles: Boolean)
    fun getShowInvisibles(): Boolean
    fun setShowPrintMargin(showPrintMargin: Boolean)
    fun getShowPrintMargin(): Boolean
    fun setPrintMarginColumn(showPrintMargin: Number)
    fun getPrintMarginColumn(): Number
    fun setReadOnly(readOnly: Boolean)
    fun getReadOnly(): Boolean
    fun setBehavioursEnabled(enabled: Boolean)
    fun getBehavioursEnabled(): Boolean
    fun setWrapBehavioursEnabled(enabled: Boolean)
    fun getWrapBehavioursEnabled()
    fun setShowFoldWidgets(show: Boolean)
    fun getShowFoldWidgets()
    fun remove(dir: String)
    fun removeWordRight()
    fun removeWordLeft()
    fun removeToLineStart()
    fun removeToLineEnd()
    fun splitLine()
    fun transposeLetters()
    fun toLowerCase()
    fun toUpperCase()
    fun indent()
    fun blockIndent()
    fun blockOutdent(arg: String = definedExternally)
    fun toggleCommentLines()
    fun getNumberAt(): Number
    fun modifyNumber(amount: Number)
    fun removeLines()
    fun moveLinesDown(): Number
    fun moveLinesUp(): Number
    //fun moveText(fromRange: Range, toPosition: Any): Range
    fun copyLinesUp(): Number
    fun copyLinesDown(): Number
    fun getFirstVisibleRow(): Number
    fun getLastVisibleRow(): Number
    fun isRowVisible(row: Number): Boolean
    fun isRowFullyVisible(row: Number): Boolean
    fun selectPageDown()
    fun selectPageUp()
    fun gotoPageDown()
    fun gotoPageUp()
    fun scrollPageDown()
    fun scrollPageUp()
    fun scrollToRow()
    fun scrollToLine(line: Number, center: Boolean, animate: Boolean, callback: Function<*>)
    fun centerSelection()
    fun getCursorPosition(): Position
    fun getCursorPositionScreen(): Number
    fun getSelectionRange(): Range
    fun selectAll()
    fun clearSelection()
    fun moveCursorTo(row: Number, column: Number = definedExternally, animate: Boolean = definedExternally)
    fun moveCursorToPosition(position: Position)
    fun jumpToMatching()
    fun gotoLine(lineNumber: Number, column: Number = definedExternally, animate: Boolean = definedExternally)
    fun navigateTo(row: Number, column: Number)
    fun navigateUp(times: Number = definedExternally)
    fun navigateDown(times: Number = definedExternally)
    fun navigateLeft(times: Number = definedExternally)
    fun navigateRight(times: Number)
    fun navigateLineStart()
    fun navigateLineEnd()
    fun navigateFileEnd()
    fun navigateFileStart()
    fun navigateWordRight()
    fun navigateWordLeft()
    fun replace(replacement: String, options: Any = definedExternally)
    fun replaceAll(replacement: String, options: Any = definedExternally)
    fun getLastSearchOptions(): Any
    fun find(needle: String, options: Any = definedExternally, animate: Boolean = definedExternally)
    fun findNext(options: Any = definedExternally, animate: Boolean = definedExternally)
    fun findPrevious(options: Any = definedExternally, animate: Boolean = definedExternally)
    fun undo()
    fun redo()
    fun destroy()
}

//@JsModule("ace-builds")
//@JsNonModule
//external interface TextMode {
//    fun getTokenizer(): Tokenizer
//    fun toggleCommentLines(state: Any, session: IEditSession, startRow: Number, endRow: Number)
//    fun toggleBlockComment(state: Any, session: IEditSession, range: Range, cursor: Position)
//    fun getNextLineIndent(state: Any, line: String, tab: String): String
//    fun checkOutdent(state: Any, line: String, input: String): Boolean
//    fun autoOutdent(state: Any, doc: Document, row: Number)
//    fun createWorker(session: IEditSession): Any
//    //fun createModeDelegates(mapping: `T$5`)
//    fun transformAction(state: String, action: String, editor: Editor, session: IEditSession, text: String): Any
//    fun getKeywords(append: Boolean = definedExternally): Array<dynamic /* String | RegExp */>
//    //fun getCompletions(state: String, session: IEditSession, pos: Position, prefix: String): Array<Completion>
//}


@JsModule("ace-builds")
@JsNonModule
external interface Tokenizer {
    fun removeCapturingGroups(src: String): String
    fun createSplitterRegexp(src: String, flag: String = definedExternally): RegExp
    fun getLineTokens(line: String, startState: String): Array<TokenInfo>
    fun getLineTokens(line: String, startState: Array<String>): Array<TokenInfo>
}


@JsModule("ace-builds")
@JsNonModule
external interface TokenInfo {
    var type: String
    var value: String
    var index: Number?
        get() = definedExternally
        set(value) = definedExternally
    var start: Number?
        get() = definedExternally
        set(value) = definedExternally
}
