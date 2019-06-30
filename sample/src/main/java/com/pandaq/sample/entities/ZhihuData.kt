package com.pandaq.app_launcher.entites

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

data class TopStory(
    val ga_prefix: String?,
    val id: Int?,
    val image: String?,
    val title: String?,
    val type: Int?
)

data class Story(
    val ga_prefix: String?,
    val id: Int?,
    val images: List<String?>?,
    val title: String?,
    val type: Int?
)