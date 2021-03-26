package com.infofoundation.firechat.beans;

import java.io.Serializable;

public class Group implements Serializable {
    private String createdBy,createdAt,groupId,description,icon,name;

    public Group() {
    }

    public String getCreatedBy() {

        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Group(String createdBy, String createdAt, String groupId, String description, String icon, String name) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.groupId = groupId;
        this.description = description;
        this.icon = icon;
        this.name = name;
    }
}
