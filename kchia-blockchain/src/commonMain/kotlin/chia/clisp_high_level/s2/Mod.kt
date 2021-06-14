@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_high_level.s2

import chia.clisp_low_level.NodePath
import chia.clisp_high_level.s2.S2Compile.eval
import chia.clisp_high_level.s2.S2Compile.quote
import chia.clisp_low_level.assemble
import chia.clisp_low_level.dissasemble
import chia.clisp_low_level.elements.*
import chia.clisp_low_level.ops.EvalError
import chia.clisp_low_level.ops.Operators
import kotlin.sequences.first

/**
 * The mod op.
 */
object Mod {


    val consKw = Operators.KEYWORD_TO_ATOM["c"]!!
    var mainName = ""


    private fun buildTree(items: List<String>): SExp {
        val size = items.size
        if (size == 0) return SExp.__null__
        if (size == 1) return SExp to items.first()
        val half = size / 2
        val left = buildTree(items.slice(0 until half))
        val right = buildTree(items.slice( half until size))
        return SExp to Pair(left, right)
    }

    private fun buildTreeProgram(items: List<SExp>): SExp {
        val size = items.size
        if (size == 0) return SExp.__null__
        if (size == 1) return items[0]
        val half = size / 2
        val left = buildTreeProgram(items.slice(0 until half))
        val right = buildTreeProgram(items.slice( half until size))
        return SExp to listOf(consKw, left, right)
    }

    private fun buildUsedConstantsNames(functions: HashMap<String, SExp>, constants: HashMap<String, SExp>,
                                        macros: List<SExp>): Set<String> {
        val macrosAsDict = macros.map { Pair(it.rest().first().asString(), it) }.toMap()

        var possibleSymbols = functions.keys.toSet()
        possibleSymbols = possibleSymbols.union(constants.keys)

        var newNames = setOf("")
        var usedNames = setOf("")

        while (newNames.isNotEmpty()) {
            val priorNewNames = newNames
            newNames = HashSet()
            priorNewNames.forEach { prior ->
                if (prior in functions) {
                    val flat = functions[prior]!!.asFlatList().map { it.toByteArray().decodeToString() }
                    newNames = newNames.union(flat)

                }
                if (prior in macrosAsDict) {
                    val flat = macrosAsDict[prior]!!.asFlatList().map { it.toByteArray().decodeToString() }
                    newNames = newNames.union(flat)
                }

            }
            newNames = newNames.subtract(usedNames).toHashSet()
            usedNames = usedNames.union(newNames)
        }
        usedNames = usedNames.intersect(possibleSymbols).toHashSet()

        return usedNames.minus("")


    }

    private fun parseInclude(name: SExp, namespace: HashSet<String>,
                             functions: HashMap<String, SExp>,
                             constants: HashMap<String, SExp>,
                             macros: MutableList<SExp>, runProgram: Evaluator) {
        val prog = assemble("(_read (_full_path_for_name 1))")
        val result = runProgram.evaluate(prog, name, null)
        result.second.forEach {
            parseModSexp(it, namespace,  functions, constants, macros, runProgram)
        }
    }

    private fun unquoteArgs(code: SExp, args: SExp): SExp {
        if (code.listp()) {
            val c1 = code.first()
            val c2 = code.rest()
            return unquoteArgs(c1,args).cons(unquoteArgs(c2, args))
        }
        if (code in args) {
            return SExp to listOf("unquote".encodeToByteArray(), code)
        }
        return code
    }

    private fun defunInlineToMacro(declerationSexp: SExp): SExp {
        val d2 = declerationSexp.rest()
        val d3 = d2.rest()
        val r = mutableListOf(SExp to "defmacro".encodeToByteArray(), d2.first(), d3.first())
        val code = d3.rest().first()
        val args = (SExp to d3.first()).asFlatList()
        val unquotedCode = unquoteArgs(code, SExp to args)
        r.add(SExp to "qq".encodeToByteArray())
        r.add(unquotedCode)
        return SExp to r

    }
    private fun parseModSexp(declerationSexp: SExp,
                             namespace: HashSet<String>,
                             functions: HashMap<String, SExp>,
                             constants: HashMap<String, SExp>,
                             macros: MutableList<SExp>, runProgram: Evaluator) {
        val op = declerationSexp.first().atom!!
        val name = declerationSexp.rest().first()
        if (op.contentEquals("include".encodeToByteArray().toUByteArray())) {
            parseInclude(name, namespace, functions, constants, macros, runProgram)
            return
        }
        val nameStr = name.asString()
        if (namespace.contains(nameStr)) {
            throw EvalError("Symbol $nameStr already defined")
        }
        namespace.add(nameStr)
        if (op.contentEquals("defmacro".encodeToByteArray().toUByteArray())) {
            macros.add(declerationSexp)
            return
        }
        else if (op.contentEquals("defun".encodeToByteArray().toUByteArray())) {
            functions[nameStr] = declerationSexp.rest().rest()
            return
        }
        else if (op.contentEquals("defun-inline".encodeToByteArray().toUByteArray())) {
            macros.add(defunInlineToMacro(declerationSexp))
            return
        }
        else if (op.contentEquals("defconstant".encodeToByteArray().toUByteArray())) {
            constants[nameStr] = SExp to quote(declerationSexp.rest().rest().first())
            return
        }
        else {
            throw EvalError("Expected fun, defun, or defconstant")
        }

    }


