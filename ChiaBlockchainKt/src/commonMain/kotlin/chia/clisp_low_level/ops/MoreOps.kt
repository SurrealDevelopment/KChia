package chia.clisp_low_level.ops

import bls.PrivateKey
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.rest
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import util.crypto.Sha256
import util.extensions.bytesRequired
import util.extensions.readUInt

@Suppress("EXPERIMENTAL_API_USAGE")
internal object MoreOps {

    fun mallocCost(cost: BigInteger, atom: SExp): OpRet = Pair(
        cost + atom.atom!!.size * Costs.MALLOC_COST_PER_BYTE,
        atom
    )

    val opSha256 = buildOp(11, "sha256") { args ->
        var cost = Costs.SHA256_BASE_COST.toBigInteger()
        var argLen = 0
        val bytes = args.fold(ubyteArrayOf()) { acc, it ->
            if (it.atom == null) {
                throw EvalError("Sha256 on list")
            }
            argLen += it.atom!!.size
            cost += Costs.SHA256_COST_PER_ARG
            acc + it.atom!!
        }
        val h = Sha256()
        cost += argLen * Costs.SHA256_COST_PER_BYTE
        mallocCost(cost, SExp to h.digest(bytes))
    }

    // Pair of atom as Uint and size of that atom's underlying structure in bytes
    fun argsAsUInt32(opName: String, args: SExp): Sequence<Pair<UInt, Int>> {
        return args.map {
            if (it.pair != null) throw EvalError("$opName needs int")

            if (it.atom!!.size > 4) {
                throw EvalError("$opName too big for int32")
            }
            Pair(it.atom!!.readUInt(), it.atom!!.size)
        }
    }

    // Pair of atom as BigInteger and size of that atom's underlying structure in bytes
    fun argsAsBig(opName: String, args: SExp): Sequence<Pair<BigInteger, Int>> {
        return args.map {
            if (it.pair != null) throw EvalError("$opName needs int")
            Pair(BigInteger.fromTwosComplementByteArray(it.atom!!.toByteArray()), it.atom!!.size)
        }
    }

    // Pair of atom as int and size of that atom's underlying structure in bytes
    fun argsAsInt32(opName: String, args: SExp): Sequence<Pair<Int, Int>> {
        return argsAsUInt32(opName, args).map {
            Pair(it.first.toInt(), it.second)
        }
    }

    //  Gives list of
    //  Pair of atom as Uint and size of that atom's underlying structure in bytes
    fun argsAsBigList(opName: String, args: SExp, count: Int): Sequence<Pair<BigInteger, Int>> {
        val list = argsAsBig(opName, args)
        if (list.count() != count) {
            throw EvalError("$opName takes exactly $count arguments.")
        }
        return list
    }

    fun argsAsBools(opName: String, args: SExp): Sequence<SExp> {
        return args.map {
            if (it.pair != null) throw EvalError("$opName not expecting pair $args")
            val bytes = it.atom!!
            if (SExp.__false__.equals(bytes)) SExp.__false__
            else SExp.__true__
        }
    }

    fun argsAsBoolList(opName: String, args: SExp, count: Int): Sequence<SExp> {
        val list = argsAsBools(opName, args)
        if (list.count() != count) {
            throw EvalError("$opName takes exactly $count arguments.")
        }
        return list
    }

    val opAdd = buildOp(0x10, "+", "add") { args ->
        var total = BigInteger(0L)
        var cost = Costs.ARITH_BASE_COST.toBigInteger()
        var argSize = 0
        argsAsBig("+", args).forEach {
            total += it.first
            argSize += it.second
            cost += Costs.ARITH_COST_PER_ARG
        }
        cost += argSize * Costs.ARITH_COST_PER_BYTE
        mallocCost(cost, SExp to total)
    }

    val opSubtract = buildOp(0x11, "-", "subtract") { args ->
        var total = BigInteger(0L)
        var cost = Costs.ARITH_BASE_COST.toBigInteger()
        var argSize = 0
        var sign = 1
        argsAsBig("-", args).forEach {
            total += it.first * sign
            sign = -1
            argSize += it.second
            cost += Costs.ARITH_COST_PER_ARG
        }
        cost += argSize * Costs.ARITH_COST_PER_BYTE
        mallocCost(cost, SExp to total)
    }

