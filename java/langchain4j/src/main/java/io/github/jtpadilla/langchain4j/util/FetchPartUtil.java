package io.github.jtpadilla.langchain4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

/**
 * Utilidad para descargar recursos binarios (p.ej. PDFs) desde una URL.
 * Equivalente a {@code FethPartUtil} de la librería google-genai.
 *
 * <p>En LangChain4j no existe un tipo único de «Part» para contenido binario,
 * por lo que este utilitario devuelve un {@link FetchedContent} con los bytes
 * y el MIME type. Incorpora los datos en un {@code UserMessage} multimodal
 * según la API del modelo que estés usando, por ejemplo:
 * <pre>{@code
 * FetchedContent pdf = FetchPartUtil.fetch("https://example.com/doc.pdf", "application/pdf");
 * // Para Gemini vía LangChain4j, añade el contenido base64 en el mensaje de usuario.
 * String base64 = pdf.toBase64();
 * }</pre>
 */
public class FetchPartUtil {

    private static final int CONNECT_TIMEOUT_MILLIS = 10_000;
    private static final int READ_TIMEOUT_MILLIS = 30_000;

    public static final String MIME_TYPE_PDF = "application/pdf";

    /**
     * Descarga el recurso en {@code url} y lo devuelve como {@link FetchedContent}.
     */
    public static FetchedContent fetch(String url, String mimeType) throws FetchPartException {
        final byte[] data = readBytes(createConnection(url));
        return new FetchedContent(data, mimeType);
    }

    /**
     * Atajo para descargar un PDF.
     */
    public static FetchedContent fetchPdf(String pdfUrl) throws FetchPartException {
        return fetch(pdfUrl, MIME_TYPE_PDF);
    }

    // -------------------------------------------------------------------------

    /**
     * Resultado de una descarga: bytes raw + MIME type.
     */
    public record FetchedContent(byte[] data, String mimeType) {

        /** Devuelve los bytes codificados en Base64, útil para APIs multimodales. */
        public String toBase64() {
            return Base64.getEncoder().encodeToString(data);
        }
    }

    // -------------------------------------------------------------------------

    private static URLConnection createConnection(String urlString) throws FetchPartException {
        try {
            final URL url = new URI(urlString).toURL();
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);
            return connection;
        } catch (URISyntaxException e) {
            throw new FetchPartException("Invalid URL: " + urlString, e);
        } catch (IOException e) {
            throw new FetchPartException("I/O error while opening connection to: " + urlString, e);
        }
    }

    private static byte[] readBytes(URLConnection connection) throws FetchPartException {
        try (InputStream inputStream = connection.getInputStream()) {
            final byte[] bytes = inputStream.readAllBytes();
            if (bytes.length == 0) {
                throw new FetchPartException("Remote resource returned empty content");
            }
            return bytes;
        } catch (IOException e) {
            throw new FetchPartException("I/O error while reading response bytes", e);
        }
    }

}
