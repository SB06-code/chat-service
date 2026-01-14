package codeit.sb06.ws.interceptor;

import codeit.sb06.ws.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {

        String token = extractTokenFromQuery(request.getURI());

        if (token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }


        try {
            if (!jwtTokenProvider.validateToken(token)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add("X-Auth-Error", "TOKEN_INVALID");
                return false;
            }
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("X-Auth-Error", "TOKEN_ERROR");
            return false;
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        attributes.put("username", username);
        attributes.put("token", token);

        return true;
    }

    private String extractTokenFromQuery(URI uri) {
        // http://abc.com?token=1234&date=2024-02-03
        String query = uri.getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    try {
                        return URLDecoder.decode(param.substring(6), StandardCharsets.UTF_8);
                    } catch (Exception E) {
                        return param.substring(6);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            @Nullable Exception exception
    ) {
    }
}
