package com.revature.project1;

import com.revature.project1.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class DispatchServlet extends HttpServlet {

    public void init() {}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        String jsonResponse = null;
        if (uriDecon.length >= 3) {
            switch (uriDecon[2]) {
                case "user":
                    //TODO Pass to service object
                    jsonResponse = UserService.getInstance()
                                              .processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length));
                    break;
                case "reimbursement":
                    //TODO Pass to service object
                    break;
                default:
                    //TODO return code not found;
            }
        } else {
            //TODO return code not found;
            response.sendError(404, "Path not found");
        }
        PrintWriter out = response.getWriter();
        out.println(jsonResponse);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
    }
}
