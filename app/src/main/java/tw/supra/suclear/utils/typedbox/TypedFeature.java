package tw.supra.suclear.utils.typedbox;

import org.jetbrains.annotations.NotNull;

public final class TypedFeature<ContenT> {
    private ContenT mContent;
    final TypedConstructor<ContenT> mImpl;

    private TypedFeature(TypedConstructor<ContenT> impl) {
        mImpl = impl;
    }

    private static <ContenT> TypedFeature<ContenT> create(@NotNull TypedConstructor<ContenT> impl) {
        return new TypedFeature<ContenT>(impl);
    }

    public TypedFeature<ContenT> invoke(TypedCallback<TypedFeature<ContenT>> callback) {
        callback.onCallback(this);
        return this;
    }

    public <OuT> OuT invoke(TypedMapping<TypedFeature<ContenT>, OuT> mapping) {
        return mapping.map(this);
    }

    public ContenT get() {
        return mContent;
    }

    public ContenT require() {
        if (!valid()) {
            mContent = mImpl.construct();
        }
        return get();
    }

    public boolean valid() {
        return TypedBox.forEachMapping(mContent, mImpl.mVerifiers.collection, k -> k);
    }

}
