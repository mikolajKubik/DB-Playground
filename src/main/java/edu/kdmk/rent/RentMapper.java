package edu.kdmk.rent;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface RentMapper {
    @DaoFactory
    RentDao rentDao(@DaoKeyspace String keyspace);

    @DaoFactory
    RentDao rentDao();
}
