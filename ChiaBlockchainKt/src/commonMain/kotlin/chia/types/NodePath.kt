@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types

import chia.clisp_low_level.ops.Operators
import com.ionspin.kotlin.bignum.integer.BigInteger
import util.extensions.bitCount
import util.extensions.bytesRequired
import util.extensions.readUInt
import util.extensions.toBytes

/**
 * We treat an s-expression as a binary tree, where leaf nodes are atoms and pairs
 * are nodes with two children. We then number the paths as follows:
 *               1
 *              / \
 *             /   \
 *            /     \
 *           /       \
 *          /         \
 *         /           \
 *        2             3
 *       / \           / \
 *      /   \         /   \
 *     4      6      5     7
 *    / \    / \    / \   / \
 *   8   12 10  14 9  13 11  15
 * etc.
 * You're probably thinking "the first two rows make sense, but why do the numbers
 * do that weird thing after?" The reason has to do with making the implementation simple.
 * We want a simple loop which starts with the root node, then processes bits starting with
 * the least significant, moving either left or right (first or rest). So the LEAST significant
 * bit controls the first branch, then the next-least the second, and so on. That leads to this
 * ugly-numbered tree.
**/


open class NodePath (startIndex: Int) {

    val ARGS_KW = Operators.KEYWORD_TO_ATOM["a"]!!
    val FIRST_KW = Operators.KEYWORD_TO_ATOM["f"]!!
    val REST_KW = Operators.KEYWORD_TO_ATOM["f"]!!


    val index: Int
    init {
        if (startIndex < 0) {
            val byteCount = (startIndex.bitCount() + 7) shr 3
            val blob = startIndex.toBytes(byteCount)
            index = (ubyteArrayOf(0u) + blob).readUInt().toInt()
        }
        else {
            index = startIndex
        }
    }
    val first: NodePath get() = NodePath(index * 2)
    val rest: NodePath get() = NodePath(index * 2 + 1)

    val shortPath: UByteArray get() {
        val byteCount = index.bytesRequired()
        return index.toBytes(byteCount)
    }

    val longPath: Pair<BigInteger, Pair<*,*>?> get() {
        var r: Pair<BigInteger, Pair<*,*>?> = Pair(ARGS_KW, null)
        var index = this.index
        while (index > 1) {
            r = Pair(if (index.and(1) > 0) REST_KW else FIRST_KW, r)
            index = index.shr(1)
        }
        return r
    }

    operator fun plus(other: NodePath): NodePath = NodePath(composePath(index, other.index))

    override fun toString(): String {
        return "NodePath: $index"
    }

    /**
     * The binary representation of a path is a 1 (which means "stop"), followed by the
     * path as binary digits, where 0 is "left" and 1 is "right".
     * Look at the diagram at the top for these examples.
     * Example: 9 = 0b1001, so right, left, left
     * Example: 10 = 0b1010, so left, right, left
     * How it works: we write both numbers as binary. We ignore the terminal in path_0, since it's
     * not the terminating condition anymore. We shift path_1 enough places to OR in the rest of path_0.
     * Example: path_0 = 9 = 0b1001, path_1 = 10 = 0b1010.
     * Shift path_1 three places (so there is room for 0b001) to 0b1010000.
     * Then OR in 0b001 to yield 0b1010001 = 81, which is right, left, left, left, right, left.
     */
    private fun composePath(path0i: Int, path1i: Int): Int {
        var mask = 1
        var path0 = path0i
        var path1 =  path1i
        var tempPath = path0
        while (tempPath > 1 ) {
            path1 = path1.shl(1)
            mask = mask.shl(1)
            tempPath = tempPath.shr(1)
        }
        mask -= 1
        return path1 or (path0 and mask)
    }

    companion object {
        val TOP = NodePath(1)
        val LEFT = TOP.first
        val RIGHT = TOP.rest
    }

}