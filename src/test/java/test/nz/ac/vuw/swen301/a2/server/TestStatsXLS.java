package test.nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import nz.ac.vuw.swen301.a2.server.StatsXLSServlet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test cases for the StatsCSVServlet class */
public class TestStatsXLS {
    @Test
    /** Testing that no log statistics are generated if request is null */
    public void testRequestNull() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet service = new StatsXLSServlet();
        service.doGet(null, response);
        assertEquals(0, response.getContentAsString().length());
    }

    @Test
    /** Testing that no log statistics are generated if server logs is null */
    public void testLogsNull() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet service = new StatsXLSServlet();
        service.doGet(request, response);
        assertEquals(0, response.getContentAsString().length());
    }

    @Test
    /** Testing that no log statistics are generated if no logs exist on the server */
    public void testLogsEmpty() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet service = new StatsXLSServlet();
        LogsServlet server = new LogsServlet();
        service.doGet(request, response);
        assertEquals(0, response.getContentAsString().length());
    }

    @Test
    /** Testing that log statistics are generated if logs exist on the server (just one log exists on server) */
    public void testOneLogStats() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet service = new StatsXLSServlet();
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
        assertEquals("application/vnd.ms-excel", response.getContentType());
        assertEquals(200, response.getStatus());

        InputStream is = new ByteArrayInputStream(response.getContentAsByteArray());
        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        assertEquals("name", row.getCell(0).toString());
        assertEquals(newObj.get("timestamp").toString().substring(1, 11), row.getCell(1).toString());
        row = sheet.getRow(1);
        assertEquals(newObj.get("logger").toString().substring(1, newObj.get("logger").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());
        row = sheet.getRow(2);
        assertEquals(newObj.get("level").toString().substring(1, newObj.get("level").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());
        row = sheet.getRow(3);
        assertEquals(newObj.get("thread").toString().substring(1, newObj.get("thread").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());

    }
    @Test
    /** Testing that log statistics are generated if logs exist on the server (multiple logs exists on server) */
    public void testMultipleLogsEmpty() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet service = new StatsXLSServlet();
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
        assertEquals("application/vnd.ms-excel", response.getContentType());
        assertEquals(200, response.getStatus());

        InputStream is = new ByteArrayInputStream(response.getContentAsByteArray());
        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        assertEquals("name", row.getCell(0).toString());
        assertEquals(newObj.get("timestamp").toString().substring(1, 11), row.getCell(1).toString());
        assertEquals(newObj2.get("timestamp").toString().substring(1, 11), row.getCell(2).toString());

        row = sheet.getRow(1);
        assertEquals(newObj1.get("logger").toString().substring(1, newObj1.get("logger").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());
        assertEquals("0", row.getCell(2).toString());

        row = sheet.getRow(2);
        assertEquals(newObj.get("logger").toString().substring(1, newObj.get("logger").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());
        assertEquals("0", row.getCell(2).toString());

        row = sheet.getRow(3);
        assertEquals(newObj2.get("logger").toString().substring(1, newObj2.get("logger").toString().length()-1), row.getCell(0).toString());
        assertEquals("0", row.getCell(1).toString());
        assertEquals("1", row.getCell(2).toString());

        row = sheet.getRow(4);
        assertEquals(newObj1.get("level").toString().substring(1, newObj1.get("level").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());
        assertEquals("0", row.getCell(2).toString());

        row = sheet.getRow(5);
        assertEquals(newObj.get("level").toString().substring(1, newObj.get("level").toString().length()-1), row.getCell(0).toString());
        assertEquals("1", row.getCell(1).toString());
        assertEquals("0", row.getCell(2).toString());

        row = sheet.getRow(6);
        assertEquals(newObj2.get("level").toString().substring(1, newObj2.get("level").toString().length()-1), row.getCell(0).toString());
        assertEquals("0", row.getCell(1).toString());
        assertEquals("1", row.getCell(2).toString());

        row = sheet.getRow(7);
        assertEquals(newObj.get("thread").toString().substring(1, newObj.get("thread").toString().length()-1), row.getCell(0).toString());
        assertEquals("2", row.getCell(1).toString());
        assertEquals("0", row.getCell(2).toString());

        row = sheet.getRow(8);
        assertEquals(newObj2.get("thread").toString().substring(1, newObj2.get("thread").toString().length()-1), row.getCell(0).toString());
        assertEquals("0", row.getCell(1).toString());
        assertEquals("1", row.getCell(2).toString());
    }

}
