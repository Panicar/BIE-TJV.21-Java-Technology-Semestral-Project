package com.panicar.epistemic.harmony.entity;

import jakarta.persistence.*;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "connection")
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_item_id", nullable = false)
    private EpistemicItem fromItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_item_id", nullable = false)
    private EpistemicItem toItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false, columnDefinition = "connection_type")
    private ConnectionType connectionType;

    @Column(name = "strength", nullable = false)
    private Integer strength;

    public enum ConnectionType {
        SUPPORTS, CONTRADICTS, RELATES, COMPLEMENTS
    }

    // Constructors
    public Connection() {}

    public Connection(EpistemicItem fromItem, EpistemicItem toItem,
                      ConnectionType connectionType, Integer strength) {
        this.fromItem = fromItem;
        this.toItem = toItem;
        this.connectionType = connectionType;
        this.strength = strength;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EpistemicItem getFromItem() { return fromItem; }
    public void setFromItem(EpistemicItem fromItem) { this.fromItem = fromItem; }

    public EpistemicItem getToItem() { return toItem; }
    public void setToItem(EpistemicItem toItem) { this.toItem = toItem; }

    public ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(ConnectionType connectionType) { this.connectionType = connectionType; }

    public Integer getStrength() { return strength; }
    public void setStrength(Integer strength) { this.strength = strength; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "id=" + id +
                ", fromItemId=" + (fromItem != null ? fromItem.getId() : null) +
                ", toItemId=" + (toItem != null ? toItem.getId() : null) +
                ", connectionType=" + connectionType +
                ", strength=" + strength +
                '}';
    }
}