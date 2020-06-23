package test.nz.ac.vuw.swen301.a2.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/** Class that contains the tests for the doGet() method in LogsServlet. */
public class TestGetLogs {

    /** Testing that 400 error code is the response when request is given with no parameters */
    @Test
    public void testInvalidResponseCodeNoParam() throws IOException {
        /* Creating mock request and response */
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doGet(request, response);
        assertEquals(400, response.getStatus()); //400 is the failure response code
    }

    /** Testing that 400 error code is the response when request is given with only the level parameter */
    @Test
    public void testInvalidResponseCodeOneParamLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        /* Adding level parameter but not adding limit parameter */
        request.setParameter("level", "WARN");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    /** Testing that 400 error code is the response when request is given with only the limit parameter */
    @Test
    public void testInvalidResponseCodeOneParamLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        /* Adding limit parameter but not adding level parameter */
        request.setParameter("limit", "42");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    /** Testing that 400 error code is the response when request is given with a negative limit */
    @Test
    public void testInvalidResponseCodeNegativeLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();

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
        newObj1.addProperty("timestamp", "2020-04-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "main");
        newObj2.addProperty("logger", "com.example.Foo");
        newObj2.addProperty("level", "ERROR");

        /* Adding JsonObjects */
        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        request.setParameter("limit", "-2"); //Limit can't be negative
        request.setParameter("level", "WARN");
        service.doGet(request, response);
        LogsServlet.logs.clear(); //Clearing the logs since the logs variable is static
        assertEquals(400, response.getStatus());
    }

    /** Testing that 400 error code is the response when request is given with a limit that isn't an integer */
    @Test
    public void testInvalidResponseCodeNonIntLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.setParameter("limit", "lim"); //Limit has to be an integer
        request.setParameter("level", "WARN");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    /** Testing that 400 error code is the response when request is given with a level that isn't valid (outside of enum 'levels' scope in LogsServlet) */
    @Test
    public void testInvalidResponseCodeInvalidLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.setParameter("level", "test"); //Level has to be part of 'levels' enum in LogsServlet
        request.setParameter("limit", "3");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    /** Testing that doGet() works (responds with 200 status code) when given a request (that has valid parameters) and a response. */
    @Test
    public void testValidResponseCode() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();

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
        newObj1.addProperty("timestamp", "2020-04-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "main");
        newObj2.addProperty("logger", "com.example.Foo");
        newObj2.addProperty("level", "ERROR");

        /* Adding JsonObjects */
        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        request.setParameter("limit", "3");
        request.setParameter("level", "INFO");
        service.doGet(request, response);
        LogsServlet.logs.clear();
        assertEquals(200, response.getStatus()); //200 is the success response code
    }

    /** Tests that the log events in the response are formatted correctly (and contain all the relevant fields) and are sorted
     * by timestamp (latest logs are first) */
    @Test
    public void testTimestampSorted() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();

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
        newObj1.addProperty("timestamp", "2020-04-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "main");
        newObj2.addProperty("logger", "com.example.Foo");
        newObj2.addProperty("level", "ERROR");

        /* Adding JsonObjects */
        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        request.setParameter("limit", "3");
        request.setParameter("level", "INFO");
        service.doGet(request, response);
        LogsServlet.logs.clear();
        String result = response.getContentAsString(); //Getting logs as a Json string
        Gson g = new Gson();
        JsonArray logs = g.fromJson(result, JsonArray.class); //Converting Json string to Json array
        assertEquals(3, logs.size()); //Checking that the right amount of logs was returned
        /* Checking that the JsonObject has all the right fields (encapsulated in "") and that the logs are ordered by
        * timestamp. */
        assertEquals("\"e290f1ee-6c54-4b01-90e6-d701748f0851\"", logs.get(0).getAsJsonObject().get("id").toString());
        assertEquals("\"Threat received\"", logs.get(0).getAsJsonObject().get("message").toString());
        assertEquals("\"2020-04-29T09:12:33.001Z\"", logs.get(0).getAsJsonObject().get("timestamp").toString());
        assertEquals("\"main\"", logs.get(0).getAsJsonObject().get("thread").toString());
        assertEquals("\"com.example.Foo\"", logs.get(0).getAsJsonObject().get("logger").toString());
        assertEquals("\"WARN\"", logs.get(0).getAsJsonObject().get("level").toString());

        assertEquals("\"c290f1ee-6c54-4b01-90e6-d701748f0851\"", logs.get(1).getAsJsonObject().get("id").toString());
        assertEquals("\"Unexpected encounter\"", logs.get(1).getAsJsonObject().get("message").toString());
        assertEquals("\"2020-03-29T09:12:33.001Z\"", logs.get(1).getAsJsonObject().get("timestamp").toString());
        assertEquals("\"main\"", logs.get(1).getAsJsonObject().get("thread").toString());
        assertEquals("\"com.example.Foo\"", logs.get(1).getAsJsonObject().get("logger").toString());
        assertEquals("\"ERROR\"", logs.get(1).getAsJsonObject().get("level").toString());

        assertEquals("\"d290f1ee-6c54-4b01-90e6-d701748f0851\"", logs.get(2).getAsJsonObject().get("id").toString());
        assertEquals("\"Everything running smoothly\"", logs.get(2).getAsJsonObject().get("message").toString());
        assertEquals("\"2019-07-29T09:12:33.001Z\"", logs.get(2).getAsJsonObject().get("timestamp").toString());
        assertEquals("\"main\"", logs.get(2).getAsJsonObject().get("thread").toString());
        assertEquals("\"com.example.Foo\"", logs.get(2).getAsJsonObject().get("logger").toString());
        assertEquals("\"INFO\"", logs.get(2).getAsJsonObject().get("level").toString());

    }

