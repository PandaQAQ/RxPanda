package com.pandaq.ktpanda.ssl

import android.annotation.SuppressLint
import android.util.Log
import com.pandaq.ktpanda.config.HttpGlobalConfig
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by huxinyu on 2019/2/15.
 * Email : panda.h@foxmail.com
 * Description :
 */
object SSLManager {
    fun getSslSocketFactory(
        certificates: Array<InputStream?>?,
        bksFile: InputStream?,
        password: String?
    ): SSLSocketFactory {
        return try {
            val trustManagers = prepareTrustManager(certificates)
            val keyManagers = prepareKeyManager(bksFile, password)
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager: TrustManager = if (trustManagers != null) {
                MyTrustManager(chooseTrustManager(trustManagers))
            } else {
                UnSafeTrustManager()
            }
            sslContext.init(keyManagers, arrayOf(trustManager), SecureRandom())
            sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        } catch (e: KeyStoreException) {
            throw AssertionError(e)
        }
    }

    private fun prepareTrustManager(certificates: Array<InputStream?>?): Array<TrustManager>? {
        if (certificates.isNullOrEmpty()) return null
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            for ((index, certificate) in certificates.withIndex()) {
                val certificateAlias = index.toString()
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(certificate)
                )
                try {
                    certificate?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            return trustManagerFactory.trustManagers
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager>? {
        try {
            if (bksFile == null || password == null) return null
            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            return keyManagerFactory.keyManagers
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }

    class SafeHostnameVerifier : HostnameVerifier {
        private val hosts: MutableSet<String> = HashSet()

        constructor(host: String) {
            hosts.add(host)
        }

        constructor(hosts: List<String>?) {
            this.hosts.addAll(hosts!!)
        }

        constructor(vararg hosts: String) {
            this.hosts.addAll(hosts)
        }

        /**
         * 添加一个 host
         *
         * @param host 添加的 host
         */
        fun addHost(host: String) {
            hosts.add(host)
        }

        /**
         * 添加一个 host 列表
         *
         * @param hosts 添加的 host
         */
        fun addHosts(hosts: List<String>) {
            this.hosts.addAll(hosts)
        }

        /**
         * 重置 host
         *
         * @param hosts 支持的 hosts
         */
        fun resetHosts(hosts: List<String>) {
            this.hosts.clear()
            this.hosts.addAll(hosts)
        }

        override fun verify(hostname: String, session: SSLSession): Boolean {
            // if allow all return true
            if (HttpGlobalConfig.instance.isTrustAllHost) return true
            if (hosts.isEmpty()) return false
            for (host in hosts) {
                if (host.contains(hostname)) {
                    return true
                }
            }
            return false
        }
    }

    @SuppressLint("CustomX509TrustManager")
    private class UnSafeTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            Log.d("SSLManager", authType)
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            Log.d("SSLManager", authType)
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    @SuppressLint("CustomX509TrustManager")
    private class MyTrustManager(localTrustManager: X509TrustManager?) : X509TrustManager {
        private val defaultTrustManager: X509TrustManager?
        private val localTrustManager: X509TrustManager?

        init {
            val var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            var4.init(null as KeyStore?)
            defaultTrustManager = chooseTrustManager(var4.trustManagers)
            this.localTrustManager = localTrustManager
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            Log.d("SSLManager", authType)
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTrustManager!!.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager!!.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
}