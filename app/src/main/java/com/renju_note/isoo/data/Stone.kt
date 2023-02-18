package com.renju_note.isoo.data

data class Stone(
    val x : Int,
    val y : Int,
    val text : String,
    val type : Type
) {

    enum class Type {
        BLACK, WHITE, CHILD
    }

}
