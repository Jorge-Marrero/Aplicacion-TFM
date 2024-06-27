package es.tfm.clientetfm.util.net;

import android.util.Log;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class LoadCertificates {
    private static String TAG = LoadCertificates.class.getName();
    public static OkHttpClient cargarCetificado(InputStream is, InputStream certificateStream){
        try{

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(is);

            KeyManagerFactory kmf;

            KeyStore ks = KeyStore.getInstance("PKCS12");

            char[] passwd = "ppppp".toCharArray();

            ks.load(certificateStream, passwd);

            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, passwd);


            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            // No necesitas implementar este método si solo te interesa la verificación del servidor.
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            for (X509Certificate cert : chain) {
                                try {
                                    System.out.println(cert);
                                    cert.verify(certificate.getPublicKey());
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                    throw new CertificateException("Certificado no reconocido: " + cert.getSubjectX500Principal());
                                }
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };


            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), trustManagers, new SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return s.equals("192.168.190.165");

                        }
                    })
                    .build();
            return client;
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public static PublicKey leerClavePública(String cp) throws Exception {
        String[] lines = cp.replace("\\r", "").replaceAll("\"", "").split("\\\\n");
        String nuevo = "";
        for (String line : lines) {
            nuevo += line + "\n";
        }
        nuevo = nuevo.replaceAll("\\n$", "");
        StringReader reader = new StringReader(nuevo);

        PemReader pemReader = new PemReader(reader);
        PemObject pemObject = pemReader.readPemObject();
        byte[] publicKeyBytes = pemObject.getContent();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        System.out.println("Clave pública: " + publicKey);
        return publicKey;
    }

    public static boolean verificarFirma(PublicKey cp, String msj, String hash){
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(hash);
        try{
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(cp);
            signature.update(msj.getBytes());
            boolean verificado = signature.verify(encryptedMessageBytes);
            return verificado;
        }catch(Exception e){
            System.out.println(e.toString());
            return false;
        }

    }
}
