package com.inf.medical_record_system.dto;

public class AdminUserViewDTO {

    private Long id;
    private String username;
    private String email;
    private String roleName;

    private boolean assigned;
    private String assignedType;
    private Long assignedEntityId;
    private String assignedName;

    public AdminUserViewDTO() {
    }

    public Long getId() {
        return id;
    }

    public AdminUserViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AdminUserViewDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AdminUserViewDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public AdminUserViewDTO setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public AdminUserViewDTO setAssigned(boolean assigned) {
        this.assigned = assigned;
        return this;
    }

    public String getAssignedType() {
        return assignedType;
    }

    public AdminUserViewDTO setAssignedType(String assignedType) {
        this.assignedType = assignedType;
        return this;
    }

    public Long getAssignedEntityId() {
        return assignedEntityId;
    }

    public AdminUserViewDTO setAssignedEntityId(Long assignedEntityId) {
        this.assignedEntityId = assignedEntityId;
        return this;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public AdminUserViewDTO setAssignedName(String assignedName) {
        this.assignedName = assignedName;
        return this;
    }
}