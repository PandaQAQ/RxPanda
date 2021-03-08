package com.pandaq.sample.entities

/**
 * Created by huxinyu on 2019/6/13.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :
 */
data class ZhihuData(
    val date: String?,
    val stories: List<Story?>?,
    val top_stories: List<TopStory?>?
)

/**
 * TopStory
 */
data class TopStory(
    val ga_prefix: String?,
    val id: Int?,
    val image: String?,
    val title: String?,
    val type: Int?
)

/**
 * Story
 */
data class Story(
    val ga_prefix: String?,
    val id: Int?,
    val images: List<String?>?,
    val title: String?,
    val type: Int?
)