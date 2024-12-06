package ru.velialcult.kits.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;

@DatabaseTable(tableName = "kits_data")
public class KitDataEntity {

    @DatabaseField(canBeNull = false, unique = true, generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = "kit")
    private String kitName;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE)
    private Date date;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "user_id")
    private UserEntity user;

    public KitDataEntity(String kitName, UserEntity user) {
        this.kitName = kitName;
        LocalDate localDate = LocalDate.of(2000, 1, 1);
        this.date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.user = user;
    }

    public KitDataEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getKitName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }
}
