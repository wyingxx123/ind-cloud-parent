package com.dfc.ind.gateway.mapper;


import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.gateway.entity.FiltersterVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface FiltersterMapper {

     @Select("    SELECT r.role_id FROM `ind-cloud-sys`.sys_user u\n" +
             "        left JOIN `ind-cloud-sys`.sys_user_role ur on ur.user_id=u.user_id\n" +
             "        left JOIN `ind-cloud-sys`.sys_role r on r.role_id =ur.role_id\n" +
             "        WHERE u.user_id=#{userId}  and u.merchant_id=#{merchantId} AND u.del_flag='0'")
    List<String> selectByUserId(@Param("userId") String userId,@Param("merchantId") String merchantId);

     @Select(" SELECT COUNT(*) FROM c_mov_role_white WHERE mov_url like concat('%',#{path},'%')")
    int selectByurlName(@Param("path") String path);

     @Select(" SELECT count(*) FROM c_mov_role_authorization WHERE mov_url like concat('%',#{path},'%')  AND role_id=#{roleId}")
    int selectByPathAndRoleId(@Param("path") String path,@Param("roleId") String roleId);

     @Select("  \n" +
             "  SELECT \n" +
             "   s.menu_id  AS menuId,\n" +
             "   s.menu_name  AS menuName,\n" +
             "   s.parent_id  AS parentId,\n" +
             "   s.parent_name AS parentName ,\n" +
             "   s.path  AS path,\n" +
             "   s.component  AS component,\n" +
             "   s.perms  AS perms\n" +
             "   FROM `ind-cloud-sys`.sys_role r \n" +
             "                  LEFT  OUTER JOIN `ind-cloud-sys`.sys_role_menu m ON r.role_id = m.role_id \n" +
             "                  LEFT   OUTER  JOIN `ind-cloud-sys`.sys_menu s ON s.menu_id = m.menu_id\n" +
             "                  WHERE r.role_id = #{roleId}  AND  s.merchant_id= #{merchantId}")
    List<SysMenu> selectByroldormulis(@Param("roleId") String roleId, @Param("merchantId") String merchantId);

    @Select("SELECT distinct(menu_name) FROM  `c_apic`.c_mov_role_menu_service  WHERE mov_url LIKE  concat('%',#{path},'%')")
    List<String> selectByroldormuliscount(@Param("roleId") String roleId, @Param("merchantId") String merchantId,@Param("path") String path);


    @Select("SELECT \n" +
            "   COUNT(*)\n" +
            "   FROM `ind-cloud-sys`.sys_role r \n" +
            "                  LEFT  OUTER JOIN `ind-cloud-sys`.sys_role_menu m ON r.role_id = m.role_id \n" +
            "                  LEFT   OUTER  JOIN `ind-cloud-sys`.sys_menu s ON s.menu_id = m.menu_id\n" +
            "                  WHERE r.role_id = #{roleId}   AND  s.merchant_id= #{merchantId}  AND s.menu_name =#{muenname}")
    int selectBymunename(@Param("muenname") String muenname, @Param("roleId") String roleId,@Param("merchantId") String merchantId);

    @Select("SELECT count(*) FROM   c_mov_role_authorization  WHERE aisle_type =#{type}   AND mov_url LIKE   concat('%',#{path}) ")
    int selectBytypeorroleId(@Param("type") String substring,@Param("roleId") String roleId, @Param("path") String path);

    @Select("select user_name from `ind-cloud-sys`.sys_user where user_id=#{userId}")
    String selectByuserId(@Param("userId") String userId);

    List<FiltersterVo> lists(FiltersterVo filtersterVo);

    int addrebo(FiltersterVo filtersterVo);

    @Select("select * from  ${pathtype}  where id =#{id}")
    FiltersterVo selectOne(FiltersterVo filtersterVo);

   @Delete("delete from ${pathtype} where id=#{id}")
    int deteleOne(FiltersterVo filtersterVo);
}
