package com.pandaq.rxpanda.gsonadapter;


import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.pandaq.rxpanda.config.HttpGlobalConfig;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by huxinyu on 2019/7/19.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class DefaultTypeAdapters {


    private DefaultTypeAdapters() {
        throw new UnsupportedOperationException();
    }

    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                // 返回配置的默认值
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    return HttpGlobalConfig.getInstance().getDefValues().defBoolean;
                } else {
                    return false;
                }
            } else if (peek == JsonToken.STRING) {
                // support strings for compatibility with GSON 1.7
                return Boolean.parseBoolean(in.nextString());
            }
            return in.nextBoolean();
        }

        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defBoolean;
                } else {
                    value = false;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                // 返回配置的默认值
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    return HttpGlobalConfig.getInstance().getDefValues().defBoolean;
                } else {
                    return false;
                }
            }
            if (in.peek() == JsonToken.BOOLEAN) {
                return in.nextBoolean();
            }
            return Boolean.valueOf(in.nextString());
        }

        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defBoolean;
                } else {
                    value = false;
                }
            }
            out.value(value);
        }
    };


    public static final TypeAdapter<Integer> INTEGER = new TypeAdapter<Integer>() {
        @Override
        public Integer read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                        return HttpGlobalConfig.getInstance().getDefValues().defInt;
                    } else {
                        return 0;
                    }
                case NUMBER:
                    return in.nextInt();
                case STRING:
                    String str = in.nextString();
                    if (TextUtils.isEmpty(str)) {
                        if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                            return HttpGlobalConfig.getInstance().getDefValues().defInt;
                        } else {
                            return 0;
                        }
                    }
                    return Integer.valueOf(str);
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defInt;
                } else {
                    value = 0;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<Long> LONG = new TypeAdapter<Long>() {
        @Override
        public Long read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                        return HttpGlobalConfig.getInstance().getDefValues().defLong;
                    } else {
                        return 0L;
                    }
                case NUMBER:
                    return in.nextLong();
                case STRING:
                    String str = in.nextString();
                    if (TextUtils.isEmpty(str)) {
                        if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                            return HttpGlobalConfig.getInstance().getDefValues().defLong;
                        } else {
                            return 0L;
                        }
                    }
                    return Long.valueOf(str);
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Long value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defLong;
                } else {
                    value = 0L;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<Float> FLOAT = new TypeAdapter<Float>() {
        @Override
        public Float read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                        return HttpGlobalConfig.getInstance().getDefValues().defFloat;
                    } else {
                        return 0.0f;
                    }
                case NUMBER:
                    return (float) in.nextDouble();
                case STRING:
                    String str = in.nextString();
                    if (TextUtils.isEmpty(str)) {
                        if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                            return HttpGlobalConfig.getInstance().getDefValues().defFloat;
                        } else {
                            return 0.0f;
                        }
                    }
                    return Float.valueOf(str);
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Float value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defFloat;
                } else {
                    value = 0.0f;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<Double> DOUBLE = new TypeAdapter<Double>() {
        @Override
        public Double read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                        return HttpGlobalConfig.getInstance().getDefValues().defDouble;
                    } else {
                        return 0.0;
                    }
                case NUMBER:
                    return in.nextDouble();
                case STRING:
                    String str = in.nextString();
                    if (TextUtils.isEmpty(str)) {
                        if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                            return HttpGlobalConfig.getInstance().getDefValues().defDouble;
                        } else {
                            return 0.0;
                        }
                    }
                    return Double.valueOf(str);
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Double value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defDouble;
                } else {
                    value = 0.0;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<Number> NUMBER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                        return HttpGlobalConfig.getInstance().getDefValues().defDouble;
                    } else {
                        return 0;
                    }
                case NUMBER:
                case STRING:
                    String str = in.nextString();
                    if (TextUtils.isEmpty(str)) {
                        if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                            return HttpGlobalConfig.getInstance().getDefValues().defDouble;
                        } else {
                            return 0;
                        }
                    }
                    return new LazilyParsedNumber(str);
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defDouble;
                } else {
                    value = 0;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    return HttpGlobalConfig.getInstance().getDefValues().defString;
                } else {
                    return null;
                }
            }
            if (peek == JsonToken.NUMBER) {
                double dbNum = in.nextDouble();
                if (dbNum > Long.MAX_VALUE) {
                    return String.valueOf(dbNum);
                }
                // 如果是整数
                if (dbNum == (long) dbNum) {
                    return String.valueOf((long) dbNum);
                } else {
                    return String.valueOf(dbNum);
                }
            }
            /* coerce booleans to strings for backwards compatibility */
            if (peek == JsonToken.BOOLEAN) {
                return Boolean.toString(in.nextBoolean());
            }
            return in.nextString();
        }

        @Override
        public void write(JsonWriter out, String value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = HttpGlobalConfig.getInstance().getDefValues().defString;
                }
            }
            out.value(value);
        }
    };

    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
        @Override
        public BigDecimal read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    return new BigDecimal(HttpGlobalConfig.getInstance().getDefValues().defInt);
                } else {
                    return new BigDecimal(0);
                }
            }
            try {
                return new BigDecimal(in.nextString());
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public void write(JsonWriter out, BigDecimal value) throws IOException {
            if (value == null) {
                if (HttpGlobalConfig.getInstance().getDefValues() != null) {
                    value = new BigDecimal(HttpGlobalConfig.getInstance().getDefValues().defInt);
                } else {
                    value = new BigDecimal(0);

                }
            }
            out.value(value);
        }
    };
}
