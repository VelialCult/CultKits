package ru.velialcult.kits.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "kits_users")
public class UserEntity {

    @DatabaseField(id = true, unique = true, canBeNull = false)
    private UUID id;

    @ForeignCollectionField(eager = true, columnName = "kits_data")
    private ForeignCollection<KitDataEntity> kitsData;

    public UserEntity(UUID id, ForeignCollection<KitDataEntity> kitsData) {
        this.id = id;
        this.kitsData = kitsData;
    }

    public UserEntity() {

    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public ForeignCollection<KitDataEntity> getKitsData() {
        return kitsData;
    }

    public void setKitsData(ForeignCollection<KitDataEntity> kitsData) {
        this.kitsData = kitsData;
    }
}
