package pl.jsieczczynski.SpringBootRedditClone.validators.unique;

public interface FieldValueExists {
    boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException;
}
