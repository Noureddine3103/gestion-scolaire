package com.school.gestion.service;

import com.school.gestion.model.Utilisateur;
import com.school.gestion.util.SessionManager;

public final class AuthorizationService {
    private AuthorizationService() {}

    public static boolean canAccessRole(String role) {
        Utilisateur user = SessionManager.getCurrentUser();
        return user != null && role != null && role.equalsIgnoreCase(user.getRole());
    }

    public static boolean isAdmin() {
        return canAccessRole(Utilisateur.ROLE_ADMIN);
    }

    public static boolean isTeacher() {
        return canAccessRole(Utilisateur.ROLE_ENSEIGNANT);
    }

    public static boolean isParent() {
        return canAccessRole(Utilisateur.ROLE_PARENT);
    }

    public static boolean isStudent() {
        return canAccessRole(Utilisateur.ROLE_ELEVE);
    }

    public static boolean canManageSensitiveAdminAction() {
        return isAdmin();
    }
}