    /** Tests that the content type of the response is valid */
    @Test
    public void testValidContentType() throws IOException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.setParameter("limit", "3");
        request.setParameter("level", "INFO");
        service.doGet(request, response);
        assertNotNull(response.getContentType());
        assertTrue(response.getContentType().startsWith("application/json")); //Content type should be application/json since Json String is being returned
    }

    /** Tests that request's limit parameter limits the number of logs in the response */
    @Test
    public void testLimitResponse() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();

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
        newObj1.addProperty("timestamp", "2020-04-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "main");
        newObj2.addProperty("logger", "com.example.Foo");
        newObj2.addProperty("level", "ERROR");

        /* Adding JsonObjects */
        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        request.setParameter("limit", "2");
        request.setParameter("level", "INFO");
        service.doGet(request, response);
        LogsServlet.logs.clear();
        String result = response.getContentAsString();
        Gson g = new Gson();
        JsonArray logs = g.fromJson(result, JsonArray.class);
        assertEquals(2, logs.size()); //Since limit is 2 only 2 logs should be returned.
    }

    /** Tests that request's level parameter filters the response to logs that have a level equal to or higher than the level parameter */
    @Test
    public void testLevelFilteringResponse() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();

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
        newObj1.addProperty("timestamp", "2020-04-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "main");
        newObj2.addProperty("logger", "com.example.Foo");
        newObj2.addProperty("level", "ERROR");

        /* Adding JsonObjects */
        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        request.setParameter("limit", "3");
        request.setParameter("level", "WARN");
        service.doGet(request, response);
        LogsServlet.logs.clear();
        String result = response.getContentAsString();
        Gson g = new Gson();
        JsonArray logs = g.fromJson(result, JsonArray.class);
        /* Testing that only WARN and ERROR logs are returned and the log with INFO level isn't since minimum level was set to WARN */
        assertEquals(2, logs.size());
        assertEquals("\"WARN\"", logs.get(0).getAsJsonObject().get("level").toString());
        assertEquals("\"ERROR\"", logs.get(1).getAsJsonObject().get("level").toString());
    }

    /** Tests that when the response responds with only one log that a JsonObject string is produced not a JsonArray string */
    @Test
    public void testJsonObjectResponse() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();

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
        newObj1.addProperty("timestamp", "2020-04-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");

        JsonObject newObj2 = new JsonObject();
        newObj2.addProperty("id", "c290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj2.addProperty("message", "Unexpected encounter");
        newObj2.addProperty("timestamp", "2020-03-29T09:12:33.001Z");
        newObj2.addProperty("thread", "main");
        newObj2.addProperty("logger", "com.example.Foo");
        newObj2.addProperty("level", "ERROR");

        /* Adding JsonObjects */
        LogsServlet.logs.add(newObj);
        LogsServlet.logs.add(newObj1);
        LogsServlet.logs.add(newObj2);

        request.setParameter("limit", "1");
        request.setParameter("level", "WARN");
        service.doGet(request, response);
        LogsServlet.logs.clear();
        String result = response.getContentAsString();
        assertTrue(result.startsWith("{") && result.endsWith("}")); //JsonArray strings starts with [] whereas JsonObject strings start with {}
    }
}
