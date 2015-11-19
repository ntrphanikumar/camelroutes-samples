package learn.camel.sample.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExchangeCacheEntity {
    private byte[] body;
    private Map<String, Object> headers = new HashMap<>(), properties = new HashMap<>();

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
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
