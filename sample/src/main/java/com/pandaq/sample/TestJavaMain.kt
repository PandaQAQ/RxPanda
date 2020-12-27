package com.pandaq.sample

object TestJavaMain {
    @JvmStatic
    fun main(args: Array<String>) {
        println(findOcurrences("we will we will rock you", "we", "will").toString())
    }

    fun findOcurrences(text: String, first: String, second: String): Array<String> {
        val result = arrayListOf<String>()
        val array = text.split(" ")
        for (i in 0..array.size-3){
            if (array[i]==first&&array[i+1]==second){
                result.add(array[i+2])
            }
        }
        return result.toTypedArray()
    }
}