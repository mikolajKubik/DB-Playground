package edu.kdmk.game;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface GameMapper {

    @DaoFactory
    GameDao gameDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    GameDao gameDao();
}
