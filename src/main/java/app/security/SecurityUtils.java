package app.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Authentication auth() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null) {
            throw new AccessDeniedException("No autenticado");
        }
        return a;
    }

    public static Long currentUserId() {
        return (Long) auth().getPrincipal();
    }

    public static boolean hasAuthority(String authority) {
        return auth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }

    public static boolean isAdmin()  { return hasAuthority("ROL_ADMIN"); }
    public static boolean isUser()   { return hasAuthority("ROL_USER"); }
    public static boolean isTaller() { return hasAuthority("ROL_TALLER"); }


    public static void requireAdminOrSelf(Long targetUserId) {
        Long me = currentUserId();
        if (!isAdmin() && (targetUserId == null || !targetUserId.equals(me))) {
            throw new AccessDeniedException("No autorizado");
        }
    }
}

