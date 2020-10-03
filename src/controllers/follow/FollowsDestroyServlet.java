package controllers.follow;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Follow;
import utils.DBUtil;

/**
 * Servlet implementation class FollowsDestroyServlet
 */
@WebServlet("/follows/destroy")
public class FollowsDestroyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowsDestroyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String _token = request.getParameter("_token");

        if (_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            Employee employee = (Employee)request.getSession().getAttribute("login_employee");
            Employee follwer =  em.createNamedQuery("getReportEmployee", Employee.class)
                                   .setParameter("code", request.getParameter("report.employee.code"))
                                   .getSingleResult();


            Follow f = em.createNamedQuery("isRegisterdFollows", Follow.class)
                         .setParameter("employee_code", employee.getCode())
                         .setParameter("follow_code", request.getParameter("report.employee.code"))
                         .getSingleResult();

            follwer.setFollow_flag(0);

            em.getTransaction().begin();
            em.remove(f);
            em.getTransaction().commit();
            em.close();

            request.getSession().setAttribute("flush", "フォローを解除しました");

            if (request.getParameter("from").equals("timeline")) {
                response.sendRedirect(request.getContextPath() + "/follows/index");
            } else {
                response.sendRedirect(request.getContextPath() + "/reports/index");
            }



        }

    }

}
