package app.security;

import org.springframework.security.access.AccessDeniedException;

public final class OwnershipGuard {
    private OwnershipGuard() {}

    public static void requireOwnerOrAdmin(Long ownerId) {
        Long me = SecurityUtils.currentUserId();
        if (!SecurityUtils.isAdmin() && (ownerId == null || !ownerId.equals(me))) {
            throw new AccessDeniedException("No sos dueño de este recurso");
        }
    }
}
