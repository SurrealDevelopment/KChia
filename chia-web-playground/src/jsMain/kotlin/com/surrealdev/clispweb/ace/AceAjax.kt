package com.surrealdev.clispweb.ace

/*
import kotlin.js.*
import org.w3c.dom.*

external interface Delta {
    var action: String /* "insert" | "remove" */
    var start: Position
    var end: Position
    var lines: Array<String>
}

external interface `T$0` {
    var mac: String?
        get() = definedExternally
        set(value) = definedExternally
    var win: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EditorCommand {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var bindKey: dynamic /* String? | `T$0`? */
        get() = definedExternally
        set(value) = definedExternally
    var exec: (editor: Editor, args: Any) -> Unit
    var readOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CommandMap {
    @nativeGetter
    operator fun get(name: String): EditorCommand?
    @nativeSetter
    operator fun set(name: String, value: EditorCommand)
}

external interface `T$1` {
    var editor: Editor
    var command: EditorCommand
    var args: Array<Any>
}

external interface `T$2` {
    @nativeGetter
    operator fun get(s: String): Function<*>?
    @nativeSetter
    operator fun set(s: String, value: Function<*>)
}

external interface `T$3` {
    var key: String
    var hashId: Number
}

external interface `T$4` {
    var command: String
}

external interface CommandManager {
    var byName: CommandMap
    var commands: CommandMap
    fun on(name: String /* "exec" | "afterExec" */, callback: execEventHandler): Function<*>
    fun once(name: String, callback: Function<*>)
    fun setDefaultHandler(name: String, callback: Function<*>)
    fun removeDefaultHandler(name: String, callback: Function<*>)
    fun on(name: String, callback: Function<*>, capturing: Boolean = definedExternally): Function<*>
    fun on(name: String, callback: Function<*>): Function<*>
    fun addEventListener(name: String, callback: Function<*>, capturing: Boolean = definedExternally)
    fun off(name: String, callback: Function<*>)
    fun removeListener(name: String, callback: Function<*>)
    fun removeEventListener(name: String, callback: Function<*>)
    fun exec(command: String, editor: Editor, args: Any): Boolean
    fun toggleRecording(editor: Editor)
    fun replay(editor: Editor)
    fun addCommand(command: EditorCommand)
    fun addCommands(commands: Array<EditorCommand>)
    fun removeCommand(command: EditorCommand, keepCommand: Boolean = definedExternally)
    fun removeCommand(command: EditorCommand)
    fun removeCommand(command: String, keepCommand: Boolean = definedExternally)
    fun removeCommand(command: String)
    fun removeCommands(command: Array<EditorCommand>)
    fun bindKey(key: String, command: EditorCommand, position: Number = definedExternally)
    fun bindKey(key: String, command: EditorCommand)
    fun bindKey(key: String, command: (editor: Editor) -> Unit, position: Number = definedExternally)
    fun bindKey(key: String, command: (editor: Editor) -> Unit)
    fun bindKey(key: `T$0`, command: EditorCommand, position: Number = definedExternally)
    fun bindKey(key: `T$0`, command: EditorCommand)
    fun bindKey(key: `T$0`, command: (editor: Editor) -> Unit, position: Number = definedExternally)
    fun bindKey(key: `T$0`, command: (editor: Editor) -> Unit)
    fun bindKeys(keys: `T$2`)
    fun parseKeys(keyPart: String): `T$3`
    fun findKeyCommand(hashId: Number, keyString: String): String?
    fun handleKeyboard(data: Any, hashId: Number, keyString: String, keyCode: String): dynamic /* Unit | `T$4` */
    fun handleKeyboard(data: Any, hashId: Number, keyString: String, keyCode: Number): dynamic /* Unit | `T$4` */
    fun getStatusText(editor: Editor, data: Any): String
    var platform: String
}

external interface Annotation {
    var row: Number?
        get() = definedExternally
        set(value) = definedExternally
    var column: Number?
        get() = definedExternally
        set(value) = definedExternally
    var text: String
    var type: String
}

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

external interface Position {
    var row: Number
    var column: Number
}

external interface KeyboardHandler {
    var handleKeyboard: Function<*>
}

external interface KeyBinding {
    fun setDefaultHandler(kb: KeyboardHandler)
    fun setKeyboardHandler(kb: KeyboardHandler)
    fun addKeyboardHandler(kb: KeyboardHandler, pos: Number)
    fun removeKeyboardHandler(kb: KeyboardHandler): Boolean
    fun getKeyboardHandler(): KeyboardHandler
    fun onCommandKey(e: Any, hashId: Number, keyCode: Number): Boolean
    fun onTextInput(text: String): Boolean
}

external interface `T$5` {
    @nativeGetter
    operator fun get(key: String): String?
    @nativeSetter
    operator fun set(key: String, value: String)
}

external interface TextMode {
    fun getTokenizer(): Tokenizer
    fun toggleCommentLines(state: Any, session: IEditSession, startRow: Number, endRow: Number)
    fun toggleBlockComment(state: Any, session: IEditSession, range: Range, cursor: Position)
    fun getNextLineIndent(state: Any, line: String, tab: String): String
    fun checkOutdent(state: Any, line: String, input: String): Boolean
    fun autoOutdent(state: Any, doc: Document, row: Number)
    fun createWorker(session: IEditSession): Any
    fun createModeDelegates(mapping: `T$5`)
    fun transformAction(state: String, action: String, editor: Editor, session: IEditSession, text: String): Any
    fun getKeywords(append: Boolean = definedExternally): Array<dynamic /* String | RegExp */>
    fun getCompletions(state: String, session: IEditSession, pos: Position, prefix: String): Array<Completion>
}

external interface OptionProvider {
    fun setOption(optionName: String, optionValue: Any)
    fun setOptions(keyValueTuples: Json)
    fun getOption(name: String): Any
    fun getOptions(optionNames: Array<String> = definedExternally): Json
    fun getOptions(): Json
    fun getOptions(optionNames: Json = definedExternally): Json
}


external interface Anchor {
    fun on(event: String, fn: (e: Any) -> Any)
    fun getPosition(): Position
    fun getDocument(): Document
    fun onChange(e: Any)
    fun setPosition(row: Number, column: Number, noClip: Boolean = definedExternally)
    fun detach()
    fun attach(doc: Document)

}

external interface BackgroundTokenizer {
    var states: Array<Any>
    fun setTokenizer(tokenizer: Tokenizer)
    fun setDocument(doc: Document)
    fun fireUpdateEvent(firstRow: Number, lastRow: Number)
    fun start(startRow: Number)
    fun stop()
    fun getTokens(row: Number): Array<TokenInfo>
    fun getState(row: Number): String
}

external interface Document {
    fun on(event: String, fn: (e: Any) -> Any)
    fun setValue(text: String)
    fun getValue(): String
    fun createAnchor(row: Number, column: Number)
    fun getNewLineCharacter(): String
    fun setNewLineMode(newLineMode: String /* "auto" | "unix" | "windows" */)
    fun getNewLineMode(): String /* "auto" | "unix" | "windows" */
    fun isNewLine(text: String): Boolean
    fun getLine(row: Number): String
    fun getLines(firstRow: Number, lastRow: Number): Array<String>
    fun getAllLines(): Array<String>
    fun getLength(): Number
    fun getTextRange(range: Range): String
    fun getLinesForRange(range: Range): Array<String>
    fun insert(position: Position, text: String): Position
    fun insertLines(row: Number, lines: Array<String>): Position
    fun insertFullLines(row: Number, lines: Array<String>)
    fun insertNewLine(position: Position): Position
    fun insertMergedLines(row: Number, lines: Array<String>): Position
    fun insertInLine(position: Position, text: String): Position
    fun clippedPos(row: Number, column: Number): Position
    fun clonePos(pos: Position): Position
    fun pos(row: Number, column: Number): Position
    fun remove(range: Range): Position
    fun removeInLine(row: Number, startColumn: Number, endColumn: Number): Position
    fun removeLines(firstRow: Number, lastRow: Number): Array<String>
    fun removeFullLines(firstRow: Number, lastRow: Number): Array<String>
    fun removeNewLine(row: Number)
    fun replace(range: Range, text: String): Position
    fun applyDeltas(deltas: Array<Delta>)
    fun revertDeltas(deltas: Array<Delta>)
    fun indexToPosition(index: Number, startRow: Number): Position
    fun positionToIndex(pos: Position, startRow: Number = definedExternally): Number
}

external interface IEditSession : OptionProvider {
    var selection: Selection
    var bgTokenizer: BackgroundTokenizer
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
    fun getSelection(): Selection
    fun getState(row: Number): String
    fun getTokens(row: Number): Array<TokenInfo>
    fun getTokenAt(row: Number, column: Number): TokenInfo?
    fun setUndoManager(undoManager: UndoManager)
    fun getUndoManager(): UndoManager
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
    fun `$mode`(mode: TextMode)
    fun getMode(): TextMode
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

external object EditSession {
}

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
    var commands: CommandManager
    var session: IEditSession
    var selection: Selection
    var renderer: VirtualRenderer
    var keyBinding: KeyBinding
    var container: HTMLElement
    fun onSelectionChange(e: Any)
    fun onChangeMode(e: Any = definedExternally)
    fun execCommand(command: String, args: Any = definedExternally)
    var `$blockScrolling`: Number
    fun setKeyboardHandler(keyboardHandler: String)
    fun getKeyboardHandler(): String
    fun setSession(session: IEditSession)
    fun getSession(): IEditSession
    fun setValue(param_val: String, cursorPos: Number = definedExternally): String
    fun getValue(): String
    fun getSelection(): Selection
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
    fun moveText(fromRange: Range, toPosition: Any): Range
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

external interface EditorChangeEvent {
    var start: Position
    var end: Position
    var action: String
    var lines: Array<Any>
}

external interface PlaceHolder {
    fun on(event: String, fn: (e: Any) -> Any)
    fun setup()
    fun showOtherMarkers()
    fun hideOtherMarkers()
    fun onUpdate()
    fun onCursorChange()
    fun detach()
    fun cancel()

}

external interface IRangeList {
    var ranges: Array<Range>
    fun pointIndex(pos: Position, startIndex: Number = definedExternally)
    fun addList(ranges: Array<Range>)
    fun add(ranges: Range)
    fun merge(): Array<Range>
    fun substractPoint(pos: Position)
}

external object RangeList {
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface Range {
    var startRow: Number
    var startColumn: Number
    var endRow: Number
    var endColumn: Number
    var start: Position
    var end: Position
    fun isEmpty(): Boolean
    fun isEqual(range: Range)
    override fun toString()
    fun contains(row: Number, column: Number): Boolean
    fun compareRange(range: Range): Number
    fun comparePoint(p: Range): Number
    fun containsRange(range: Range): Boolean
    fun intersects(range: Range): Boolean
    fun isEnd(row: Number, column: Number): Boolean
    fun isStart(row: Number, column: Number): Boolean
    fun setStart(row: Number, column: Number)
    fun setEnd(row: Number, column: Number)
    fun inside(row: Number, column: Number): Boolean
    fun insideStart(row: Number, column: Number): Boolean
    fun insideEnd(row: Number, column: Number): Boolean
    fun compare(row: Number, column: Number): Number
    fun compareStart(row: Number, column: Number): Number
    fun compareEnd(row: Number, column: Number): Number
    fun compareInside(row: Number, column: Number): Number
    fun clipRows(firstRow: Number, lastRow: Number): Range
    fun extend(row: Number, column: Number): Range
    fun isMultiLine(): Boolean
    fun clone(): Range
    fun collapseRows(): Range
    fun toScreenRange(session: IEditSession): Range
    fun fromPoints(start: Range, end: Range): Range

    companion object {
        fun fromPoints(pos1: Position, pos2: Position): Range
    }
}

external interface RenderLoop {
}

external interface ScrollBar {
    fun onScroll(e: Any)
    fun getWidth(): Number
    fun setHeight(height: Number)
    fun setInnerHeight(height: Number)
    fun setScrollTop(scrollTop: Number)
}

external interface Search {
    fun set(options: Any): Search
    fun getOptions(): Any
    fun setOptions(An: Any)
    fun find(session: IEditSession): Range
    fun findAll(session: IEditSession): Array<Range>
    fun replace(input: String, replacement: String): String
}

external interface Selection {
    fun on(ev: String, callback: Function<*>)
    fun addEventListener(ev: String, callback: Function<*>)
    fun off(ev: String, callback: Function<*>)
    fun removeListener(ev: String, callback: Function<*>)
    fun removeEventListener(ev: String, callback: Function<*>)
    fun moveCursorWordLeft()
    fun moveCursorWordRight()
    fun fromOrientedRange(range: Range)
    fun setSelectionRange(match: Any)
    fun getAllRanges(): Array<Range>
    fun on(event: String, fn: (e: Any) -> Any)
    fun addRange(range: Range)
    fun isEmpty(): Boolean
    fun isMultiLine(): Boolean
    fun getCursor(): Position
    fun setSelectionAnchor(row: Number, column: Number)
    fun getSelectionAnchor(): Any
    fun getSelectionLead(): Any
    fun shiftSelection(columns: Number)
    fun isBackwards(): Boolean
    fun getRange(): Range
    fun clearSelection()
    fun selectAll()
    fun setRange(range: Range, reverse: Boolean)
    fun selectTo(row: Number, column: Number)
    fun selectToPosition(pos: Any)
    fun selectUp()
    fun selectDown()
    fun selectRight()
    fun selectLeft()
    fun selectLineStart()
    fun selectLineEnd()
    fun selectFileEnd()
    fun selectFileStart()
    fun selectWordRight()
    fun selectWordLeft()
    fun getWordRange()
    fun selectWord()
    fun selectAWord()
    fun selectLine()
    fun moveCursorUp()
    fun moveCursorDown()
    fun moveCursorLeft()
    fun moveCursorRight()
    fun moveCursorLineStart()
    fun moveCursorLineEnd()
    fun moveCursorFileEnd()
    fun moveCursorFileStart()
    fun moveCursorLongWordRight()
    fun moveCursorLongWordLeft()
    fun moveCursorBy(rows: Number, chars: Number)
    fun moveCursorToPosition(position: Any)
    fun moveCursorTo(row: Number, column: Number, keepDesiredColumn: Boolean = definedExternally)
    fun moveCursorToScreen(row: Number, column: Number, keepDesiredColumn: Boolean)
}

external interface Split {
    fun getSplits(): Number
    fun getEditor(idx: Number)
    fun getCurrentEditor(): Editor
    fun focus()
    fun blur()
    fun setTheme(theme: String)
    fun setKeyboardHandler(keybinding: String)
    fun forEach(callback: Function<*>, scope: String)
    fun setFontSize(size: Number)
    fun setSession(session: IEditSession, idx: Number)
    fun getOrientation(): Number
    fun setOrientation(orientation: Number)
    fun resize()
}

external interface TokenIterator {
    fun stepBackward(): Array<String>
    fun stepForward(): String
    fun getCurrentToken(): TokenInfo
    fun getCurrentTokenRow(): Number
    fun getCurrentTokenColumn(): Number
}

external interface Tokenizer {
    fun removeCapturingGroups(src: String): String
    fun createSplitterRegexp(src: String, flag: String = definedExternally): RegExp
    fun getLineTokens(line: String, startState: String): Array<TokenInfo>
    fun getLineTokens(line: String, startState: Array<String>): Array<TokenInfo>
}

external interface UndoManager {
    fun execute(options: Any)
    fun undo(dontSelect: Boolean = definedExternally): Range
    fun redo(dontSelect: Boolean)
    fun reset()
    fun hasUndo(): Boolean
    fun hasRedo(): Boolean
    fun isClean(): Boolean
    fun markClean()
}

external interface VirtualRenderer : OptionProvider {
    var scroller: Any
    var characterWidth: Number
    var lineHeight: Number
    fun setScrollMargin(top: Number, bottom: Number, left: Number, right: Number)
    fun screenToTextCoordinates(left: Number, top: Number)
    fun setSession(session: IEditSession)
    fun updateLines(firstRow: Number, lastRow: Number)
    fun updateText()
    fun updateFull(force: Boolean)
    fun updateFontSize()
    fun onResize(force: Boolean, gutterWidth: Number, width: Number, height: Number)
    fun adjustWrapLimit()
    fun setAnimatedScroll(shouldAnimate: Boolean)
    fun getAnimatedScroll(): Boolean
    fun setShowInvisibles(showInvisibles: Boolean)
    fun getShowInvisibles(): Boolean
    fun setShowPrintMargin(showPrintMargin: Boolean)
    fun getShowPrintMargin(): Boolean
    fun setPrintMarginColumn(showPrintMargin: Boolean)
    fun getPrintMarginColumn(): Boolean
    fun getShowGutter(): Boolean
    fun setShowGutter(show: Boolean)
    fun getContainerElement(): HTMLElement
    fun getMouseEventTarget(): HTMLElement
    fun getTextAreaContainer(): HTMLElement
    fun getFirstVisibleRow(): Number
    fun getFirstFullyVisibleRow(): Number
    fun getLastFullyVisibleRow(): Number
    fun getLastVisibleRow(): Number
    fun setPadding(padding: Number)
    fun getHScrollBarAlwaysVisible(): Boolean
    fun setHScrollBarAlwaysVisible(alwaysVisible: Boolean)
    fun updateFrontMarkers()
    fun updateBackMarkers()
    fun addGutterDecoration()
    fun removeGutterDecoration()
    fun updateBreakpoints()
    fun setAnnotations(annotations: Array<Any>)
    fun updateCursor()
    fun hideCursor()
    fun showCursor()
    fun scrollCursorIntoView()
    fun getScrollTop(): Number
    fun getScrollLeft(): Number
    fun getScrollTopRow(): Number
    fun getScrollBottomRow(): Number
    fun scrollToRow(row: Number)
    fun scrollToLine(line: Number, center: Boolean, animate: Boolean, callback: Function<*>)
    fun scrollToY(scrollTop: Number): Number
    fun scrollToX(scrollLeft: Number): Number
    fun scrollBy(deltaX: Number, deltaY: Number)
    fun isScrollableBy(deltaX: Number, deltaY: Number): Boolean
    fun textToScreenCoordinates(row: Number, column: Number): Any
    fun visualizeFocus()
    fun visualizeBlur()
    fun showComposition(position: Number)
    fun setCompositionText(text: String)
    fun hideComposition()
    fun setTheme(theme: String)
    fun getTheme(): String
    fun setStyle(style: String)
    fun unsetStyle(style: String)
    fun destroy()

}

external interface Completer {
    var getCompletions: (editor: Editor, session: IEditSession, pos: Position, prefix: String, callback: CompletionCallback) -> Unit
    var getDocTooltip: ((item: Completion) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Completion {
    var value: String
    var meta: String
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
    var caption: String?
        get() = definedExternally
        set(value) = definedExternally
    var snippet: Any?
        get() = definedExternally
        set(value) = definedExternally
    var score: Number?
        get() = definedExternally
        set(value) = definedExternally
    var exactMatch: Number?
        get() = definedExternally
        set(value) = definedExternally
    var docHTML: String?
        get() = definedExternally
        set(value) = definedExternally
}*/