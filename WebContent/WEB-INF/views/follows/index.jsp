<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:import url="../layout/app.jsp">
    <c:param name="content">
        <h2>タイムライン</h2>
        <table id="report_list">
            <tr>
                <th class="report_name">氏名</th>
                <th class="report_follow">フォロー</th>
                <th class="report_date">日付</th>
                <th class="report_title">タイトル</th>
                <th class="report_action">操作</th>
            </tr>
            <c:forEach var="report" items="${reports}">
                <tr>
                    <td class="report_name">
                        <c:out value="${report.employee.name}" />
                    </td>
                    <td class="report_follow">
                        <c:choose>
                            <c:when test="${sessionScope.login_employee.code == report.employee.code}">
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${report.employee.follow_flag == 0}">
                                        <form method="POST" action="<c:url value='/follows/create'/>">
                                            <input type="hidden" name="_token" value="${_token}">
                                            <input type="hidden" name="report.employee.code" value="${report.employee.code}">
                                            <input type="hidden" name="from" value="timeline">
                                            <p><button type="submit">フォローする</button></p>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form method="POST" action="<c:url value='/follows/destroy'/>">
                                            <input type="hidden" name="_token" value="${_token}">
                                            <input type="hidden" name="report.employee.code" value="${report.employee.code}">
                                            <input type="hidden" name="report.employee" value="${report.employee}">
                                            <input type="hidden" name="from" value="timeline">
                                            <p><button type="submit">フォロー解除する</button></p>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="report_date">
                        <fmt:formatDate value='${report.report_date}' pattern='yyyy-MM-dd' />
                    </td>
                    <td class="report_title">
                        <c:out value="${report.title}" />
                    </td>
                    <td class="report_action">
                        <a href="<c:url value='/reports/show?id=${report.employee.id}'/>">詳細を見る</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:param>
</c:import>