    val opMultiply = buildOp(0x12, "*", "multiply") { args ->
        var cost = Costs.MUL_BASE_COST.toBigInteger()
        val operands = argsAsBig("*", args)
        if (operands.count() == 0) {
            mallocCost(cost, SExp to 1)
        }

        val v = operands.first()
        var num = v.first
        var vs = v.second

        operands.drop(1).forEach { r ->
            cost += Costs.MUL_COST_PER_OP
            cost += (r.second + vs) * Costs.MUL_LINEAR_COST_PER_BYTE
            cost += (r.second * vs) / Costs.MUL_SQUARE_COST_PER_BYTE_DIVIDER
            num *= r.first
            vs = num.bytesRequired()
        }
        mallocCost(cost, SExp to num)
    }

    val opDivMod = buildOp(0x14, "divmod") { args ->
        var cost = Costs.DIVMOD_BASE_COST.toBigInteger()
        val i = argsAsBigList("divmod", args, 2).toList()

        if (i[1].first == BigInteger.ZERO) {
            throw EvalError("Divmod by 0 ${args}")
        }
        cost += (i[0].second + i[1].second) * Costs.DIVMOD_COST_PER_BYTE
        val q = i[0].first / i[1].first
        val r = i[0].first.rem(i[1].first)
        val q1 = SExp to q
        val r1 = SExp to r
        cost += q1.atom!!.size + r1.atom!!.size * Costs.MALLOC_COST_PER_BYTE

        cost to (SExp to Pair(q, r))
    }

    val opDiv = buildOp(0x13, "/", "div") { args ->
        var cost = Costs.DIV_BASE_COST.toBigInteger()
        val i = argsAsBigList("/", args, 2).toList()

        if (i[1].first == BigInteger.ZERO) {
            throw EvalError("div by 0 ${args}")
        }

        cost += (i[0].second + i[1].second) * Costs.DIV_COST_PER_BYTE
        val q = i[0].first / i[1].first
        mallocCost(cost, SExp to q)
    }

    val opGr = buildOp(0x15, ">", "gr") { args ->
        val i = argsAsBigList(">", args, 2).toList()
        val i0 = i[0].first
        val l0 = i[0].second
        val i1 = i[1].first
        val l1 = i[1].second
        var cost = Costs.GR_BASE_COST.toBigInteger()
        cost += (l0 + l1) * Costs.GR_COST_PER_BYTE
        cost to (if (i0 > i1) SExp.__true__ else SExp.__false__)
    }

    val opGrBytes = buildOp(0x0A, ">s", "gr_bytes") { args ->
        val argList = args.toList()
        if (argList.size != 2) {
            throw EvalError(">s takes exactly 2 arugments")
        }
        val a0 = argList[0]
        val a1 = argList[1]
        if (a0.pair != null || a1.pair != null) {
            throw EvalError(">s on list")
        }
        val b0 = a0.atom!!
        val b1 = a1.atom!!
        var cost = Costs.GRS_BASE_COST.toBigInteger()
        cost += (b0.size + b1.size) * Costs.GRS_COST_PER_BYTE
        cost to (if (BigInteger.fromUByteArray(b0, Sign.POSITIVE) > BigInteger.fromUByteArray(b1, Sign.POSITIVE))
            SExp.__true__ else SExp.__false__)
    }

    val opPubkeyForExp = buildOp(0x1E, "pubkey_for_exp") { args ->
        val i = argsAsBigList("pubkey_for_exp", args, 1).toList()
        var i0 = i[0].first
        val l0 = i[0].second

        i0 = i0.mod(BigInteger.parseString("73EDA753299D7D483339D80809A1D80553BDA402FFFE5BFEFFFFFFFF00000001"))
        val exponent = PrivateKey.fromByteArray(i0.toByteArray())
        try {
            val r = SExp to exponent.getG1().toByteArray()
            val cost = Costs.PUBKEY_BASE_COST.toBigInteger() + Costs.PUBKEY_COST_PER_BYTE * l0
            mallocCost(cost, r)

        } catch( e: Exception) {
            throw EvalError("op_pubkey_for_exp")
        }
    }

