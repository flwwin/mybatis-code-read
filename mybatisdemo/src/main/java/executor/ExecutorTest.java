package executor;
import bean.Mock;
import bean.User;
import mapper.UserMapper;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author flw
 */
public class ExecutorTest {

  private Configuration configuration;
  private Connection connection;
  private JdbcTransaction jdbcTransaction;
  private MappedStatement ms;
  private SqlSessionFactory factory;

  @Before
  public void init() {
    // 获取构建器
    SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
    // 解析XML 并构造会话工厂
    factory = factoryBuilder.build(ExecutorTest.class.getResourceAsStream("/mybatis-config.xml"));
    // 配置对象
    configuration = factory.getConfiguration();
    // 事务管理器
    jdbcTransaction = new JdbcTransaction(factory.openSession(true).getConnection());
    // 获取SQL映射
    ms = configuration.getMappedStatement("mapper.UserMapper.selectByid");
  }

  /** 基础执行器 */
  @Test
  public void simpleTest() throws SQLException {
    SimpleExecutor executor = new SimpleExecutor(configuration, jdbcTransaction);
    RowBounds rowBounds = RowBounds.DEFAULT;
    ResultHandler resultHandler = Executor.NO_RESULT_HANDLER;
    executor.doQuery(ms, 1, rowBounds, resultHandler, ms.getBoundSql(10));
    executor.doQuery(ms, 1, rowBounds, resultHandler, ms.getBoundSql(10));
  }

  @Test
  public void reuseTest() throws SQLException {
    ReuseExecutor executor = new ReuseExecutor(configuration, jdbcTransaction);
    RowBounds rowBounds = RowBounds.DEFAULT;
    ResultHandler resultHandler = Executor.NO_RESULT_HANDLER;
    executor.doQuery(ms, 1, rowBounds, resultHandler, ms.getBoundSql(10));
    executor.doQuery(ms, 1, rowBounds, resultHandler, ms.getBoundSql(10));
  }

  /** 批处理 只针对 修改操作，不针对查询 批处理执行器好处： SQL合并的条件： 1.相同的SQl statementID 、必须是连续的（才能保证执行顺序） */
  @Test
  public void batchTest() throws SQLException {
    BatchExecutor executor = new BatchExecutor(configuration, jdbcTransaction);
    MappedStatement ms =
        configuration.getMappedStatement("mapper.UserMapper.setName");
    MappedStatement addMapper =
        configuration.getMappedStatement("mapper.UserMapper.addUser");

    Map map = new HashMap<>();
    map.put("id", 10);
    map.put("name", "鲁班大佬");
    executor.doUpdate(ms, map);
    User newUSer = Mock.newUser();
    executor.doUpdate(addMapper, newUSer);
    executor.doUpdate(addMapper, Mock.newUser());
    map.put("id", newUSer.getId());
    executor.doUpdate(ms, map); // SQL语句会合并成一个 statement
    // A 4个 B 2个 C3个
    // 更新了多少条记录
    List<BatchResult> batchResults = executor.flushStatements();
  }

  /**
   * 一级缓存: 无法关闭的缓存，循环依赖的情况下出现死循环 一级缓存命中 条件 1.SQL语句相同 2.statement ID 相同 3.参数相同 4.分页条件相同 5.没有执行以下操作
   * a.update b.clear c.localCacheScope=statement
   */
  @Test
  public void cacheTest() throws SQLException {
    // query
    // 批处理执行器
    BaseExecutor executor = new BatchExecutor(configuration, jdbcTransaction);
    RowBounds rowBounds = RowBounds.DEFAULT;
    ResultHandler resultHandler = Executor.NO_RESULT_HANDLER;
    List<Object> query = executor.query(ms, 10, rowBounds, resultHandler); // statement
    List<Object> query1 = executor.query(ms, 10, new RowBounds(0, 100), resultHandler); // statement
    System.out.println(query == query1);
  }

  @Test
  public void sqlSessionTest() {
    // 二级缓存是一个跨会话
    SqlSession session = factory.openSession(ExecutorType.BATCH);
    session.selectList("mapper.UserMapper.selectByid", 1);
    session.commit(true); // 提交二级缓存到 二级缓存空间
    session.selectList("mapper.UserMapper.selectByid", 1);
  }

  @Test
  public void test01() {
      //UserMapper userMapper = new UserMapper();
    SqlSession sqlSession = factory.openSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.selectByid(1);
    System.out.println("user = " + user);
  }
}
