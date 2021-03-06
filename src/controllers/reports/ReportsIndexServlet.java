package controllers.reports;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Follow;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsIndexServlet
 */
@WebServlet("/reports/index")
public class ReportsIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("_token", request.getSession().getId());

        EntityManager em = DBUtil.createEntityManager();
        //followチェック用
        Employee employee = (Employee)request.getSession().getAttribute("login_employee");

        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e) {
            page = 1;
        }
        List<Report> reports = em.createNamedQuery("getAllReports", Report.class)
                                .setFirstResult(15 * (page -1))
                                .setMaxResults(15)
                                .getResultList();

        long reports_count = (long)em.createNamedQuery("getReportsCount", Long.class)
                                .getSingleResult();



        //reports記載の従業員をログインユーザがすでにfollowしているかどうかを確認 → リスト取得後に更新してもjspには反映されないのでは?
        Iterator<Report> reportsIterator = reports.iterator();
        Follow followCheck;
        while(reportsIterator.hasNext()) {
            Report report = reportsIterator.next();

            try {
                followCheck = em.createNamedQuery("isRegisterdFollows", Follow.class)
                        .setParameter("employee_code", employee.getCode())
                        .setParameter("follow_code", report.getEmployee().getCode())
                        .getSingleResult();
            } catch (Exception e) {
                followCheck = null;
            }

            if (followCheck == null) {
                report.getEmployee().setFollow_flag(0);
            } else {
                report.getEmployee().setFollow_flag(1);
                System.out.println(followCheck.getFollow_code());
                System.out.println(report.getEmployee().getName());
            }

            em.getTransaction().begin();
            em.persist(report);
            em.getTransaction().commit();
        }

        em.close();


        request.setAttribute("reports", reports);
        request.setAttribute("reports_count", reports_count);
        request.setAttribute("page", page);


        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp");
        rd.forward(request, response);


        response.getWriter().append("Served at: ").append(request.getContextPath());

    }

}
