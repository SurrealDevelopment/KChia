@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.ir

import chia.clisp_low_level.ToSexpFun
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.toImp
import com.ionspin.kotlin.bignum.integer.BigInteger
import util.hexstring.asHexString


typealias Token = Pair<String, Int>
class TokenReader(val s: String, val toSexpFun: ToSexpFun): Sequence<Token> {

    override fun iterator(): Iterator<Token> {
        return TokenIterator(s, toSexpFun)
    }
}


private fun nextConsToken(stream: Iterator<Token>): Token {
    try {
        return stream.next()
    } catch (e: Exception) {
        throw Exception("Missing )")
    }
}

private fun tokenizeCons(token: String, offset: Int, stream: Iterator<Token>): SExp {
    if (token == ")") {
        return Utils.irNew(TypeAtom.NULL.value, 0, offset)
    }
    val initialoffset = offset

    val firstSexp = tokenizeSexp(token, offset, stream)

    var tokenOffset = nextConsToken(stream)
    val restSexp: SExp
    if (tokenOffset.first == ".") {
        val dotOffset = tokenOffset.second
        // grab last item
        tokenOffset = nextConsToken(stream)
        restSexp = tokenizeSexp(tokenOffset.first, tokenOffset.second, stream)
        tokenOffset = nextConsToken(stream)
        if (tokenOffset.first != ")") {
            throw Exception("illegal dot expression $dotOffset")
        }
    } else {
        restSexp = tokenizeCons(tokenOffset.first, tokenOffset.second, stream)
    }
    return Utils.irCons(firstSexp, restSexp, initialoffset)

}

private fun tokenizeInt(token: String, offset: Int): SExp? {
    return try {
        Utils.irNew(TypeAtom.INT.value, BigInteger.parseString(token, 10), offset)
    } catch (e: Exception) {
        null
    }
}

private fun tokenizeHex(s: String, offset: Int): SExp? {
    var token: String
    if(s.take(2).uppercase() == "0X") {
        try {
            token = s.drop(2)
            if (token.length % 2 == 1) {
                token = "0" + token
            }
            return Utils.irNew(TypeAtom.HEX.value, token.asHexString().toUByteArray(), offset)
        }
        catch (e: Exception) {
            throw Exception("Invalid hex format: $offset", e)
        }
    }
    return null
}

private fun tokenizeQuotes(token: String, offset: Int): SExp? {
    if (token.length < 2) return null
    val c = token.first()
    if (c !in "\'\"") return null

    if (token.last() != c) {
        throw Exception("Underminated string starting at $offset.")
    }
    val qType = if (c == '\'') TypeAtom.SINGLE_QUOTE else TypeAtom.DOUBLE_QUOTE

    return SExp to Pair(Pair
        (qType, offset),
        token.drop(1).dropLast(1).encodeToByteArray().toUByteArray()
    )
}

private fun tokenizeSymbol(token: String, offset: Int): SExp =
    SExp to Pair(Pair(TypeAtom.SYMBOL, offset), token.encodeToByteArray().toUByteArray())

private fun tokenizeSexp(token: String, offset: Int, stream: Iterator<Token>): SExp {
    if (token == "(") {
        val next = nextConsToken(stream)
        return tokenizeCons(next.first, next.second, stream)
    }
    for (f in listOf(::tokenizeInt, ::tokenizeHex, ::tokenizeQuotes, ::tokenizeSymbol)) {
        val r = f(token, offset)
        if (r != null) {
            return r
        }
    }

    throw IllegalStateException("No symbol fall through")
}

class TokenIterator(var s: String, val toSexpFun: ToSexpFun): Iterator<Token> {

    private var offset = 0
    override fun hasNext(): Boolean {
        return offset < s.length
    }

    private fun consumeWhitespace() {
        while (true) {
            while(offset < s.length && s[offset].isWhitespace()) {
                offset += 1
            }
            if(offset >= s.length || s[offset] != ';') {
                break
            }
            while(offset < s.length && (s[offset] != '\n' && s[offset] != '\r')) {
                offset+= 1
            }
        }
    }

    private fun consumeUntilWhitespace(): Token {
        val start = offset
        while (offset < s.length && !(s[offset].isWhitespace()) && s[offset] != ')')
            offset += 1
        return s.slice(start until offset) to offset
    }


    override fun next(): Token {
        consumeWhitespace()
        if (offset >= s.length) {
            throw IllegalStateException()
        }
        val c = s[offset]
        if (c in "(.)") {
            offset += 1
            return c.toString() to offset - 1
        }
        if (c in "\"'") {
            val start = offset
            val initialC = s[start]
            offset += 1
            while (offset < s.length && s[offset] != initialC) {
                offset += 1
            }
            if (offset < s.length) {
                offset += 1
                return s.substring(start until offset) to start
            } else {
                throw Exception("Unterminated string starting at $start")
            }
        }
        val start = offset
        val result = consumeUntilWhitespace()
        offset = result.second
        return Pair(result.first, start)
    }

}


fun readIr(s: String, toSexpFun: ToSexpFun = { SExp.toImp(it)}): SExp{
    val stream = TokenReader(s, toSexpFun).iterator()
    stream.forEach {
        val tokenize = tokenizeSexp(it.first, it.second, stream)

        return toSexpFun(tokenize)
    }

    throw Exception("unexpected end of stream")
}