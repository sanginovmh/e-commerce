package uz.pdp.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class FileUtils {
    private static final String PATH = "src/main/java/uz/pdp/data/";

    private static final ObjectMapper objectMapper;
    private static final XmlMapper xmlMapper;

    static {
        objectMapper = JsonMapper.builder()
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .build();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        xmlMapper = XmlMapper.builder()
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .build();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Writes a list of objects to a JSON file.
     *
     * @param fileName the name of the file to write to
     * @param t        the list of objects to write
     * @param <T>      the type of objects in the list
     * @throws IOException if an I/O error occurs
     */
    public static <T> void writeToJson(String fileName, T t) throws IOException {
        objectMapper.writeValue(new File(PATH + fileName), t);
    }

    /**
     * Reads a list of objects from a JSON file.
     *
     * @param fileName the name of the file to read from
     * @param <T>      the type of objects in the list
     * @return a list of objects read from the file
     * @throws IOException if an I/O error occurs
     */
    public static <T> List<T> readFromJson(String fileName, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(new File(PATH + fileName),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            if (e.getMessage().contains("No content to map due to end-of-input")) {
                return new ArrayList<>();
            }
            throw e;
        }
    }

    /**
     * Writes an object to an XML file.
     *
     * @param fileName the name of the file to write to
     * @param t        the object to write
     * @param <T>      the type of the object
     * @throws IOException if an I/O error occurs
     */
    public static <T> void writeToXml(String fileName, T t) throws IOException {
        xmlMapper.writeValue(new File(PATH + fileName), t);
    }

    /**
     * Reads a list of objects from an XML file.
     *
     * @param fileName the name of the file to read from
     * @param clazz    the class type of the objects in the list
     * @param <T>      the type of objects in the list
     * @return a list of objects read from the file
     * @throws IOException if an I/O error occurs
     */
    public static <T> List<T> readFromXml(String fileName, Class<T> clazz) throws IOException {
        try {
            return xmlMapper.readValue(new File(PATH + fileName),
                    xmlMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            if (e.getMessage().contains("No content to map due to end-of-input")) {
                return new ArrayList<>();
            }
            throw e;
        }
    }
}