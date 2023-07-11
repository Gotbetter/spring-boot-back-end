package pcrc.gotbetter.user.login_method.jwt.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JwtAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String exception = (String)request.getAttribute("exception");

        LocalDateTime now = LocalDateTime.now();
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//        System.out.println("[" + formatedNow + "] exception: " + exception);

        //토큰이 없는 경우 예외처리
        if (exception == null) {
            setResponse(response, "UNAUTHORIZED", MessageType.UNAUTHORIZED);
            return;
        }

        if (exception.equals("UnauthorizedException")) {
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
            setResponse(response, "IllegalArgumentJwtException", MessageType.IllegalArgumentJwtException);
            return;
        }

        if (exception.equals("UsernameOrPasswordNotFound")) {
            setResponse(response, "UsernameOrPasswordNotFound", MessageType.UsernameOrPasswordNotFound);
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
