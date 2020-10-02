package controlles.follow;

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
 * Servlet implementation class FollowsCreateServlet
 */
@WebServlet("/follows/create")
public class FollowsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowsCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");

        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();
            Follow f = new Follow();
            Employee employee = (Employee)request.getSession().getAttribute("login_employee");


            Follow checkFollow = null;
            try {
                checkFollow = em.createNamedQuery("isRegisterdFollows", Follow.class)
                        .setParameter("employee_code", employee.getCode())
                        .setParameter("follow_code", request.getParameter("report.employee.code"))
                        .getSingleResult();
            } catch (Exception e) {

            }


            if(checkFollow == null) {
                f.setEmployee_code(employee.getCode());
                f.setFollow_code(request.getParameter("report.employee.code"));
                em.getTransaction().begin();
                em.persist(f);
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "フォローしました");
            } else {
                request.getSession().setAttribute("flush", "フォローに失敗しました");
            }

            if (request.getParameter("from").equals("timeline")) {
                response.sendRedirect(request.getContextPath() + "/follows/index");
            } else {
                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}
