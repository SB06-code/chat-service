package codeit.sb06.ws.controller;

import codeit.sb06.ws.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();

        // 사용자명 유효성 검사
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "사용자명을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        username = username.trim();

        if (username.length() < 2 || username.length() > 20) {
            response.put("success", false);
            response.put("message", "사용자명은 2-20글자여야 합니다.");
            return ResponseEntity.badRequest().body(response);
        }

        if (!username.matches("^[가-힣a-zA-Z0-9\\s]+$")) {
            response.put("success", false);
            response.put("message", "사용자명에는 한글, 영문, 숫자만 사용할 수 있습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        String token = jwtTokenProvider.createToken(username);

        response.put("success", true);
        response.put("token", token);
        response.put("username", username);

        return ResponseEntity.ok(response);
    }
}
