package pcrc.gotbetter.setting.security.JWT.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String)request.getAttribute("exception");

        System.out.println("exception:" + exception);

        //토큰이 없는 경우 예외처리
        if (exception == null) {
            setResponse(response, "UNAUTHORIZED", MessageType.UNAUTHORIZED);
            return;
        }

        //토큰이 만료된 경우 예외처리
        if (exception.equals("ExpiredJwtException")) {
            setResponse(response, "ExpiredJwtException", MessageType.ExpiredJwtException);
            return;
        }

        if (exception.equals("MalformedJwtException")) {
            setResponse(response, "MalformedJwtException", MessageType.MalformedJwtException);
            return;
        }

        // 이후 모든 exception은 illegal로 돌릴지./....
        if (exception.equals("IllegalArgumentException") || exception.equals("UnsupportedJwtException")) {
            setResponse(response, "IllegalArgumentJwtException", MessageType.MalformedJwtException);
            return;
        }

        if (exception.equals("UsernameOrPasswordNotFound")) {
            setResponse(response, "UsernameOrPasswordNotFound", MessageType.UsernameOrPasswordNotFound);
            return;
        }
    }

    private void setResponse(HttpServletResponse response, String type, MessageType messageType)  throws IOException {

        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("errorType", type);
        json.put("errorMessage", messageType.getMessage());
        response.getWriter().print(json);
    }
}
