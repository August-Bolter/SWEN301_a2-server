package nz.ac.vuw.swen301.a2.server;
import com.google.gson.*;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/** Class the contains the tests for the doGet() method in LogsServlet. A lot of these test cases use the addTestingLogs()
 * method in the LogsServlet therefore the expected response is measured against these test logs. */
public class TestGetLogs {
    @Test
    /** Testing that 400 error code is the response when request is given with no parameters */
    public void testInvalidResponseCodeNoParam() throws IOException {
        /* Creating mock request and response */
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doGet(request, response);
        assertEquals(400, response.getStatus()); //400 is the failure response code
    }

    @Test
    /** Testing that 400 error code is the response when request is given with only the level parameter */
    public void testInvalidResponseCodeOneParamLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        /* Adding level parameter but not adding limit parameter */
        request.addParameter("level", "WARN");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with only the limit parameter */
    public void testInvalidResponseCodeOneParamLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        /* Adding limit parameter but not adding level parameter */
        request.addParameter("limit", "42");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with a negative limit */
    public void testInvalidResponseCodeNegativeLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.addTestingLogs();
        request.addParameter("limit", "-2"); //Limit can't be negative
        request.addParameter("level", "WARN");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with a limit that isn't an integer */
    public void testInvalidResponseCodeNonIntLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.addParameter("limit", "lim"); //Limit has to be an integer
        request.addParameter("level", "WARN");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with a level that isn't valid (outside of enum 'levels' scope in LogsServlet) */
    public void testInvalidResponseCodeInvalidLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.addParameter("level", "test"); //Level has to be part of 'levels' enum in LogsServlet
        request.addParameter("limit", "3");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that doGet() works (responds with 200 status code) when given a request (that has valid parameters) and a response. */
    public void testValidResponseCode() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.addTestingLogs(); //Adding some sample logs to service so I can test the data that the service responds with
        request.addParameter("limit", "3");
        request.addParameter("level", "INFO");
        service.doGet(request, response);
        assertEquals(200, response.getStatus()); //200 is the success response code
    }

    @Test
    /** Tests that the log events in the response are formatted correctly (and contain all the relevant fields) and are sorted
     * by timestamp (latest logs are first) */
    public void testTimestampSorted() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.addTestingLogs();
        request.addParameter("limit", "3");
        request.addParameter("level", "INFO");
        service.doGet(request, response);
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

    @Test
    /** Tests that the content type of the response is valid */
    public void testValidContentType() throws IOException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.addParameter("limit", "3");
        request.addParameter("level", "INFO");
        service.doGet(request, response);
        assertEquals("application/json", response.getContentType()); //Content type should be application/json since Json String is being returned
    }

    @Test
    /** Tests that request's limit parameter limits the number of logs in the response */
    public void testValidLimitResponse() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.addTestingLogs();
        request.addParameter("limit", "2");
        request.addParameter("level", "INFO");
        service.doGet(request, response);
        String result = response.getContentAsString();
        Gson g = new Gson();
        JsonArray logs = g.fromJson(result, JsonArray.class);
        assertEquals(2, logs.size()); //Since limit is 2 only 2 logs should be returned.
    }

    @Test
    /** Tests that request's level parameter filters the response to logs that have a level equal to or higher than the level parameter */
    public void testValidLevelFilteringResponse() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.addTestingLogs();
        request.addParameter("limit", "3");
        request.addParameter("level", "WARN");
        service.doGet(request, response);
        String result = response.getContentAsString();
        Gson g = new Gson();
        JsonArray logs = g.fromJson(result, JsonArray.class);
        /* Testing that only WARN and ERROR logs are returned and the log with INFO level isn't since minimum level was set to WARN */
        assertEquals(2, logs.size());
        assertEquals("\"WARN\"", logs.get(0).getAsJsonObject().get("level").toString());
        assertEquals("\"ERROR\"", logs.get(1).getAsJsonObject().get("level").toString());
    }
}
