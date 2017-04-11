/*
 * Origin.java
 *
 * Created on December 20, 2001, 2:30 PM
 */

package xpertss.sdp;


import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

/**
 * An Origin represents the o= fields contained within a SessionDescription and
 * identifies the originator of the session.
 * <p><pre>
 *    o=<username> <sess-id> <sess-version> <nettype> <addrtype> <unicast-address>
 * </pre><p>
 * This is not necessarily the same entity who is involved in the session.
 * <p>
 * The Origin contains:
 * <ul>
 * <li>the name of the user originating the session,</li>
 * <li>a unique session identifier, and</li>
 * <li>a unique version for the session.</li>
 * </ul>
 * These fields should uniquely identify the session.
 * <p>
 * The Origin also includes:
 * <ul>
 * <li>the network type,</li>
 * <li>address type, and</li>
 * <li>address of the originator.</li>
 * </ul>
 * Please refer to IETF RFC 4566 for a description of SDP.
 *
 * @version 1.0
 */
public final class Origin extends Field {

    private String username;
    private String address;
    private String addressType;
    private String networkType;
    private long sessionId;
    private long version;


    Origin(String username, long sessionId, long version, String address, String addressType, String networkType) {
        Assert.assertNotNull("username may not be empty", username);
        Assert.assertNotNull("address may not be empty", address);
        Assert.assertNotNull("addressType may not be empty", addressType);
        Assert.assertNotNull("networkType may not be empty", networkType);
        this.username = StringUtils.trim(username);
        this.address = StringUtils.trim(address);
        this.addressType = StringUtils.trim(addressType);
        this.networkType = StringUtils.trim(networkType);

        this.sessionId = Math.max(0, sessionId);
        this.version = Math.max(0, version);
    }

    /**
     * Returns the name of the session originator.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the unique identity of the session.
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * Returns the unique version of the session.
     */
    public long getSessionVersion() {
        return version;
    }

    /**
     * Returns the address for this session.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the type of the address for this Connection.
     *
     * @see SdpConstants#ADDRESS_TYPE_IP4
     * @see SdpConstants#ADDRESS_TYPE_IP6
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * Returns the type of the network for this Connection
     *
     * @see SdpConstants#NETWORK_TYPE_INTERNET
     */
    public String getNetworkType() {
        return networkType;
    }


    @Override
    public char getTypeChar() {
        return 'o';
    }

    @Override
    public Origin clone() {
        try {
            return (Origin) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(username, sessionId, version, address, addressType, networkType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Origin) {
            Origin o = (Origin) obj;
            return Objects.equal(o.getUsername(), username) &&
                    Objects.equal(o.getSessionId(), sessionId) &&
                    Objects.equal(o.getSessionVersion(), version) &&
                    Objects.equal(o.getAddress(), address) &&
                    Objects.equal(o.getAddressType(), addressType) &&
                    Objects.equal(o.getNetworkType(), networkType);
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getTypeChar()).append("=").append(username).append(" ");
        buf.append(sessionId).append(" ").append(version).append(" ");
        buf.append(networkType).append(" ").append(addressType).append(" ");
        buf.append(address);
        return buf.toString();
    }
}

