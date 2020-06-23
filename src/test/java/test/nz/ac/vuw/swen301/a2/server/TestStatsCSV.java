package test.nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsCSVServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test cases for the StatsCSVServlet class */
public class TestStatsCSV {
    @Test
    /** Testing that no log statistics are generated if request is null */
    public void testRequestNull() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet service = new StatsCSVServlet();
        service.doGet(null, response);
        assertEquals(0, response.getContentAsString().length());
    }

    @Test
    /** Testing that no log statistics are generated if server logs is null */
    public void testLogsNull() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet service = new StatsCSVServlet();
        service.doGet(request, response);
        assertEquals(0, response.getContentAsString().length());
    }

    @Test
    /** Testing that no log statistics are generated if no logs exist on the server */
    public void testLogsEmpty() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet service = new StatsCSVServlet();
        LogsServlet server = new LogsServlet();
        service.doGet(request, response);
        assertEquals(0, response.getContentAsString().length());
    }

    @Test
    /** Testing that log statistics are generated if logs exist on the server (just one log exists on server) */
    public void testOneLogStats() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet service = new StatsCSVServlet();
        LogsServlet server = new LogsServlet();

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
        assertEquals("name\t2019-07-29\n" +
                "com.example.Foo\t1\n" +
                "INFO\t1\n" +
                "main\t1", response.getContentAsString());
    }
    @Test
    /** Testing that log statistics are generated if logs exist on the server (multiple logs exists on server) */
    public void testMultipleLogsEmpty() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet service = new StatsCSVServlet();
        LogsServlet server = new LogsServlet();

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
        assertEquals("name\t2019-07-29\t2020-03-29\n" +
                "com.example.Bar\t1\t0\n" +
                "com.example.Foo\t1\t0\n" +
                "com.example.Baz\t0\t1\n" +
                "WARN\t1\t0\n" +
                "INFO\t1\t0\n" +
                "ERROR\t0\t1\n" +
                "main\t2\t0\n" +
                "concurrent\t0\t1", response.getContentAsString());
    }

}
