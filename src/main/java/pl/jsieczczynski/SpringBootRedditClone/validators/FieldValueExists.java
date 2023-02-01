package pl.jsieczczynski.SpringBootRedditClone.validators;

public interface FieldValueExists {
    boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException;
}
