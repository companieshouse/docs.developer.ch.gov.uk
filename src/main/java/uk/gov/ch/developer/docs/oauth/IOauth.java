package uk.gov.ch.developer.docs.oauth;

public interface IOauth {

    String oauth2EncodeState(String returnUri,
            String nonce,
            String attributeName);
}
