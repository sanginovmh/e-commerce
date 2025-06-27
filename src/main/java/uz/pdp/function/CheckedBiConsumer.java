package uz.pdp.function;

import java.io.IOException;

@FunctionalInterface
public interface CheckedBiConsumer<T, U> {
    void accept(T t, U u) throws IOException;
}
