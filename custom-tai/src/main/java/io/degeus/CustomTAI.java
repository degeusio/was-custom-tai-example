package io.degeus;

import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;
import com.ibm.wsspi.security.tai.TAIResult;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;
import com.ibm.wsspi.security.token.AttributeNameConstants;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Based on
 * https://www.ibm.com/support/knowledgecenter/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/twlp_dev_custom_tai.html
 * See also
 * https://www.ibm.com/support/knowledgecenter/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/twlp_custom_sip_tai.html
 *
 */
public class CustomTAI implements TrustAssociationInterceptor {

    private static final String CUSTOM_HEADER = "x-my-customtoken";

    public CustomTAI() {
        super();
    }

    /*
     * @see com.ibm.wsspi.security.tai.TrustAssociationInterceptor#isTargetInterceptor
     * (javax.servlet.http.HttpServletRequest)
     */
    public boolean isTargetInterceptor(HttpServletRequest req)
            throws WebTrustAssociationException {
        //Add logic to determine whether to intercept this request
        return true;
    }

    /*
     * @see com.ibm.wsspi.security.tai.TrustAssociationInterceptor#negotiateValidateandEstablishTrust
     * (javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
     */
    public TAIResult negotiateValidateandEstablishTrust(HttpServletRequest req,
                                                        HttpServletResponse resp) throws WebTrustAssociationFailedException {

        // Add logic to authenticate a request and return a TAI result here.

        String header = req.getHeader(CUSTOM_HEADER);
        if (header == null || header.isEmpty()) {

            // we are not concerned about proper logging functionality here, so just use normal standard out.
            System.out.println(String.format("No value found for header [%s]. Returning 401.", CUSTOM_HEADER));
            return TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // we are simply splitting the provided header value here to get the userId and role to be evaluated by
        // the container given the JSR-250 @RolesAllowed annotation in the Java EE app.
        String[] userRole = header.split(":");
        String userId = userRole[0];
        List<String> groups = new ArrayList<String>();
        groups.add(userRole[1]);
        String uniqueId = "someUniqueId";
        String key = "somekey";
        Subject subject = createSubject(userId, uniqueId, groups, key);

        System.out.println(String.format("Value found for header [%s]. Returning user [%s] with group [%s].",
                CUSTOM_HEADER, userRole[0], userRole[1] ));
        return TAIResult.create(HttpServletResponse.SC_OK, "notused", subject);
    }

    /*
     * @see com.ibm.wsspi.security.tai.TrustAssociationInterceptor#initialize(java.util.Properties)
     */
    public int initialize(Properties arg0)
            throws WebTrustAssociationFailedException {
        return 0;
    }

    /*
     * @see com.ibm.wsspi.security.tai.TrustAssociationInterceptor#getVersion()
     */
    public String getVersion() {
        return "1.0";
    }

    /*
     * @see com.ibm.wsspi.security.tai.TrustAssociationInterceptor#getType()
     */
    public String getType() {
        return this.getClass().getName();
    }

    /*
     * @see com.ibm.wsspi.security.tai.TrustAssociationInterceptor#cleanup()
     */
    public void cleanup()

    {}

    private Subject createSubject(String userid, String uniqueid, List groups,
                                  String key) {
        Subject subject = new Subject();
        Hashtable hashtable = new Hashtable();
        hashtable.put(AttributeNameConstants.WSCREDENTIAL_UNIQUEID, uniqueid);
        hashtable.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, userid);
        hashtable.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groups);
        System.out.println("Subject cache key is " + key);
        hashtable.put(AttributeNameConstants.WSCREDENTIAL_CACHE_KEY, key);
        subject.getPublicCredentials().add(hashtable);

        return subject;
    }
}