package com.joonmyoung.openphonebook.Group

data class GroupDataModel(
    val groupKey : String? = null,
    val creatorUid : String? = null,
    val groupName : String? = null,
    val groupMember : MutableList<String> = mutableListOf()

)
