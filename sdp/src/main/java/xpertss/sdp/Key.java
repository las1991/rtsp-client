/*
 * Key.java
 *
 * Created on December 19, 2001, 10:10 AM
 */

package xpertss.sdp;


import com.google.common.base.Strings;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.Objects;

/**
 * A Key represents the k= field contained within either a MediaDescription or a
 * SessionDescription.
 * <p>
 * Please refer to IETF RFC 4566 for a description of SDP.
 *
 * @version 1.0
 */
public final class Key extends Field {

    private String method;
    private String key;

    Key(String method, String key) {
        Assert.assertNull("method may not be empty", method);
        this.method = method;
        this.key = StringUtils.trim(key);
    }

    /**
     * Returns the name of this attribute
     */
    public String getMethod() {
        return method;
    }


    /**
     * Returns the value of this attribute.
     */
    public String getKey() {
        return key;
    }

    @Override
    public char getTypeChar() {
        return 'k';
    }

    @Override
    public Key clone() {
        try {
            return (Key) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(method, key);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Key) {
            Key k = (Key) o;
            return Objects.equals(k.getMethod(), method) &&
                    Objects.equals(k.getKey(), key);
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getTypeChar()).append("=").append(method);
        if (!StringUtils.isEmpty(key)) buf.append(":").append(key);
        return buf.toString();
    }

}

