package ru.velialcult.kits.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.velialcult.kits.entity.KitDataEntity;
import ru.velialcult.kits.entity.UserEntity;

import java.sql.SQLException;
import java.util.Optional;

public class KitDataDao {

    private final Dao<KitDataEntity, Integer> dao;

    public KitDataDao(ConnectionSource connectionSource) throws SQLException {

        this.dao = DaoManager.createDao(connectionSource, KitDataEntity.class);

        TableUtils.createTableIfNotExists(connectionSource, KitDataEntity.class);
    }

    public KitDataEntity queryIdOrCreate(String kitName, UserEntity user) throws SQLException {
        Optional<KitDataEntity> optionalEntity = Optional.ofNullable(dao.queryBuilder()
                .where()
                .eq("kit", kitName)
                .and()
                .eq("user_id", user)
                .queryForFirst());

        if (optionalEntity.isEmpty()) {
            KitDataEntity kitDataEntity = new KitDataEntity(kitName, user);

            dao.createOrUpdate(kitDataEntity);

            return kitDataEntity;
        }

        return optionalEntity.get();
    }

    public void update(KitDataEntity kitDataEntity) throws SQLException {
        dao.createOrUpdate(kitDataEntity);
    }
}