    data class Stage1Result(
        val functions: HashMap<String, SExp>,
        val constants: HashMap<String, SExp>,
        val macros: ArrayList<SExp>
    )

    fun compileModStage1(iargs: SExp, runProgram: Evaluator): Stage1Result {
        val functions = HashMap<String, SExp>()
        val constants = HashMap<String, SExp>()
        val macros = ArrayList<SExp>()
        val mainLocalArguments = iargs.first()

        var args = iargs
        val namespace = HashSet<String>()
        while (true) {
            args = args.rest()
            if (args.rest().nullp()) break
            parseModSexp(args.first(), namespace, functions, constants, macros, runProgram)
        }

        val uncompiledMain = args.first()

        functions[mainName] = SExp to listOf(mainLocalArguments, uncompiledMain)

        return Stage1Result(functions, constants, macros)
    }

    private fun buildMacroLookupProgram(macroLookup: SExp, macros: MutableList<SExp>, runProgram: Evaluator): SExp {
        var macroLookupProgram = SExp to quote(macroLookup)
        macros.forEach {
            macroLookupProgram = eval(
                SExp to listOf(
                "opt",
                listOf("com",
                    quote(listOf(
                        consKw,
                        it,
                        macroLookupProgram)),
                    macroLookupProgram
                )),
                NodePath.TOP.shortPath
            )
            macroLookupProgram = S2Optimize.optimizeSexp(macroLookupProgram, runProgram)
        }
        return macroLookupProgram
    }

    private fun symbolTableForTree(tree: SExp, rootNode: NodePath): List<List<SExp>> {
        if (tree.nullp()) return listOf()

        if (!tree.listp())
            return listOf(
                listOf(tree
                , SExp to rootNode.shortPath))

        val left = symbolTableForTree(tree.first(), rootNode + NodePath.LEFT)
        val right = symbolTableForTree(tree.rest(), rootNode + NodePath.RIGHT)

        return left + right
    }

    private fun compileFunctions(functions: HashMap<String, SExp>,
                                 macroLookupProgram: SExp,
                                 constantsSymbolTable: List<List<SExp>>, argsRootNode: NodePath
    ): HashMap<String, SExp> {
        val compiledFunctions = HashMap<String, SExp>()

        functions.forEach {
            val localSymbolTable = symbolTableForTree(it.value.first(), argsRootNode)
            val allSymbols = localSymbolTable + constantsSymbolTable
            compiledFunctions[it.key] = SExp to listOf(
                "opt",
                listOf(
                    "com",
                    quote(it.value.rest().first()),
                    macroLookupProgram,
                    quote(allSymbols)
                )
            )
        }
        return compiledFunctions
    }

    val compileMod = CompileBinding("mod") { args, macroLookup, symbolTable, runProgram, level ->
        val s1result = compileModStage1(args, runProgram)
        // move macros into macro lookup
        val macroLookupProgram = buildMacroLookupProgram(macroLookup, s1result.macros, runProgram)

        // get a list of all symbols that are possibly used
        val allConstantsNames = buildUsedConstantsNames(s1result.functions, s1result.constants, s1result.macros)
        val hasConstantTree = allConstantsNames.isNotEmpty()

        // build defuns table, with function names as keys
        val constantsTree = SExp to buildTree(allConstantsNames.toList())

        val constantsRootNode = NodePath.LEFT
        val argsRootNode = if (hasConstantTree) {
            NodePath.RIGHT
        } else {
            NodePath.TOP
        }

        val constantsSymbolTable = symbolTableForTree(constantsTree, constantsRootNode)

        val compiledFunctions = compileFunctions(
            s1result.functions, macroLookupProgram, constantsSymbolTable, argsRootNode
        )
        val mainPathSrc = dissasemble(compiledFunctions[""] ?: throw EvalError("No main?"))

        val argTreeSrc = if (hasConstantTree) {
            val allConstantsLookup = compiledFunctions.filter {
                it.key in allConstantsNames
            }.toMutableMap()
            allConstantsLookup.putAll(s1result.constants)

            val allConstantsLists = allConstantsNames.map { allConstantsLookup[it]!! }
            val allConstnatsTreeProgram = SExp to buildTreeProgram(allConstantsLists)

            val allConstantsTreeSource = dissasemble(allConstnatsTreeProgram)
            "(c $allConstantsTreeSource 1)"

        } else {
            "1"
        }

        val mainCode = "(opt (q . (a $mainPathSrc $argTreeSrc)))"
        //if (hasConstantTree) {
            // buildSymbolDump() TODO debug stuff
        //}
        val assm = assemble(mainCode)
        return@CompileBinding assm
    }


}