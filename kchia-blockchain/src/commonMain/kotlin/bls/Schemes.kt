package bls


@ExperimentalUnsignedTypes
object Core {

    fun coreSignMpl(sk: PrivateKey, message: ByteArray, dst: ByteArray): JacobianPoint {
        return opswug2.g2Map(message, dst) * sk.value
    }

    suspend fun coreVerifyMpl(pk: JacobianPoint, message: ByteArray, signature: JacobianPoint, dst: ByteArray): Boolean {
        try {
            signature.checkValid()
            pk.checkValid()
        } catch (e: Exception) {
            return false
        }
        val q = opswug2.g2Map(message, dst)
        val one = Fq12.one(defaultEc.q)
        val pairingResult = Pairing.atePairingMulti(listOf(pk, -G1Generator()), listOf(q, signature))
        return pairingResult == one
    }

    fun coreAggregateMpl(signatures: List<JacobianPoint>): JacobianPoint {
        if (signatures.isEmpty()) throw IllegalArgumentException("Need a tleast 1 signature")

        var aggregate = signatures.first()
        aggregate.checkValid()
        signatures.drop(1).forEach {
            it.checkValid()
            aggregate += it
        }
        return aggregate
    }

    suspend fun coreAggregateVerify(
        pks: List<JacobianPoint>, ms: List<ByteArray>,
        signature: JacobianPoint, dst: ByteArray
    ): Boolean {
        if (pks.size != ms.size) return false
        if (pks.size < 1) return false
        return try {
            signature.checkValid()
            val qs = mutableListOf(signature)
            val ps = mutableListOf(-G1Generator())
            pks.zip(ms).forEach {
                it.first.checkValid()
                qs.add(opswug2.g2Map(it.second, dst))
                ps.add(it.first)
            }
            Fq12.one(defaultEc.q) == Pairing.atePairingMulti(ps, qs)
        } catch (e: Exception) {
            false
        }
    }
}

@ExperimentalUnsignedTypes
object BasicSchemeMPL {
    val basicSchemeDst = "BLS_SIG_BLS12381G2_XMD:SHA-256_SSWU_RO_NUL_".encodeToByteArray()
    fun keyGen(seed: UByteArray): PrivateKey = HdKeys.keyGen(seed)
    fun sign(sk: PrivateKey, message: ByteArray): JacobianPoint {
        return Core.coreSignMpl(sk, message, basicSchemeDst)
    }

    suspend fun verify(pk: JacobianPoint, message: ByteArray, signature: JacobianPoint): Boolean {
        return Core.coreVerifyMpl(pk, message, signature, basicSchemeDst)
    }

    fun aggregate(vararg signatures: JacobianPoint): JacobianPoint = aggregate(signatures.toList())
    fun aggregate(signatures: List<JacobianPoint>): JacobianPoint = Core.coreAggregateMpl(signatures)

    suspend fun aggregateVerify(pks: List<JacobianPoint>, ms: List<ByteArray>, signature: JacobianPoint): Boolean {
        if (pks.size != ms.size || ms.toSet().size != ms.size) return false
        if (pks.size < 1) return false
        return Core.coreAggregateVerify(pks, ms, signature, basicSchemeDst)
    }

    fun deriveChildSk(sk: PrivateKey, index: Int): PrivateKey = HdKeys.deriveChildSk(sk, index)
    fun deriveChildSkUnderdended(sk: PrivateKey, index: Int): PrivateKey = HdKeys.deriveChildSkUnhardened(sk, index)
    fun deriveChildPkUnhardened(pk: JacobianPoint, index: Int): JacobianPoint =
        HdKeys.deriveChildG1Unhardened(pk, index)
}

@ExperimentalUnsignedTypes
object AugSchemeMPL {
    val augSchemeDst = "BLS_SIG_BLS12381G2_XMD:SHA-256_SSWU_RO_AUG_".encodeToByteArray()
    fun keyGen(seed: UByteArray): PrivateKey = HdKeys.keyGen(seed)
    fun sign(sk: PrivateKey, message: ByteArray): JacobianPoint {
        val pk = sk.getG1()
        return Core.coreSignMpl(sk, pk.toByteArray() + message, augSchemeDst)
    }

