package com.example.newsandactions.entities

data class News(
    val _attachments: String?,
    val _etag: String?,
    val _rid: String?,
    val _self: String?,
    val _ts: Int?,
    val author: String?,
    val categories: List<String>?,
    val content: String?,
    val description: String?,
    val guid: String?,
    val id: String?,
    val image: Image?,
    val link: String?,
    val pubDate: String?,
    val title: String?
)
