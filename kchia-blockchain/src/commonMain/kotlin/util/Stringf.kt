package util


@ExperimentalUnsignedTypes
fun formatf(format: String, vararg args: Any?): String {

    // temp solution
    if (format.startsWith("%") && format.endsWith("X", true) && args[0] is Int) {

        val count = format.substring(2).dropLast(1).toInt(10)

        val pad = format[1]

        return (args[0] as Int).toString(16).padStart(count, pad).takeLast(count)

    } else if (format.startsWith("%") && format.endsWith("X", true) && args[0] is Long) {


        val count = format.substring(2).dropLast(1).toInt(10)

        val pad = format[1]

        return (args[0] as Long).toString(16).padStart(count, pad).takeLast(count)

    } else if (format.startsWith("%") && format.endsWith("X", true) && args[0] is ULong) {

        val count = format.substring(2).dropLast(1).toInt(10)

        val pad = format[1]

        return (args[0] as ULong).toString(16).padStart(count, pad).takeLast(count)

    } else if (format.startsWith("%.") && format.endsWith("f", true) && args[0] is Double) {

        val count = format.substring(2).dropLast(1).toInt(10)

        val str = args[0].toString()

        val split = str.split(".")

        return if (split.size == 1) {
            "${split[0]}.".padEnd(count, '0')
        } else {
            val pad = split[1].take(count).padEnd(count, '0')
            "${split[0]}.$pad"

        }

    } else {
        throw UnsupportedOperationException("Not supported format $format for args: $args}")
    }
}