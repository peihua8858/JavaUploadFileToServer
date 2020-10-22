package com.fz.upload.http;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class GsonConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactory create() {
        return create(new Gson());
    }

    public static GsonConverterFactory create(MediaType mediaType) {

        return create(new Gson(), mediaType);
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactory create(Gson gson) {
        return new GsonConverterFactory(gson);
    }

    public static GsonConverterFactory create(Gson gson, MediaType mediaType) {
        return new GsonConverterFactory(gson, mediaType);
    }

    private final Gson gson;
    private final MediaType mediaType;

    private GsonConverterFactory(Gson gson) {
        this(gson, RetrofitClient.MEDIA_TYPE);
    }

    private GsonConverterFactory(Gson gson, MediaType mediaType) {
        if (gson == null) {
            gson = new Gson();
        }
        this.gson = gson;
        this.mediaType = mediaType;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == String.class) {
            return StringConverter.INSTANCE;
        }
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter, mediaType);
    }

    /**
     * http 响应String类型数据处理
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2016/12/24 11:42
     */
    final static class StringConverter implements Converter<ResponseBody, String> {

        public static final StringConverter INSTANCE = new StringConverter();

        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }

    static final class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;
        private final MediaType mediaType;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter, MediaType mediaType) {
            this.gson = gson;
            this.adapter = adapter;
            this.mediaType = mediaType;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(mediaType, buffer.readByteString());
        }
    }


    final static class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(value.bytes());
                Reader reader = new BufferedReader(new InputStreamReader(bis));
                JsonReader jsonReader = gson.newJsonReader(reader);
                T t = adapter.read(jsonReader);
                reader.close();
                bis.close();
                return t;
            } finally {
                value.close();
            }
        }
    }
}
