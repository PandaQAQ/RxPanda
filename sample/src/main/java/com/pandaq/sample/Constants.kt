package com.pandaq.sample

/**
 * Created by huxinyu on 2020/12/28.
 * Email : panda.h@foxmail.com
 * Description :
 */
class Constants {
    companion object {
        const val MOCK_TYPE_ERROR = "{\n" +
                "\t\"code\":0,\n" +
                "\t\"msg\":\"成功\",\n" +
                "\t\"data\":{\n" +
                "\t\"group\":\"groupName\",\n" +
                "\t\"info\":{\n" +
                "\t\t\"userId\":\"1\",\n" +
                "\t\t\"userName\":\"\",\n" +
                "\t\t\"nickName\":\"\",\n" +
                "\t\t\"age\":\"\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}"

        const val MOCK_USER = "{\n" +
                "\t\"code\":0,\n" +
                "\t\"msg\":\"成功\",\n" +
                "\t\"data\":{\n" +
                "\t\"group\":\"groupName\",\n" +
                "\t\"info\":{\n" +
                "\t\t\"userId\":\"1\",\n" +
                "\t\t\"userName\":\"\",\n" +
                "\t\t\"nickName\":\"\",\n" +
                "\t\t\"age\":\"18\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}"
        const val MOCK_STRING = "{\n" +
                "\t\"code\":0,\n" +
                "\t\"msg\":\"成功\",\n" +
                "\t\"data\":\"stringData\"\n" +
                "}"
        const val MOCK_INT = "{\n" +
                "\t\"code\":0,\n" +
                "\t\"msg\":\"成功\",\n" +
                "\t\"data\":1\n" +
                "}"
    }
}