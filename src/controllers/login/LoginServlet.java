package controllers.login;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
        request.setAttribute("_token", request.getSession().getId());
        request.setAttribute("hasError", false);
        //どこのflush_?

        if(request.getSession().getAttribute("flush") != null) {
            request.getSession().setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    //ログイン処理
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //認証結果を格納する変数
        Boolean check_result =false;

        String code = request.getParameter("code");
        String plain_pass = request.getParameter("password");

        Employee employee = null;

        if(code != null && !code.equals("") && plain_pass != null && !plain_pass.equals("")) {
            EntityManager em = DBUtil.createEntityManager();

            String password = EncryptUtil.getPasswordEncrypt(plain_pass,
                    (String)this.getServletContext().getAttribute("pepper")
                    );
            //社員番号とパスワードが正しいかをチェックする
            try {
                employee = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
                        .setParameter("code", code)
                        .setParameter("pass", password)
                        .getSingleResult();
            } catch(NoResultException ex) {}

            em.close();

            if (employee != null) {
                check_result = true;
            }
        }

        if(!check_result) {
            //認証できなかたらログイン画面に戻る
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("hasError", true);
            request.setAttribute("code", code);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);
        } else {
            //認証できたらログイン状態にしてトップページへリダイレクト
            request.getSession().setAttribute("login_employee", employee);

            //Follow_flagの更新作業
            EntityManager em = DBUtil.createEntityManager();

            List<Report> reports = em.createNamedQuery("getAllReports", Report.class)
                    .getResultList();

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


            request.getSession().setAttribute("flush", "ログインしました。");
            response.sendRedirect(request.getContextPath() + "/");
        }


    }

}
