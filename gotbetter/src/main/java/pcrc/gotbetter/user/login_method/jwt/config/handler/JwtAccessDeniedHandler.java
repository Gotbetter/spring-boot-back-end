package pcrc.gotbetter.user.login_method.jwt.config.handler;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pcrc.gotbetter.setting.http_api.MessageType;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        JSONObject json = new JSONObject();

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("errorType", "FORBIDDEN");
        json.put("errorMessage", MessageType.FORBIDDEN.getMessage());
        response.getWriter().print(json);
    }

}
