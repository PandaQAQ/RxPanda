
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1a9236f222ac4293a509c9db710a13f5)](https://app.codacy.com/app/PandaQAQ/RxPanda?utm_source=github.com&utm_medium=referral&utm_content=PandaQAQ/RxPanda&utm_campaign=Badge_Grade_Dashboard)  [![License](https://img.shields.io/github/license/PandaQAQ/RxPanda.svg)](https://github.com/PandaQAQ/RxPanda/blob/master/LICENSE)  [![Download](https://api.bintray.com/packages/huxinyu/maven/rxpanda/images/download.svg?version=0.2.0)](https://bintray.com/huxinyu/maven/rxpanda/0.2.0/link)

# 项目地址
[RxPanda](https://github.com/PandaQAQ/RxPanda)，欢迎使用和 star，提出的问题我会及时回复并处理。
# RxPanda
基于 `RxJava2` `Retrofit2` `Okhttp3` 封装的网络库，处理了数据格式封装，gson 数据类型处理，gson 类解析空安全问题

> 1、支持解析数据壳 key 自定义
> 2、支持接口单独配置禁用脱壳返回接口定义的原始对象
> 3、支持多 host 校验
> 4、支持日志格式化及并发按序输出
> 5、支持 data 为基本数据类型
> 6、支持 int 类型 json 解析为 String 不会 0 变成 0.0

# 基本用法
### 一、全局配置推荐在 Application 初始化时配置
```java
        RxPanda.globalConfig()
                .baseUrl(ApiService.BASE_URL) //配置基础域名
                .netInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)) //添加日志拦截器
                .apiSuccessCode(100L) // 数据壳解析时接口成功的状态码
                .hosts("http://192.168.0.107:8080") // 兼容另一个 host（默认只允许基础域名接口访问）
                .connectTimeout(10000) // 连接超时时间（ms）
                .readTimeout(10000) // 读取超时时间（ms）
                .writeTimeout(10000) // 写入超时时间（ms）
                .debug(BuildConfig.DEBUG);// 是否 dubug 模式（非 debug 模式不会输出日志）
```
以上只是精简的配置，还可以通过 GlobalConfig 配置类进行更多的全局配置

**全部配置**

| 方法                                                        | 说明                                                                         | 是否必须																								|
| ----------------------------------------------------------- | ---------------------------------------------------------- | -------------------------- |
| baseUrl()                                                   | 基础域名配置                                                                 | true				|
| hosts(String... hosts)                                      | 添加信任域名未配置默认只允许 baseUrl 配置的地址                                                                 | false				|
| trustAllHost(boolean trustAll)                              | 是否信任所有域名优先级大于 hosts，配置此为 true 则信任所有 host 不管是否添加 | false				|
| hostVerifier(@NonNull HostnameVerifier verifier)            | 配置 Host 验证规则对象，未配置默认为 `SafeHostnameVerifier`  **(与 hosts()、trustAllHost() 方法冲突，添加此配置后另两个配置失效，验证规则以此配置为准)**                                                 | false				|
| addCallAdapterFactory(@NonNull CallAdapter.Factory factory) | 添加 CallAdapterFactory 未添加默认值为 `RxJava2CallAdapterFactory`           | false				|
| converterFactory(@NonNull Converter.Factory factory)        | 配置 ConverterFactory 未添加默认值为 `PandaConvertFactory`                   | false				|
| callFactory(@NonNull Call.Factory factory)                  | 配置 CallFactory                                                             | false				|
| sslFactory(@NonNull SSLSocketFactory factory)               | 配置 SSLFactory 未添加则通过 SSLManager 配置一个初始参数全为 null 的默认对象 | false				|
| connectionPool(@NonNull ConnectionPool pool)                | 配置连接池，未配置则使用 Okhttp 默认                                         | false				|
| addGlobalHeader(@NonNull String key, String header)         | 添加一个全局的请求头                                                                      | false				|
| globalHeader(@NonNull Map<String, String> headers)         | 设置全局请求头，会将已有数据清除再添加                                                                         | false				|
| addGlobalParam(@NonNull String key, String param)       	  | 添加一个全局的请求参数                                                                           | false				|
| globalParams(@NonNull Map<String, String> params)         | 设置全局请求参数，会将已有数据清除再添加                                                                             | false				|
| retryDelayMillis(long retryDelay)         									| 重试间隔时间                                                                             | false				|
| retryCount(int retryCount)         | 重试次数                                                                             | false				|
| interceptor(@NonNull Interceptor interceptor)        | 添加全局拦截器                                                                          | false				|
| netInterceptor(@NonNull Interceptor interceptor)        | 添加全局网络拦截器                                                                          | false				|
| readTimeout(long readTimeout)       | 全局读取超时时间                                                                          | false				|
| writeTimeout(long writeTimeout)        | 全局写超时时间                                                                          | false				|
| connectTimeout(long connectTimeout)        | 全局连接超时时间                                                                          | false				|
| apiDataClazz(Class<? extends IApiData> clazz)  | Json解析接口数据结构外壳对象 参考 `ApiData`，未配置默认按 `ApiData` 解析，如结构不变 key 不一致则可以通过自定义 | false				|
| apiSuccessCode(Long apiSuccessCode)             | Json解析接口数据结构外壳对象为 `ApiData` 结构时，配置成功 Code，默认值为 `0L`				| false |
| debug(boolean debug)             | 配置是否为 debug 模式，非 debug 模式网络库将不会输出 日志 | false |

### 二、接口定义
``` kotlin
public interface ApiService {

    // 在线 mock 正常使用 ApiData 数据壳
    @GET("xxx/xxx/xxx")
    Observable<List<ZooData>> getZooList();

    // 数据结构不变但是数据壳 jsonKey 与框架默认不一致时使用此注解，也可在 Config 配置全局使用此数据壳
    @ApiData(clazz = ZooApiData.class)
    @GET("xxx/xxx/xxx")
    Observable<List<ZooData>> newJsonKeyData();

    // 与 ApiData 结构完全不一样使用 RealEntity 标准不做脱壳处理，返回 ZhihuData 就解析为 ZhihuData
    @RealEntity
    @GET("xxx/xxx/xxx")
    Observable<ZhihuData> zhihu();
}
```
与 retrofit 完全一样的基础上增加了两个自定义注解
- 1、 @RealEntity

	接口数据未使用 ApiData 进行数据壳包装，需要直接解析未定义对象时使用。如上面代码中的 `ZhihuData` 在解析时不会进行脱壳操作，接口返回 `ZhihuData` 就解析为 `ZhihuData`
- 2、@ApiData(clazz = ZooApiData.class)

	接口数据使用 ApiData 进行数据壳包装，但包装的 key 与默认的 ApiData 不一致时，可自定义数数据壳实现 IApiData 接口
```kotlin
// 自定义解析 key
data class ZooApiData<T>(
    @SerializedName("errorCode") private val code: Long,
    @SerializedName("errorMsg") private val msg: String,
    @SerializedName("response") private val data: T
) : IApiData<T> {
    override fun getCode(): Long {
        return code
    }

    override fun getMsg(): String {
        return msg
    }

    override fun getData(): T {
        return data
    }

    override fun isSuccess(): Boolean {
        return code.toInt() == 100
    }

}
```
如果全部接口都是按 ZooApiData 的解析 key 格式返回的数据,也不用麻烦的每个接口都加注解。直接在第一步的配置中使用全局配置来配置全局的数据壳

``` kotlin
  .apiDataClazz(ZooApiData::class.java)
```
### 三、请求使用

#### Retrofit 方式
``` kotlin
    private val apiService = RxPanda.retrofit().create(ApiService::class.java)

				. . .

	 apiService.zooList
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : ApiObserver<List<ZooData>>() {
                        override fun onSuccess(data: List<ZooData>?) {
                            // do something
                        }

                        override fun onError(e: ApiException?) {
                            // do something when error
                        }

                        override fun finished(success: Boolean) {
                            // do something when all finish
                        }
                    })

				. . .

```

#### Http 请求方式
此方式直接使用，不需要第二步的接口定义

- GET 方式

这只是一个最简例子，可以通过链式调用添加`参数` `请求头` `拦截器` `标签` 等属性
``` kotlin
    RxPanda.get("https://www.xx.xx.xx/xx/xx/xx")
    .addParam(paramsMap)
    .tag("tags") // 可使用 RequestManager 根据 tag 管理请求
    .request(object :ApiObserver<List<ZooData>>(){
        override fun onSuccess(data: List<ZooData>?) {
            // do something
        }

        override fun onError(e: ApiException?) {
            // do something when error
        }

        override fun finished(success: Boolean) {
            // do something when all finish
        }

    })
```

- POST 方式

这只是一个最简例子，可以通过链式调用添加`参数` `请求头` `拦截器` `标签` 等属性
```kotlin
                RxPanda.post("xxxxxx")
                    .addHeader("header", "value")
                    .urlParams("key", "value")
                    .tag("ss")
                    .request(object : AppCallBack<String>() {
                        override fun success(data: String?) {

                        }

                        override fun fail(code: Long?, msg: String?) {

                        }

                        override fun finish(success: Boolean) {

                        }

                    })
```
#### 文件上传
```kotlin
        RxPanda.upload("url")
            .addImageFile("key",file)
//            .addBytes("key",bytes)
//            .addStream("key",stream)
//            .addImageFile("key",file)
            .request(object : UploadCallBack() {
                override fun done(success: Boolean) {

                }

                override fun onFailed(e: Exception?) {

                }

                override fun inProgress(progress: Int) {

                }

            })
```
#### 文件下载
```kotlin
        RxPanda.download("url")
            .target(file)
//            .target(path,fileName)
            .request(object : UploadCallBack() {
                override fun done(success: Boolean) {

                }

                override fun onFailed(e: Exception?) {

                }

                override fun inProgress(progress: Int) {

                }

            })
```
# 日志处理
- 日志数据格式化
以下是一次完整的网络请求，包含了数据和请求的基本参数数据
```md
2019-08-13 10:04:02.088 22957-23059/com.pandaq.sample D/RxPanda:
2019-08-13 10:04:02.088 22957-23059/com.pandaq.sample D/RxPanda: ╔════════════════════════  HTTP  START  ══════════════════════════
2019-08-13 10:04:02.088 22957-23059/com.pandaq.sample D/RxPanda: ║
2019-08-13 10:04:02.088 22957-23059/com.pandaq.sample D/RxPanda: ║==> GET https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/customApiData http/1.1
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Host: www.easy-mock.com
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Connection: Keep-Alive
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Accept-Encoding: gzip
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║User-Agent: okhttp/3.10.0
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Info: GET
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║==> 200 OK https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/customApiData (245ms)
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Server: Tengine
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Date: Tue, 13 Aug 2019 02:04:01 GMT
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Content-Type: application/json; charset=utf-8
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Content-Length: 495
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Connection: keep-alive
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║X-Request-Id: 71a77b24-9822-47df-94b1-fd477cfcdaa9
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Vary: Accept, Origin
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Rate-Limit-Remaining: 1
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Rate-Limit-Reset: 1565661842
2019-08-13 10:04:02.089 22957-23059/com.pandaq.sample D/RxPanda: ║Rate-Limit-Total: 2
2019-08-13 10:04:02.094 22957-23059/com.pandaq.sample D/RxPanda: ║
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║——————————————————JSON START——————————————————
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║ {
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║   "errorCode": 100,
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║   "errorMsg": "我是错误信息",
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║   "response": [
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║     {
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "zooId": 28,
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "name": "成都市动物园",
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "englishName": "chengdu zoo",
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "address": "中国·四川·成都·成华区昭觉寺南路234号",
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "tel": "028-83516953"
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║     },
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║     {
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "zooId": 28,
2019-08-13 10:04:02.095 22957-23059/com.pandaq.sample D/RxPanda: ║       "name": "北京市动物园",
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "englishName": "beijing zoo",
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "address": "中国·北京·北京·XX路XX号",
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "tel": "028-83316953"
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║     },
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║     {
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "zooId": 28,
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "name": "重庆市动物园",
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "englishName": "chongqing zoo",
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "address": "中国·重庆·重庆·XX路XX号",
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║       "tel": "028-83513353"
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║     }
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║   ]
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║ }
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║——————————————————JSON END———————————————————
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║Info: 495-byte body
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ║
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda: ╚════════════════════════  HTTP  END  ═══════════════════════════
2019-08-13 10:04:02.096 22957-23059/com.pandaq.sample D/RxPanda:
```

- 多线程并发请求时日志输出交错错乱的问题
为了避免请求日志穿插问题，定义了 `LogEntity` 日志对象类，将一次请求的各个阶段的日志输出暂存起来，到当次网络请求结束时统一打印数据，打印时使用了线程安全的 LogPrinter 类有序输出。（因此上线一定要关闭 Log（一般使用第一步的 BuildConfig.DEBUG 来动态配置），日志的线程锁会有性能损耗。）
# Gson 解析处理
以 String 类型解析 TypeAdapter 为例，其他处理可在 [DefaultTypeAdapters](https://github.com/PandaQAQ/RxPanda/blob/master/rxpanda/src/main/java/com/pandaq/rxpanda/gsonadapter/DefaultTypeAdapters.java) 查看
``` java
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return "";
            }
            if (peek == JsonToken.NUMBER) {
                double dbNum = in.nextDouble();
                if (dbNum > Long.MAX_VALUE) {
                    return String.valueOf(dbNum);
                }
                // 如果是整数
                if (dbNum == (long) dbNum) {
                    return String.valueOf((long) dbNum);
                } else {
                    return String.valueOf(dbNum);
                }
            }
            /* coerce booleans to strings for backwards compatibility */
            if (peek == JsonToken.BOOLEAN) {
                return Boolean.toString(in.nextBoolean());
            }
            return in.nextString();
        }

        @Override
        public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
        }
    };
```

- **number 类型转解析为字符串  `1` 变 `"1.0"` 的问题**
Gson 解析由于 Gson 库默认的 ObjectTypeAdapter 中 Number 类型数据直接都解析为了 double 数据类型，因此会出现。当接口返回数据为 int 型，解析类中又定义为 String 类型的时候出现 `1` 变 `"1.0"`的问题。
```java
// 对 number 具体的类型进行判断，而不是一概而论的返回 double 类型
		 	if (peek == JsonToken.NUMBER) {
                double dbNum = in.nextDouble();
                if (dbNum > Long.MAX_VALUE) {
                    return String.valueOf(dbNum);
                }
                // 如果是整数
                if (dbNum == (long) dbNum) {
                    return String.valueOf((long) dbNum);
                } else {
                    return String.valueOf(dbNum);
                }
            }
```

- **避免空指针问题**
重写 String 类型的 TypeAdapter 在类型为 null 时返回 `""`空字符串
```java
// 对于空类型不直接返回 null 而是返回 "" 避免空指针
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return "";
            }
```
# 混淆打包
混淆打包需添加如下的过滤规则
``` java
-keep @android.support.annotation.Keep class * {*;}

-keep class android.support.annotation.Keep

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

########### OkHttp3 ###########
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**

########### RxJava RxAndroid ###########
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

########### Gson ###########
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
# Gson 自定义相关
-keep class com.pandaq.rxpanda.entity.**{*;}
-keep class com.pandaq.rxpanda.gsonadapter.**{*;}
```
