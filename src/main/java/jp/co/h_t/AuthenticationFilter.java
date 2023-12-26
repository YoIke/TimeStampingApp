package jp.co.h_t;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = "")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // フィルターの初期化処理（必要に応じて実装）
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String requestPath = req.getRequestURI();
        boolean loggedIn = session != null && session.getAttribute("user") != null;

        if (loggedIn || requestPath.endsWith("login")) {
            // ログイン済み、またはログインページおよびログイン処理の場合、次の処理（サーブレットなど）に進む
            chain.doFilter(request, response);
        } else {
            // ログインしていない場合、ログインページへリダイレクト
            res.sendRedirect("login");
        }
    }

    @Override
    public void destroy() {
        // フィルターの終了処理（必要に応じて実装）
    }
}

