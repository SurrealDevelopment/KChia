package util.extensions

fun <T> ArrayList<T>.pop(): T {
    return this.removeAt(this.lastIndex)
}

fun <T> ArrayList<T>.push(vararg items: T) {
    this.addAll(items)
}