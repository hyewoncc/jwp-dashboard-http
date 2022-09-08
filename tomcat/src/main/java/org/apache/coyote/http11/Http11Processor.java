package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest request = readHttpRequest(reader);
            HttpResponse response = requestMapping.process(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        List<String> headerLines = readHeaders(reader);
        HttpRequest request = HttpRequest.from(requestLine, headerLines);

        if (request.hasHeader(Header.CONTENT_LENGTH)) {
            readBody(reader, request);
        }
        return request;
    }

    private void readBody(final BufferedReader reader, final HttpRequest request) throws IOException {
        int contentLength = Integer.parseInt(request.getHeaderValue(Header.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        request.addBody(requestBody);
    }

    private List<String> readHeaders(final BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (line.isEmpty()) {
                break;
            }
            headerLines.add(line);
        }
        return headerLines;
    }
}
