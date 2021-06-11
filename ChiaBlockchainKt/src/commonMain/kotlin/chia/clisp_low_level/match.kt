@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level

import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.listp
import chia.clisp_low_level.elements.rest

private val ATOM_MATCH = SExp to "$".encodeToByteArray().toUByteArray()
private val SEXP_MATCH = SExp to ":".encodeToByteArray().toUByteArray()

private fun unifyBindings(bindings: Map<String, SExp>, iNewKey: UByteArray, newValue: SExp): Map<String, SExp>? {
    val newKey = iNewKey.toByteArray().decodeToString()
    if (newKey in bindings) {
        if (bindings[newKey] != newValue) {
            return null
        }
        return bindings
    }
    val newBindigs = HashMap(bindings)
    newBindigs[newKey] = newValue
    return newBindigs

}

fun match(pattern: SExp, sexp: SExp, knownBindings: Map<String, SExp> = mapOf()): Map<String, SExp>? {
    if(!pattern.listp()) {
        if (sexp.listp()){
            return null
        }
        return if (pattern.atom.contentEquals(sexp.atom)) knownBindings else null
    }

    val left = pattern.first()
    val right = pattern.rest()
    if (left == ATOM_MATCH) {
        if (sexp.listp()) return null
        val atom = sexp.atom!!

        if (right ==  ATOM_MATCH) {
            if (atom.contentEquals(ATOM_MATCH.atom!!)) {
                return mapOf<String,SExp>()
            }
            return null
        }
        return unifyBindings(knownBindings, right.atom!!, sexp)
    }

    if (left == SEXP_MATCH) {
        if (right == SEXP_MATCH) {
            val atom = sexp.atom!!
            if (atom.contentEquals(SEXP_MATCH.atom!!)) {
                return mapOf<String,SExp>()
            }
            return null
        }
        return unifyBindings(knownBindings, right.atom!!, sexp)
    }

    if (!sexp.listp()) return null

    val newBindsings = match(left, sexp.first(), knownBindings)
    if (newBindsings == null) {
        return newBindsings
    } else {
        return match(right, sexp.rest(), newBindsings)
    }
}