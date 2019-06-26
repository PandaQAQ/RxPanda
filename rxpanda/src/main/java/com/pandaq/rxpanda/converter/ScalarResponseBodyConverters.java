package com.pandaq.rxpanda.converter;

import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;

/**
 * Created by huxinyu on 2019/6/25.
 * Email : panda.h@foxmail.com
 * Description :
 */
final class ScalarResponseBodyConverters {
    private ScalarResponseBodyConverters() {
    }

    static final class ShortResponseBodyConverter implements Converter<ResponseBody, Short> {
        static final ScalarResponseBodyConverters.ShortResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.ShortResponseBodyConverter();

        ShortResponseBodyConverter() {
        }

        public Short convert(ResponseBody value) throws IOException {
            return Short.valueOf(value.string());
        }
    }

    static final class LongResponseBodyConverter implements Converter<ResponseBody, Long> {
        static final ScalarResponseBodyConverters.LongResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.LongResponseBodyConverter();

        LongResponseBodyConverter() {
        }

        public Long convert(ResponseBody value) throws IOException {
            return Long.valueOf(value.string());
        }
    }

    static final class IntegerResponseBodyConverter implements Converter<ResponseBody, Integer> {
        static final ScalarResponseBodyConverters.IntegerResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.IntegerResponseBodyConverter();

        IntegerResponseBodyConverter() {
        }

        public Integer convert(ResponseBody value) throws IOException {
            return Integer.valueOf(value.string());
        }
    }

    static final class FloatResponseBodyConverter implements Converter<ResponseBody, Float> {
        static final ScalarResponseBodyConverters.FloatResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.FloatResponseBodyConverter();

        FloatResponseBodyConverter() {
        }

        public Float convert(ResponseBody value) throws IOException {
            return Float.valueOf(value.string());
        }
    }

    static final class DoubleResponseBodyConverter implements Converter<ResponseBody, Double> {
        static final ScalarResponseBodyConverters.DoubleResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.DoubleResponseBodyConverter();

        DoubleResponseBodyConverter() {
        }

        public Double convert(ResponseBody value) throws IOException {
            return Double.valueOf(value.string());
        }
    }

    static final class CharacterResponseBodyConverter implements Converter<ResponseBody, Character> {
        static final ScalarResponseBodyConverters.CharacterResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.CharacterResponseBodyConverter();

        CharacterResponseBodyConverter() {
        }

        public Character convert(ResponseBody value) throws IOException {
            String body = value.string();
            if (body.length() != 1) {
                throw new IOException("Expected body of length 1 for Character conversion but was " + body.length());
            } else {
                return body.charAt(0);
            }
        }
    }

    static final class ByteResponseBodyConverter implements Converter<ResponseBody, Byte> {
        static final ScalarResponseBodyConverters.ByteResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.ByteResponseBodyConverter();

        ByteResponseBodyConverter() {
        }

        public Byte convert(ResponseBody value) throws IOException {
            return Byte.valueOf(value.string());
        }
    }

    static final class BooleanResponseBodyConverter implements Converter<ResponseBody, Boolean> {
        static final ScalarResponseBodyConverters.BooleanResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.BooleanResponseBodyConverter();

        BooleanResponseBodyConverter() {
        }

        public Boolean convert(ResponseBody value) throws IOException {
            return Boolean.valueOf(value.string());
        }
    }

    static final class StringResponseBodyConverter implements Converter<ResponseBody, String> {
        static final ScalarResponseBodyConverters.StringResponseBodyConverter INSTANCE = new ScalarResponseBodyConverters.StringResponseBodyConverter();

        StringResponseBodyConverter() {
        }

        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }
}

