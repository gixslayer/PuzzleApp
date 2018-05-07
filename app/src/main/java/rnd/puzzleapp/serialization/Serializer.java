package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Defines an interface through which to serialize instances of a given type to and from data streams.
 * @param <T> the instance type
 */
public interface Serializer<T> {
    /**
     * Serializes {@code instance} to the given {@code stream}.
     * @param stream the stream to serialize to
     * @param instance the instance to serialize
     * @throws IOException if an exception occurred during the serialization
     */
    void serialize(DataOutputStream stream, T instance) throws IOException;

    /**
     * Serializes a collection of instances to the given {@code stream}.
     * @param stream the stream to serialize to
     * @param instances the instances to serialize to
     * @throws IOException if an exception occurred during the serialization
     */
    default void serializeCollection(DataOutputStream stream, Collection<T> instances) throws IOException {
        stream.writeInt(instances.size());

        for(T instance : instances) {
            serialize(stream, instance);
        }
    }

    /**
     * Deserializes an instance from the given {@code stream}.
     * @param stream the stream to deserialize from
     * @return the deserialized instance
     * @throws IOException if an exception occurred during the deserialization
     */
    T deserialize(DataInputStream stream) throws IOException;

    /**
     * Deserializes a collection of instance from the given {@code stream}.
     * @param stream the stream to deserialize from
     * @return the collection of deserialized instances
     * @throws IOException if an exception occurred during the deserialization
     */
    default Collection<T> deserializeCollection(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        List<T> instances = new ArrayList<>(size);

        for(int i = 0; i < size; ++i) {
            instances.add(deserialize(stream));
        }

        return instances;
    }
}