    suspend fun verify(pk: JacobianPoint, message: ByteArray, signature: JacobianPoint): Boolean {
        return Core.coreVerifyMpl(pk, pk.toByteArray() + message, signature, augSchemeDst)
    }

    fun aggregate(signatures: List<JacobianPoint>): JacobianPoint = Core.coreAggregateMpl(signatures)

    suspend fun aggregateVerify(pks: List<JacobianPoint>, ms: List<ByteArray>, signature: JacobianPoint): Boolean {
        if (pks.size != ms.size) return false
        if (pks.size < 1) return false
        val mPrimes = pks.zip(ms).map { it.first.toByteArray() + it.second }
        return Core.coreAggregateVerify(pks, mPrimes, signature, augSchemeDst)
    }

    fun deriveChildSk(sk: PrivateKey, index: Int): PrivateKey = HdKeys.deriveChildSk(sk, index)

    fun deriveChildSkUnderdended(sk: PrivateKey, index: Int): PrivateKey = HdKeys.deriveChildSkUnhardened(sk, index)

    fun deriveChildPkUnhardened(pk: JacobianPoint, index: Int): JacobianPoint =
        HdKeys.deriveChildG1Unhardened(pk, index)
}

@ExperimentalUnsignedTypes
object PopSchemeMPL {
    val popSchemeDst = "BLS_SIG_BLS12381G2_XMD:SHA-256_SSWU_RO_POP_".encodeToByteArray()
    val popSchemePopDst = "BLS_POP_BLS12381G2_XMD:SHA-256_SSWU_RO_POP_".encodeToByteArray()
    fun keyGen(seed: UByteArray): PrivateKey = HdKeys.keyGen(seed)
    fun sign(sk: PrivateKey, message: ByteArray): JacobianPoint {
        return Core.coreSignMpl(sk, message, popSchemeDst)
    }

    suspend fun verify(pk: JacobianPoint, message: ByteArray, signature: JacobianPoint): Boolean {
        return Core.coreVerifyMpl(pk, message, signature, popSchemeDst)
    }

    fun aggregate(signatures: List<JacobianPoint>): JacobianPoint = Core.coreAggregateMpl(signatures)

    suspend fun aggregateVerify(pks: List<JacobianPoint>, ms: List<ByteArray>, signature: JacobianPoint): Boolean {
        if (pks.size != ms.size) return false
        if (pks.isEmpty()) return false

        return Core.coreAggregateVerify(pks, ms, signature, popSchemeDst)
    }

    fun popProve(sk: PrivateKey): JacobianPoint {
        val pk = sk.getG1()
        return opswug2.g2Map(pk.toByteArray(), popSchemePopDst) * sk.value
    }

    suspend fun popVerify(pk: JacobianPoint, proof: JacobianPoint): Boolean {
        return try {
            proof.checkValid()
            pk.checkValid()
            val q = opswug2.g2Map(pk.toByteArray(), popSchemePopDst)
            val one = Fq12.one(defaultEc.q)
            val pairingResult = Pairing.atePairingMulti(listOf(pk, -G1Generator()), listOf(q, proof))
            pairingResult == one
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fastAggregateVerify(pks: List<JacobianPoint>, message: ByteArray, signature: JacobianPoint): Boolean {
        if (pks.size < 1) return false
        val aggregate = pks.reduce { acc, jacobianPoint -> acc + jacobianPoint }
        return Core.coreVerifyMpl(aggregate, message, signature, popSchemeDst)
    }

    fun deriveChildSk(sk: PrivateKey, index: Int): PrivateKey = HdKeys.deriveChildSk(sk, index)

    fun deriveChildSkUnderdended(sk: PrivateKey, index: Int): PrivateKey = HdKeys.deriveChildSkUnhardened(sk, index)

    fun deriveChildPkUnhardened(pk: JacobianPoint, index: Int): JacobianPoint =
        HdKeys.deriveChildG1Unhardened(pk, index)

}