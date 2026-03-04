package com.panicar.epistemic.harmony.dto;

import com.panicar.epistemic.harmony.entity.Connection;
import com.panicar.epistemic.harmony.entity.Connection.ConnectionType;

public class ConnectionDTO {
    private Long id;
    private Long fromItemId;
    private String fromItemName;
    private Long toItemId;
    private String toItemName;
    private ConnectionType connectionType;
    private Integer strength;

    public ConnectionDTO() {}

    public ConnectionDTO(Connection connection) {
        this.id = connection.getId();
        this.fromItemId = connection.getFromItem().getId();
        this.fromItemName = connection.getFromItem().getName();
        this.toItemId = connection.getToItem().getId();
        this.toItemName = connection.getToItem().getName();
        this.connectionType = connection.getConnectionType();
        this.strength = connection.getStrength();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFromItemId() { return fromItemId; }
    public void setFromItemId(Long fromItemId) { this.fromItemId = fromItemId; }

    public String getFromItemName() { return fromItemName; }
    public void setFromItemName(String fromItemName) { this.fromItemName = fromItemName; }

    public Long getToItemId() { return toItemId; }
    public void setToItemId(Long toItemId) { this.toItemId = toItemId; }

    public String getToItemName() { return toItemName; }
    public void setToItemName(String toItemName) { this.toItemName = toItemName; }

    public ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(ConnectionType connectionType) { this.connectionType = connectionType; }

    public Integer getStrength() { return strength; }
    public void setStrength(Integer strength) { this.strength = strength; }
}