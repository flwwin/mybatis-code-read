package mapper;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Options;
import bean.Blog;

/**
 * @author tommy
 * @title: BlogMapper
 * @projectName coderead-mybatis
 * @description: TODO
 * @date 2020/6/12:18 PM
 */
@CacheNamespace
public interface BlogMapper {
    //@Select("select * from blog where id=#{id}")
    @Options
    Blog selectById(Integer id);

   /* void selectUserById();

    Blog selectBlogById(int id);*/
}
