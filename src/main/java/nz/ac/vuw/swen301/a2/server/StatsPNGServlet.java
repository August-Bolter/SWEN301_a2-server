package nz.ac.vuw.swen301.a2.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatsPNGServlet extends HttpServlet {
    @Override
    /** Returns a PNG image of a bar-chart where the bars represent different the number of logs at different levels. */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LogsServlet.logs.size() > 0) {
            resp.setContentType("image/png"); //Since output will be a PNG image
        }
    }
}