    val opPointAdd = buildOp(0x1D, "point_add") { args ->
        var cost = Costs.POINT_ADD_BASE_COST.toBigInteger()
        val result = args.fold(ByteArray(0)) { acc, sexp ->
            if (sexp.pair != null) {
                throw EvalError("Cannot add lisp list here")
            }
            cost += Costs.POINT_ADD_COST_PER_ARG
            acc + sexp.atom!!.toByteArray()
        }
        mallocCost(cost, SExp to result)
    }

    val opStrLen = buildOp(0x0D, "point_add") { args ->
        if (args.count() != 1) {
            throw EvalError("String Len takes one argument. Got $args")
        }
        val a0 = args.first()
        if (a0.pair != null) {
            throw EvalError("String Len does not work on a list $args")
        }
        val size = a0.atom!!.size
        val cost = Costs.STRLEN_BASE_COST.toBigInteger() + Costs.STRLEN_COST_PER_BYTE.toBigInteger() * size
        mallocCost(cost, SExp to size)
    }

    // start is inclusive stop is non inclusive
    // (substr (str (start ( stop))) -> (cost ( outString, null))
    val opSubStr = buildOp(0x0C, "point_add") { args ->
        val argCounts = args.count()
        if (argCounts != 2 && argCounts != 3) {
            throw EvalError("Substr takes 2 or 3 arguments not $argCounts")
        }
        val a0 = args.first()
        if (a0.pair != null) {
            throw EvalError("Substr on list")
        }
        val s0 = a0.atom!!
        val res = if (argCounts == 2) {
            argsAsInt32("substr", args.rest()).first().first to
                    s0.size
        } else {
            val ints = argsAsInt32("substr", args.rest()).toList()
            ints[0].first to ints[1].first
        }
        if (res.second > s0.size || res.second < res.first ||  res.second < 0 || res.first < 0) {
            throw EvalError("Invalid substr arguments: $args")
        }
        val substr = s0.slice(res.first until res.second)
        BigInteger.ONE to (SExp to substr)
    }
    val opConcat = buildOp(0x0E, "concat") { args ->
        var cost = Costs.CONCAT_BASE_COST.toBigInteger()
        var s = UByteArray(0)
        args.forEach {
            if (it.pair != null) throw EvalError("concat on list: $args")
            s = s + it.atom!!
            cost += Costs.CONCAT_COST_PER_ARG
        }
        cost += s.size * Costs.CONCAT_COST_PER_BYTE
        mallocCost(cost, SExp to s)
    }

    val opAsh = buildOp(0x16, "ash") { args ->
        val argList = argsAsBigList("ash", args, 2).toList()
        val i0 = argList[0].first
        val l0 = argList[0].second
        val i1 = argList[1].first
        val l1 = argList[1].second
        if (l1 > 4) throw EvalError("Ash requires int32 args with no leadings 0s $argList")
        if (i1 > 65535) throw EvalError("Shifts too large ${SExp to i1}")
        val shift = i1.intValue(true)
        val r = if (i1 >= 0) i0 shl shift else i0 shr (-shift)
        val cost = Costs.ASHIFT_BASE_COST.toBigInteger() +
                Costs.ASHIFT_COST_PER_BYTE.toBigInteger() * (l0.bytesRequired())
        mallocCost(cost, SExp to r)
    }

    val opLsh = buildOp(0x17, "lsh") { args ->
        val argList = argsAsBigList("lsh", args, 2).toList()
        val l0 = argList[0].second
        val i1 = argList[1].first
        val l1 = argList[1].second
        if (l1 > 4) throw EvalError("Ash requires int32 args with no leadings 0s $argList")
        if (i1 > 65535) throw EvalError("Shifts too large ${SExp to i1}")

        // some unsigned number
        val i0 = BigInteger.fromUByteArray(args.first().atom!!, Sign.POSITIVE).intValue(true)

        val shift = i1.intValue(true)
        val r = if (i1 >= 0) i0 shl shift else i0 shr (-shift)
        val cost = Costs.LSHIFT_BASE_COST.toBigInteger() +
                Costs.LSHIFT_COST_PER_BYTE.toBigInteger() * (l0.bytesRequired())
        mallocCost(cost, SExp to r)
    }


