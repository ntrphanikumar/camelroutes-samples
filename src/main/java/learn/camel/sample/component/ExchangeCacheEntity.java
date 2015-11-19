package learn.camel.sample.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExchangeCacheEntity {
    private byte[] body;
    private Map<String, String> headers = new HashMap<>(), properties = new HashMap<>();

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(body).append(headers).append(properties).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ExchangeCacheEntity)) {
            return false;
        }
        ExchangeCacheEntity entity = (ExchangeCacheEntity)obj;
        return new EqualsBuilder().append(entity.body, body).append(entity.headers, headers)
                .append(entity.properties, properties).isEquals();
    }
}
