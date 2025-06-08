package ch.linst.hoatutahi.components.playgroundModel;

public class IntRef {
    public int value;

    @SuppressWarnings("unused") // This operation is needed for serialization deserialization
    public IntRef() {
    }

    public IntRef(int valArg) {
        value = valArg;
    }
}