    fun binopReduction(opName: String, initialValue: BigInteger, args: SExp,
                       opF: (BigInteger, BigInteger) -> BigInteger ): OpRet {
        var total = initialValue
        var argSize = 0
        var cost = Costs.LOG_BASE_COST.toBigInteger()
        argsAsBig(opName, args, ).forEach {
            total = opF(total, it.first)
            argSize += it.second
            cost += Costs.LOG_COST_PER_ARG
        }
        cost += Costs.LOG_COST_PER_BYTE * argSize
        return mallocCost(cost, SExp to total)
    }

    val opLogand = buildOp(0x18, "logand") { args ->
        val binop: (BigInteger, BigInteger) -> BigInteger = { a,b ->
            a.and(b)
        }
        binopReduction("logand", BigInteger(-1), args, binop)
    }

    val opLogior = buildOp(0x19, "logior") { args ->
        val binop: (BigInteger, BigInteger) -> BigInteger = { a,b ->
            a.or(b)
        }
        binopReduction("logior", BigInteger(-1), args, binop)
    }

    val opLogxor = buildOp(0x1A, "logxor") { args ->
        val binop: (BigInteger, BigInteger) -> BigInteger = { a,b ->
            a.xor(b)
        }

        binopReduction("logxor", BigInteger(-1), args, binop)
    }

    val opLogNot = buildOp(0x1B, "lognot") { args ->
        val arg = argsAsBigList("lognot", args, 1).first()
        val cost = Costs.LOGNOT_BASE_COST.toBigInteger() + arg.second * Costs.LOGNOT_COST_PER_BYTE
        mallocCost(cost, SExp to arg.first.not())
    }

    val opNot = buildOp(0x20, "not") { args ->
        val arg = argsAsBoolList("not", args, 1).first()
        val r = if (arg.first() == SExp.__null__) {
            SExp.__true__
        } else {
            SExp.__false__
        }
        Costs.BOOL_BASE_COST.toBigInteger() to (SExp to r)
    }

    val opAny = buildOp(0x21, "any") { args ->
        val items = argsAsBools("any", args)
        val cost = Costs.BOOL_BASE_COST.toBigInteger() + Costs.BOOL_COST_PER_ARG * items.count()

        var r = SExp.__false__
        items.find {
            it != SExp.__null__
        }?.let {
            r = SExp.__true__
        }

        cost to (SExp to r)
    }

    val opAll = buildOp(0x22, "all") { args ->
        val items = argsAsBools("all", args)
        val cost = Costs.BOOL_BASE_COST.toBigInteger() + Costs.BOOL_COST_PER_ARG * items.count()

        var r = SExp.__true__
        items.find {
            it == SExp.__null__
        }?.let {
            r = SExp.__false__
        }
        cost to (SExp to r)
    }

    val opSoftFork = buildOp(0x24, "softfork") { args ->
        if (args.count() < 1) throw EvalError("Soft fork requires at least 1 arg. $args")
        val a = args.first()
        if (a.pair != null) throw EvalError("soft fork requires bigint args")
        val cost = BigInteger.fromUByteArray(a.atom!!, Sign.POSITIVE)
        if (cost < 1) throw EvalError("cost must be > 0: $args")
        cost to SExp.__false__
    }

    val list = listOf(opSha256, opAdd, opSubtract, opMultiply, opDiv, opDivMod, opGr, opAsh, opLsh,
        opLogand, opConcat, opLogior, opLogxor, opLogNot, opPointAdd, opPubkeyForExp, opNot, opAny, opAll,
        opSoftFork, opSubStr, opStrLen, opGrBytes
    )



}