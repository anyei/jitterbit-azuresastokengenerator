/*
 * Copyright (c) Angel Robles
 *
 */
package org.jitterbit.plugin.sdk.pipeline.azuresastokengenerator;


import org.jitterbit.plugin.sdk.DataElement;
import org.jitterbit.plugin.sdk.DataElements;
import org.jitterbit.plugin.sdk.DataElementFactory;
import org.jitterbit.plugin.sdk.DataType;
import org.jitterbit.plugin.sdk.pipeline.PipelinePlugin;
import org.jitterbit.plugin.sdk.pipeline.PipelinePluginContext;
import org.jitterbit.plugin.sdk.pipeline.PipelinePluginInput;
import org.jitterbit.plugin.sdk.pipeline.PipelinePluginOutput;
import org.jitterbit.plugin.sdk.pipeline.PluginResult;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.net.URLEncoder;

/**
 * Since its a little bit uphill to generate azure sas tokens
 * This plugins generates the sas tokens
 */
public class AzureSASTokenGenerator extends PipelinePlugin {

    /**
     * This method provides the implementation of the plugin.
     * 
     */
    @Override
    public PluginResult run(PipelinePluginInput input, 
                            PipelinePluginOutput output, 
                            PipelinePluginContext context) throws Exception {
        GenerateSas(context);
        return PluginResult.SUCCESS;
       
    }
    
    private void GenerateSas(PipelinePluginContext context) {
        DataElements des = context.getDataElements();
        String key = null;
        String keyname = null;
        String resourceuri = null;
        if (des.contains("azuresastokengenerator.key", DataType.STRING)) {
            DataElement<String> de = des.get("azuresastokengenerator.key", DataType.STRING);
            key = de.getValue();
        }
        if (des.contains("azuresastokengenerator.keyname", DataType.STRING)) {
            DataElement<String> de = des.get("azuresastokengenerator.keyname", DataType.STRING);
            keyname = de.getValue();
        }
        if (des.contains("azuresastokengenerator.resourceuri", DataType.STRING)) {
            DataElement<String> de = des.get("azuresastokengenerator.resourceuri", DataType.STRING);
            resourceuri = de.getValue();
        }

        if(key != null && keyname != null && resourceuri != null ){
            String sastoken = GetSASToken(resourceuri, keyname, key);
            if (des.contains("azuresastokengenerator.sastoken", DataType.STRING)) {
                DataElement<String> de = des.get("azuresastokengenerator.sastoken", DataType.STRING);
                de.setValue(sastoken);
            }else{
                DataElement<String> sastokenElement = DataElementFactory.newDataElement("azuresastokengenerator.sastoken", DataType.STRING, sastoken);
                des.add(sastokenElement);
            }
        }
    }

    
    /**
     * This is the entry point to the plugin. This method is called by the
     * Jitterbit plugin framework.
     * 
     */
    public static void main(String[] args) {
        // Note that the run() method with no arguments should be called here.
        new AzureSASTokenGenerator().run();
    }

    private static String GetSASToken(String resourceUri, String keyName, String key)
  {
      long epoch = System.currentTimeMillis()/1000L;
      int week = 60*60*24*7;
      String expiry = Long.toString(epoch + week);

      String sasToken = null;
      try {
          String stringToSign = URLEncoder.encode(resourceUri, "UTF-8") + "\n" + expiry;
          String signature = getHMAC256(key, stringToSign);
          sasToken = "SharedAccessSignature sr=" + URLEncoder.encode(resourceUri, "UTF-8") +"&sig=" +
                  URLEncoder.encode(signature, "UTF-8") + "&se=" + expiry + "&skn=" + keyName;
      } catch (UnsupportedEncodingException e) {

          e.printStackTrace();
      }

      return sasToken;
  }


public static String getHMAC256(String key, String input) {
    Mac sha256_HMAC = null;
    String hash = null;
    try {
        sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        Encoder encoder = Base64.getEncoder();

        hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))));

    } catch (InvalidKeyException e) {
        e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (IllegalStateException e) {
        e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }

    return hash;
}
}
