package test.nz.ac.vuw.swen301.a2.server;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class TestGetLogs {
    @Test
    /** Testing that 400 error code is the response when request is given with no parameters */
    public void testInvalidResponseCodeNoParam() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with only the level parameter */
    public void testInvalidResponseCodeOneParamLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
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
        request.addParameter("limit", "-2");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with a limit that isn't an integer */
    public void testInvalidResponseCodeNonIntLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.addParameter("limit", "lim");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that 400 error code is the response when request is given with a level that isn't valid (outside of enum 'levels' scope in LogsServlet) */
    public void testInvalidResponseCodeInvalidLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        request.addParameter("level", "test");
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }

    @Test
    /** Testing that doGet() works (responds with 200 status code) when given a request (that has valid parameters) and a response.
     * Also tests that the log events in the response are sorted by timestamp (latest logs are first)
     */
    public void testValidResponeCodeAndSortedByTimeStamp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.addTestingLogs();
        request.addParameter("limit", "3");
        request.addParameter("level", "INFO");
    }
}
