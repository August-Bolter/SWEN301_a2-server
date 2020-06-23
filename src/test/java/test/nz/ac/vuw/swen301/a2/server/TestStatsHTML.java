package test.nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsServlet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/** Test cases for the StatsServlet class */
public class TestStatsHTML {

    /** Testing that no log statistics are generated if request is null */
    @Test
    public void testRequestNull() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsServlet service = new StatsServlet();
        service.doGet(null, response);
        assertEquals(0, response.getContentAsString().length());
    }

    /** Testing that no log statistics are generated if no logs exist on the server */
    @Test
    public void testLogsEmpty() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsServlet service = new StatsServlet();
        new LogsServlet();
        service.doGet(request, response);
        assertEquals(0, response.getContentAsString().length());
    }

    /** Testing that log statistics are generated if logs exist on the server (just one log exists on server) */
    @Test
    public void testOneLogStats() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsServlet service = new StatsServlet();
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
        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
        Document doc = Jsoup.parse(response.getContentAsString());
        /* Checking html document has the basic structure */
        assertNotEquals(null, doc.select("html"));
        assertNotEquals(null, doc.select("head"));
        assertNotEquals(null, doc.select("title"));
        assertEquals("Log statistics", doc.select("title").text());
        assertNotEquals(null, doc.select("body"));
        /* Checking that table exists */
        assertNotEquals(null, doc.select("table"));
        Element table = doc.select("table").get(0);
        /* Checking that table rows exist */
        Elements rows = table.select("tr");
        assertNotEquals(null, rows);
        Element rowHeader = rows.get(0);
        /* Checking that table headers exist and are correct */
        Elements colsHeader = rowHeader.select("th");
        assertEquals("name", colsHeader.get(0).text());
        assertEquals(newObj.get("timestamp").toString().substring(1, 11), colsHeader.get(1).text());

        /* Checking that table rows are correct */

        /* Checking row 1 */
        Element row = rows.get(1);
        Elements cols = row.select("td");
        assertEquals(newObj.get("logger").toString().substring(1, newObj.get("logger").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());

        /* Checking row 2 etc../ */
        row = rows.get(2);
        cols = row.select("td");
        assertEquals(newObj.get("level").toString().substring(1, newObj.get("level").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());

        row = rows.get(3);
        cols = row.select("td");
        assertEquals(newObj.get("thread").toString().substring(1, newObj.get("thread").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
    }

    /** Testing that log statistics are generated if logs exist on the server (multiple logs exists on server, and that one of the log table values gets incremented) */
    @Test
    public void testMultipleLogsStatsOneIncrement() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsServlet service = new StatsServlet();
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
        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
        Document doc = Jsoup.parse(response.getContentAsString());
        /* Checking html document has the basic structure */
        assertNotEquals(null, doc.select("html"));
        assertNotEquals(null, doc.select("head"));
        assertNotEquals(null, doc.select("title"));
        assertEquals("Log statistics", doc.select("title").text());
        assertNotEquals(null, doc.select("body"));
        /* Checking that table exists */
        assertNotEquals(null, doc.select("table"));
        Element table = doc.select("table").get(0);
        /* Checking that table rows exist */
        Elements rows = table.select("tr");
        assertNotEquals(null, rows);
        Element rowHeader = rows.get(0);
        /* Checking that table headers exist and are correct */
        Elements colsHeader = rowHeader.select("th");
        assertEquals("name", colsHeader.get(0).text());
        assertEquals(newObj.get("timestamp").toString().substring(1, 11), colsHeader.get(1).text());
        assertEquals(newObj2.get("timestamp").toString().substring(1, 11), colsHeader.get(2).text());

        /* Checking that table rows are correct */

        /* Checking row 1 */
        Element row = rows.get(1);
        Elements cols = row.select("td");
        assertEquals(newObj1.get("logger").toString().substring(1, newObj1.get("logger").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        /* Checking row 2 etc... */
        row = rows.get(2);
        cols = row.select("td");
        assertEquals(newObj.get("logger").toString().substring(1, newObj.get("logger").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(3);
        cols = row.select("td");
        assertEquals(newObj2.get("logger").toString().substring(1, newObj2.get("logger").toString().length()-1), cols.get(0).text());
        assertEquals("0", cols.get(1).text());
        assertEquals("1", cols.get(2).text());

        row = rows.get(4);
        cols = row.select("td");
        assertEquals(newObj1.get("level").toString().substring(1, newObj1.get("level").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(5);
        cols = row.select("td");
        assertEquals(newObj.get("level").toString().substring(1, newObj.get("level").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(6);
        cols = row.select("td");
        assertEquals(newObj2.get("level").toString().substring(1, newObj2.get("level").toString().length()-1), cols.get(0).text());
        assertEquals("0", cols.get(1).text());
        assertEquals("1", cols.get(2).text());

        row = rows.get(7);
        cols = row.select("td");
        assertEquals(newObj.get("thread").toString().substring(1, newObj.get("thread").toString().length()-1), cols.get(0).text());
        assertEquals("2", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(8);
        cols = row.select("td");
        assertEquals(newObj2.get("thread").toString().substring(1, newObj2.get("thread").toString().length()-1), cols.get(0).text());
        assertEquals("0", cols.get(1).text());
        assertEquals("1", cols.get(2).text());
    }

    /** Testing that log statistics are generated if logs exist on the server (multiple logs exists on server, and that more than one of the log table values gets incremented) */
    @Test
    public void testMultipleLogsStatsMultipleIncrements() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsServlet service = new StatsServlet();
        new LogsServlet();

        /* Creating JsonObjects to test */
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "daemon");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");

        JsonObject newObj1 = new JsonObject();
        newObj1.addProperty("id", "e290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj1.addProperty("message", "Threat received");
        newObj1.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
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
        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
        Document doc = Jsoup.parse(response.getContentAsString());
        /* Checking html document has the basic structure */
        assertNotEquals(null, doc.select("html"));
        assertNotEquals(null, doc.select("head"));
        assertNotEquals(null, doc.select("title"));
        assertEquals("Log statistics", doc.select("title").text());
        assertNotEquals(null, doc.select("body"));
        /* Checking that table exists */
        assertNotEquals(null, doc.select("table"));
        Element table = doc.select("table").get(0);
        /* Checking that table rows exist */
        Elements rows = table.select("tr");
        assertNotEquals(null, rows);
        Element rowHeader = rows.get(0);
        /* Checking that table headers exist and are correct */
        Elements colsHeader = rowHeader.select("th");
        assertEquals("name", colsHeader.get(0).text());
        assertEquals(newObj.get("timestamp").toString().substring(1, 11), colsHeader.get(1).text());
        assertEquals(newObj2.get("timestamp").toString().substring(1, 11), colsHeader.get(2).text());

        /* Checking that table rows are correct */

        /* Checking row 1 */
        Element row = rows.get(1);
        Elements cols = row.select("td");
        assertEquals(newObj.get("logger").toString().substring(1, newObj.get("logger").toString().length()-1), cols.get(0).text());
        assertEquals("2", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        /* Checking row 2 etc... */
        row = rows.get(2);
        cols = row.select("td");
        assertEquals(newObj2.get("logger").toString().substring(1, newObj2.get("logger").toString().length()-1), cols.get(0).text());
        assertEquals("0", cols.get(1).text());
        assertEquals("1", cols.get(2).text());

        row = rows.get(3);
        cols = row.select("td");
        assertEquals(newObj.get("level").toString().substring(1, newObj.get("level").toString().length()-1), cols.get(0).text());
        assertEquals("2", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(4);
        cols = row.select("td");
        assertEquals(newObj2.get("level").toString().substring(1, newObj2.get("level").toString().length()-1), cols.get(0).text());
        assertEquals("0", cols.get(1).text());
        assertEquals("1", cols.get(2).text());

        row = rows.get(5);
        cols = row.select("td");
        assertEquals(newObj1.get("thread").toString().substring(1, newObj1.get("thread").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(6);
        cols = row.select("td");
        assertEquals(newObj.get("thread").toString().substring(1, newObj.get("thread").toString().length()-1), cols.get(0).text());
        assertEquals("1", cols.get(1).text());
        assertEquals("0", cols.get(2).text());

        row = rows.get(7);
        cols = row.select("td");
        assertEquals(newObj2.get("thread").toString().substring(1, newObj2.get("thread").toString().length()-1), cols.get(0).text());
        assertEquals("0", cols.get(1).text());
        assertEquals("1", cols.get(2).text());
    }

}

