package tw.supra.suclear.utils.typedbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypedConstructor<ContenT> {

    final Impls<Boolean> mVerifiers = new Impls<>();
    final Impls<ContenT> mConstructors = new Impls<>();
    final Map<String, TypedMapping> mappings = new HashMap<>();

    public ContenT construct() {
        return TypedBox.chainMapping(null, mConstructors.collection);
    }

    public TypedConstructor<ContenT> clearVerifier() {
        mVerifiers.clear();
        return this;
    }

    public TypedConstructor<ContenT> clearConstructor() {
        mConstructors.clear();
        return this;
    }

    public TypedConstructor<ContenT> enableConstructor(TypedMapping<ContenT, ContenT> constructor) {
        mConstructors.enable(constructor);
        return this;
    }

    public TypedConstructor<ContenT> enableVerifier(TypedMapping<ContenT, Boolean> verifier) {
        mVerifiers.enable(verifier);
        return this;
    }

    public TypedConstructor<ContenT> cancelConstructor(TypedMapping<ContenT, ContenT> constructors) {
        mConstructors.cancel(constructors);
        return this;
    }

    public TypedConstructor<ContenT> cancelVerifier(TypedMapping<ContenT, Boolean> verifier) {
        mVerifiers.cancel(verifier);
        return this;
    }

    class Impls<OuT> {
        final List<TypedMapping<ContenT, OuT>> collection = new ArrayList<>();

        void enable(TypedMapping<ContenT, OuT> impl) {
            if (null != impl) {
                collection.add(impl);
            }
        }

        void cancel(TypedMapping<ContenT, OuT> impl) {
            if (null != impl) {
                collection.remove(impl);
            }
        }

        void clear() {
            collection.clear();
        }
    }

}
