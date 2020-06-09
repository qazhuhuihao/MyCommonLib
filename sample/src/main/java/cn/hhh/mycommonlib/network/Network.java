package cn.hhh.mycommonlib.network;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.hhh.commonlib.retrofit.gson.GsonConverterFactory;
import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.UIUtil;
import cn.hhh.mycommonlib.bean.BaseBean;
import cn.hhh.mycommonlib.network.api.TestApi;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class Network {
    private static final String TAG = Network.class.getSimpleName();
    private static Retrofit retrofit;
    //private static String BASE_URL;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            //.sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
            .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
            .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
            .build();
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();

    public static void initRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    public static String getBaseUrl() {
        return retrofit.baseUrl().toString();
    }

//    public static Retrofit changeBaseUrl(String baseUrl) {
//        Retrofit oldRetrofit = retrofit;
//        Logg.d(TAG, baseUrl);
//        try {
//            initRetrofit(baseUrl);
//            return oldRetrofit;
//        } catch (Exception e) {
//            Logg.e(TAG, "", e);
//            UIUtil.showToastShort("地址错误,请重新设置");
//            retrofit = oldRetrofit;
//            return null;
//        }
//    }

    /**
     * 获取权限组权限
     */
    public static Observable<BaseBean> test() {
        Logg.d(TAG, "test");
        return setThread(retrofit.create(TestApi.class)
                .test());
    }

    private static <T> Observable<T> setThread(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static class SSLSocketFactoryUtils {

        /**
         * 存储客户端自己的密钥
         */
        private final static String CLIENT_PRI_KEY = "client.bks";

        /**
         * 存储服务器的公钥
         */
        private final static String TRUSTSTORE_PUB_KEY = "publickey.bks";

        /**
         * 读取密码
         */
        private final static String CLIENT_BKS_PASSWORD = "123456";

        /**
         * 读取密码
         */
        private final static String PUCBLICKEY_BKS_PASSWORD = "123456";

        private final static String KEYSTORE_TYPE = "BKS";

        private final static String PROTOCOL_TYPE = "TLS";

        private final static String CERTIFICATE_STANDARD = "X509";

        static SSLSocketFactory createSSLSocketFactory() {
            SSLSocketFactory mSSLSocketFactory = null;

            synchronized (SSLSocketFactoryUtils.class) {
                try {
                    // 服务器端需要验证的客户端证书，其实就是客户端的keystore
                    KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
                    // 客户端信任的服务器端证书
                    KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);

                    //读取证书
                    InputStream ksIn = UIUtil.getContext().getAssets().open(CLIENT_PRI_KEY);
                    InputStream tsIn = UIUtil.getContext().getAssets().open(TRUSTSTORE_PUB_KEY);

                    //加载证书
                    keyStore.load(ksIn, CLIENT_BKS_PASSWORD.toCharArray());
                    trustStore.load(tsIn, PUCBLICKEY_BKS_PASSWORD.toCharArray());

                    //关闭流
                    ksIn.close();
                    tsIn.close();

                    //初始化ssl证书库
                    //初始化SSLContext
                    SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TYPE);
                    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERTIFICATE_STANDARD);
                    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(CERTIFICATE_STANDARD);
                    trustManagerFactory.init(trustStore);
                    keyManagerFactory.init(keyStore, CLIENT_BKS_PASSWORD.toCharArray());

                    sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

                    //获得sslSocketFactory
                    mSSLSocketFactory = sslContext.getSocketFactory();
                } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException e) {
                    e.printStackTrace();
                }
            }

            return mSSLSocketFactory;
        }

        @SuppressLint("TrustAllX509TrustManager")
        static X509TrustManager createTrustAllManager() {
            X509TrustManager tm = null;
            try {
                tm = new X509TrustManager() {

                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        //do nothing，接受任意客户端证书
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        //do nothing，接受任意服务端证书
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };
            } catch (Exception ignored) {

            }
            return tm;
        }

        static class TrustAllHostnameVerifier implements HostnameVerifier {

            @SuppressLint("BadHostnameVerifier")
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }

//        /**
//         * 获得信任所有服务器端证书库
//         */
//        static TrustManager[] getTrustAllManager() {
//            return new X509TrustManager[]{new MyX509TrustManager()};
//        }
//
//        public static class MyX509TrustManager implements X509TrustManager {
//
//            @Override
//            public void checkClientTrusted(X509Certificate[] chain, String authType) {
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] chain, String authType) {
//                Logg.i(TAG, "cert: " + chain[0].toString() + ", authType: " + authType);
//            }
//
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        }
    }

}
