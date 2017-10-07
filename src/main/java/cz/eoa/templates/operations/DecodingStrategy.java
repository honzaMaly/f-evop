package cz.eoa.templates.operations;

public interface DecodingStrategy<V,T> {
    T decode(V genes);
}
