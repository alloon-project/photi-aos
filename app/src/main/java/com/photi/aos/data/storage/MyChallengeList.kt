package com.photi.aos.data.storage

object MyChallengeList {
    private val myList = mutableListOf<Int>()

    fun addId(id: Int) {
        myList.add(id)
    }

    fun removeId(id: Int) {
        myList.remove(id)
    }

    fun clearList() {
        myList.clear()
    }

    fun getList() : List<Int> {
        return myList
    }

    fun checkUserInChallenge(id: Int) : Boolean {
        for (challengeId in myList) {
            if (challengeId == id)
                return true
        }
        return false
    }
}