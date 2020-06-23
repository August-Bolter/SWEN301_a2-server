package test.nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsPNGServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test cases for the StatsPNGServlet class */
public class TestStatsPNG {

    /** Testing that no log statistics are generated if request is null */
    @Test
    public void testRequestNull() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsPNGServlet service = new StatsPNGServlet();
        service.doGet(null, response);
        assertEquals(0, response.getContentAsString().length());
    }

    /** Testing that no log statistics are generated if no logs exist on the server */
    @Test
    public void testLogsEmpty() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsPNGServlet service = new StatsPNGServlet();
        new LogsServlet();
        service.doGet(request, response);
        assertEquals(0, response.getContentAsString().length());
    }

    /** Testing that log statistics are generated if logs exist on the server (just one log exists on server) */
    @Test
    public void testOneLogStats() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsPNGServlet service = new StatsPNGServlet();
        new LogsServlet();

        /* Creating JsonObjects to test */
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "INFO");

        LogsServlet.logs.add(newObj);
        service.doGet(request, response);
        assertEquals("image/png", response.getContentType()); //Checking content type is PNG
        assertEquals(200, response.getStatus()); //Checking response code is correct
    }

    /** Testing that log statistics are generated if logs exist on the server (multiple logs exists on server) */
    @Test
    public void testMultipleLogsStats() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsPNGServlet service = new StatsPNGServlet();
        new LogsServlet();

        /* Creating JsonObjects to test */
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "INFO");

        JsonObject newObj1 = new JsonObject();
        newObj1.addProperty("id", "e290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj1.addProperty("message", "Threat received");
        newObj1.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Bar");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "concurrent");
        newObj2.addProperty("logger", "com.example.Baz");
        newObj2.addProperty("level", "ERROR");

        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        service.doGet(request, response);
        assertEquals("image/png", response.getContentType());
        assertEquals(200, response.getStatus());
    }
}
