package com.nxquant.exchange.base.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

import java.io.ByteArrayOutputStream;


public class KryoUtils {

    private static final Pool<Kryo> pool = new Pool<Kryo>(true, false, 16) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    public static byte[] serialize(final Object obj) {
        Kryo kryo = pool.obtain();
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Output output = new Output(stream);
            kryo.writeClassAndObject(output, obj);
            output.close();
            return stream.toByteArray();
        } finally {
            pool.free(kryo);
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V deserialize(final byte[] objectData) {
        Kryo kryo = pool.obtain();
        try {
            Input input = new Input(objectData);
            return (V) kryo.readClassAndObject(input);
        } finally {
            pool.free(kryo);
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V deepCopy(final V obj) {
        Kryo kryo = pool.obtain();
        try {
            return kryo.copy(obj);
        } finally {
            pool.free(kryo);
        }
    }
}
