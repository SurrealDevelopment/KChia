@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.elements

import chia.clisp_low_level.ops.EvalError

/**
 * Assume this is a LISP LIST
 * and counts its size
 */
fun SExp.listLen(): Int {
    var v: AtomOrPair = this
    var size = 0
    while (v.pair != null) {
        size += 1
        v = v.pair!!.second
    }
    return size
}


fun SExp.listp(): Boolean {
    return this.pair != null
}

fun SExp.first(): SExp {
    return SExp to (this.pair?.first ?: throw EvalError("First of non cons"))
}

fun SExp.rest(): SExp {
    return SExp to (this.pair?.second ?: throw EvalError("First of non cons"))
}

fun SExp.nullp(): Boolean {
    return this.atom?.isEmpty() ?: false
}

//
fun SExp.asFlatList(): List<UByteArray> {
    val x = if (this.listp()) {
        this.first().asFlatList() + this.rest().asFlatList()
    } else {
        listOf(atom)
    }
    return x.map { it ?: throw IllegalStateException("Null atom is impossible") }

}

// assume this is a lisp list of lists and flatten those accordingly
fun SExp.asFlatListOfLists(): List<List<UByteArray>> {
    val x =  this.map {
        it.asFlatList()
    }
    // dont flatten
    return x.toList()
}