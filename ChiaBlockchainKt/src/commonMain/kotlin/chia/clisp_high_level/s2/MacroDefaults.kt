package chia.clisp_high_level.s2

import chia.clisp_low_level.assemble
import chia.clisp_low_level.elements.SExp

class MacroDefaults(val eval: Evaluator) {
    private val defaultSrc =listOf(
        """
        ; we have to compile this externally, since it uses itself
        ;(defmacro defmacro (name params body)
        ;    (qq (list (unquote name) (mod (unquote params) (unquote body))))
        ;)
        (q . ("defmacro"
           (c (q . "list")
              (c (f 1)
                 (c (c (q . "mod")
                       (c (f (r 1))
                          (c (f (r (r 1)))
                             (q . ()))))
                    (q . ()))))))
        """.trimIndent(),
        """
        ;(defmacro list ARGS
        ;    ((c (mod args
        ;        (defun compile-list
        ;               (args)
        ;               (if args
        ;                   (qq (c (unquote (f args))
        ;                         (unquote (compile-list (r args)))))
        ;                   ()))
        ;            (compile-list args)
        ;        )
        ;        ARGS
        ;    ))
        ;)
        (q "list"
            (a (q #a (q #a 2 (c 2 (c 3 (q))))
                     (c (q #a (i 5
                                 (q #c (q . 4)
                                       (c 9 (c (a 2 (c 2 (c 13 (q))))
                                               (q)))
                                 )
                                 (q 1))
                               1)
                        1))
                1))
        """.trimIndent(),
        """
        (defmacro function (BODY)
        (qq (opt (com (q . (unquote BODY))
                 (qq (unquote (macros)))
                 (qq (unquote (symbols)))))))""${'"'}
        """.trimIndent(),
        """
        (defmacro if (A B C)
        (qq (a
            (i (unquote A)
               (function (unquote B))
               (function (unquote C)))
            @)))""${'"'}
        """.trimIndent()

    )

    private val _defaultMacroLookup by lazy {
        val run = assemble("(a (com 2 3) 1)")
        defaultSrc.fold(SExp to listOf()) { acc: SExp, s: String ->
            val sexp = assemble(s)

            val env = SExp to Pair(sexp, acc)
            val evalResult = eval.evaluate(run, env, null)
            evalResult.second.cons(acc)
        }
    }

    fun defaultMacroLookup(): SExp {

        return _defaultMacroLookup
    }

}