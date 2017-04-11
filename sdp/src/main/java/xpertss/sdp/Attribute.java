/*
 * Attribute.java
 *
 * Created on December 18, 2001, 10:55 AM
 */

package xpertss.sdp;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

/**
 * An Attribute represents an a= fields contained within either a MediaDescription or
 * a SessionDescription.
 * <p>
 * An Attribute can be just an identity/name or a name-value pair.
 * <p>
 * Here are some examples:
 * <p><pre>
 *    a=recvonly
 *       identifies a rcvonly attribute with just a name
 *    a=rtpmap:0 PCMU/8000
 *       identifies the media format 0 has having the value PCMU/8000.
 * </pre><p>
 *
 * @version 1.0
 */
public final class Attribute extends Field {

    private String name;
    private String value;

    Attribute(String name, String value) {
        this.name = Strings.emptyToNull(name);
        this.value = Strings.nullToEmpty(value);
    }

    /**
     * Returns the name of this attribute
     *
     * @return a String identity.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of this attribute.
     *
     * @return the value; null if the attribute has no associated value.
     */
    public String getValue() {
        return value;
    }


    @Override
    public char getTypeChar() {
        return 'a';
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Attribute) {
            Attribute attr = (Attribute) o;
            return Objects.equal(attr.getName(), name) && Objects.equal(attr.getValue(), value);
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getTypeChar()).append("=").append(name);
        if (!StringUtils.isEmpty(value)) buf.append(":").append(value);
        return buf.toString();
    }

}

