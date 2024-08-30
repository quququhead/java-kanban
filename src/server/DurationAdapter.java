package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.toMinutes());
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String string = jsonReader.nextString();
        if (string.equals("null")) {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(string));
    }
}