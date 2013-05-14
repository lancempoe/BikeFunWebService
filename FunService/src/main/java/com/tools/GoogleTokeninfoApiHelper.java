package com.tools;

import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.code.geocoder.Geocoder;
import com.model.OAuth;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * See: https://developers.google.com/accounts/docs/OAuth2Login#validatingtoken
 * @author lancepoehler
 *
 */
public class GoogleTokeninfoApiHelper {

    private static final Log LOG = LogFactory.getLog(Geocoder.class);
    private static final String GoogleUserInfoApi = "https://www.googleapis.com/oauth2/v1/userinfo?accessToken=";


    /**
	 * @author lancepoehler
	 * @throws InterruptedException
	 *
	 */
	public boolean isLoggedIn(OAuth oAuth) throws InterruptedException {
		LOG.debug("Calling Google User API");

        if (oAuth == null || StringUtils.isEmpty(oAuth.foreignId)) {return false;}

        try {
            Tokeninfo tokenInfo = null;

            String uri = buildAddressString(oAuth);
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);

                JAXBContext jc = JAXBContext.newInstance(Tokeninfo.class);
                InputStream xml = connection.getInputStream();

                tokenInfo = (Tokeninfo) jc.createUnmarshaller().unmarshal(xml);
            } catch (JAXBException e) {
                return false;
            } finally {
                connection.disconnect();
            }

            //As required by google
            if (oAuth.foreignId.equals(tokenInfo.getAudience())) {
                return true;
            }
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    private String buildAddressString(OAuth oAuth) {
        return GoogleUserInfoApi + oAuth.accessToken;
    }
}
