package com.tragefox.api.security.filter;

import com.tragefox.api.application.service.UserDetailService;
import com.tragefox.api.exception.ApplicationException;
import com.tragefox.api.exception.constant.ErrorType;
import com.tragefox.api.security.constant.RoleType;
import com.tragefox.api.security.helper.JwtTokenHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private static final JsonMapper OBJECT_MAPPER = new JsonMapper();
    private static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String URL = "url";
    private static final String ROLE = "role";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String PASSWORD_RESET_CLAIM = "passwordReset";

    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();
            if (isPublicEndpoint(uri)) {
                filterChain.doFilter(request, response);
                return;
            }
            String authHeader = request.getHeader(AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);
            String username = JwtTokenHelper.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, token, username);
            }
            var claims = JwtTokenHelper.getTokenBody(token);
            var passwordRestClaim = claims.get(PASSWORD_RESET_CLAIM, String.class);
            if (request.getRequestURI().equals("/api/v1/reset-password")
                    && !passwordRestClaim.equals(RoleType.PASSWORD_RESET_CLAIM.getValue())) {
                throw new ApplicationException(ErrorType.ACCESS_DENIED);
            }
            if (request.getRequestURI().equals("/api/v1/reset-password")
                    && !passwordRestClaim.equals(RoleType.PASSWORD_RESET_CLAIM.getValue())) {
                throw new ApplicationException(ErrorType.ACCESS_DENIED);
            }
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            sendJsonResponse(request, response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private static void sendJsonResponse(
            HttpServletRequest request, HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        Map<String, Object> responseBody = Map.of(
                MESSAGE, message,
                TIMESTAMP, LocalDateTime.now().toString(),
                URL, request.getRequestURI());

        try (PrintWriter writer = response.getWriter()) {
            OBJECT_MAPPER.writeValue(writer, responseBody);
        }
    }

    private void authenticateUser(HttpServletRequest request, String token, String username) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        var claim = JwtTokenHelper.getTokenBody(token);
        var role = claim.get(ROLE, String.class);
        var roles = prepareRoles(RoleType.valueOf(role)).stream()
                .map(roleType -> new SimpleGrantedAuthority(
                        ROLE_PREFIX + roleType.getValue().toUpperCase()))
                .toList();
        if (JwtTokenHelper.validateToken(token, userDetails)) {
            var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, roles);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    private List<RoleType> prepareRoles(RoleType roleType) {
        return switch (roleType) {
            case APP_MANAGER -> List.of(RoleType.APP_USER, RoleType.APP_MANAGER);
            case APP_ADMIN -> List.of(RoleType.APP_USER, RoleType.APP_MANAGER, RoleType.APP_ADMIN);
            case SUPER_USER ->
                List.of(RoleType.APP_USER, RoleType.APP_MANAGER, RoleType.APP_ADMIN, RoleType.SUPER_USER);
            default -> List.of(RoleType.APP_USER);
        };
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-ui.html")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.contains("/login")
                || uri.contains("/signup");
    }
}
