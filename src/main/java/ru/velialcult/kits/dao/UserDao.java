package ru.velialcult.kits.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.velialcult.kits.entity.KitDataEntity;
import ru.velialcult.kits.entity.UserEntity;
import ru.velialcult.library.java.utils.TimeUtil;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class UserDao {

    private final Dao<UserEntity, UUID> dao;

    private final ConnectionSource connectionSource;

    private final KitDataDao kitDataDao;

    public UserDao(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;

        dao = DaoManager.createDao(connectionSource, UserEntity.class);

        TableUtils.createTableIfNotExists(connectionSource, UserEntity.class);

        this.kitDataDao = new KitDataDao(connectionSource);

    }

    public void takeKit(String kitName, UUID uuid) throws SQLException {

        UserEntity user = queryById(uuid);

        if (user == null) {
            user = TransactionManager.callInTransaction(connectionSource, () -> create(uuid));
        }

        KitDataEntity kitDataEntity = kitDataDao.queryIdOrCreate(kitName, user);

        kitDataEntity.setDate(new Date());

        kitDataDao.update(kitDataEntity);

        ForeignCollection<KitDataEntity> kitData = user.getKitsData();

        if (kitData.contains(kitDataEntity)) {

            kitData.update(kitDataEntity);

        } else {

            kitData.add(kitDataEntity);

        }

        dao.createOrUpdate(user);
    }

    public UserEntity create(UUID uuid) throws SQLException {

        return TransactionManager.callInTransaction(connectionSource, () -> {

            UserEntity userEntity = new UserEntity(uuid, dao.getEmptyForeignCollection("kits_data"));

            create(userEntity);

            return userEntity;
        });
    }

    public UserEntity queryById(UUID id) throws SQLException{
       Optional<UserEntity> optionEntity = Optional.ofNullable(dao.queryForId(id));

        if (optionEntity.isEmpty()) {
            return create(id);
        }

        return optionEntity.get();
    }

    private void create(UserEntity userEntity) throws SQLException {
        dao.createOrUpdate(userEntity);
    }

    private Dao<UserEntity, UUID> getDao() {
        return dao;
    }

    public KitDataDao getKitDataDao() {
        return kitDataDao;
    }
}
