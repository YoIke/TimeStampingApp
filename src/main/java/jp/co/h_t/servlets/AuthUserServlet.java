package jp.co.h_t.servlets;

import jp.co.h_t.dao.UsersDAO;
import jp.co.h_t.dto.UserDTO;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ユーザー認証処理を行うサーブレットクラス。
 */
@WebServlet({"/login", "/logout"})
public class AuthUserServlet extends HttpServlet {
    private VelocityEngine velocityEngine;
    private UsersDAO usersDAO;

    /**
     * サーブレットの初期化処理。VelocityEngineとUsersDAOオブジェクトを取得します。
     */
    @Override
    public void init() {
        ServletContext context = getServletContext();
        velocityEngine = (VelocityEngine) context.getAttribute("velocityEngine");
        usersDAO = (UsersDAO) context.getAttribute("usersDAO");
    }

    /**
     * GETリクエストを処理し、ログインフォームを表示するか、ログアウト処理を行います。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ログアウト処理
        if (request.getServletPath().equals("/logout")) {
            request.getSession().invalidate();
            response.sendRedirect("login");
            return;
        }

        // セッションにユーザー情報が格納されているかチェック
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");

        String templateName = user != null ? "loginSuccess.html" : "loginForm.html";
        prepareTemplate(templateName, user, response);
    }

    /**
     * POSTリクエストを処理し、ユーザー認証処理を実行し、結果を表示します。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ログインフォームから送信されたデータを取得
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // ユーザー認証処理を実行し、結果を取得
        UserDTO user = usersDAO.auth(username, password);
        String templateName = user != null ? "stampingForm.html" : "loginFailure.html";

        // 認証成功の場合、セッションにユーザー情報を格納
        if (user != null) {
            request.getSession().setAttribute("user", user);
            response.sendRedirect("record");
        }

        //TODO エラーメッセージをログイン画面に返す
    }

    /**
     * テンプレートを準備し、レスポンスに書き込みます。
     *
     * @param templateName テンプレート名
     * @param user         ユーザー情報
     * @param response     HttpServletResponse
     * @throws IOException 入出力エラーが発生した場合
     */
    private void prepareTemplate(String templateName, UserDTO user, HttpServletResponse response) throws IOException {
        Template template;
        try {
            template = velocityEngine.getTemplate(templateName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VelocityContext context = new VelocityContext();
        if (user != null) {
            context.put("user", user);
        }
        template.merge(context, response.getWriter());
    }
}
