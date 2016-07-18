package com.sogou.pay.remit.config;

import java.util.List;

import javax.sql.DataSource;
import com.alibaba.druid.pool.DruidDataSource;

import commons.mybatis.ListTypeHandler;
import commons.mybatis.LocalDateTimeTypeHandler;
import commons.utils.MyBatisHelper;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = ProjectInfo.MAPPER_PKG, sqlSessionFactoryRef = "sqlSessionFactory")
public class DaoConfig {

  @Autowired
  Environment env;

  @Bean(initMethod = "init", destroyMethod = "close")
  public DataSource dataSource() {
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
    dataSource.setUrl(env.getRequiredProperty("jdbc.url"));
    dataSource.setUsername(env.getRequiredProperty("jdbc.username"));
    dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
    dataSource.setMaxActive(20);
    dataSource.setInitialSize(1);
    dataSource.setMaxWait(60000);
    dataSource.setMinIdle(3);
    dataSource.setRemoveAbandoned(true);
    dataSource.setRemoveAbandonedTimeout(180);
    dataSource.setConnectionProperties("clientEncoding=UTF-8");
    return dataSource;
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource());

    TypeHandler<?>[] handlers = new TypeHandler[] { new LocalDateTimeTypeHandler(), };
    sqlSessionFactoryBean.setTypeHandlers(handlers);

    SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) sqlSessionFactoryBean.getObject();

    org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
    configuration.setCacheEnabled(false);
    configuration.setMultipleResultSetsEnabled(true);
    configuration.setDefaultExecutorType(ExecutorType.REUSE);
    configuration.setLazyLoadingEnabled(false);
    configuration.setAggressiveLazyLoading(true);
    configuration.setDefaultStatementTimeout(300);
    MyBatisHelper.registerEnumHandler(configuration.getTypeHandlerRegistry(), ProjectInfo.PKG_PREFIX);
    TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<List<String>>() {};
    configuration.getTypeHandlerRegistry().register(STRING_LIST_TYPE, new ListTypeHandler<String>());
    return sqlSessionFactory;
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }
}
