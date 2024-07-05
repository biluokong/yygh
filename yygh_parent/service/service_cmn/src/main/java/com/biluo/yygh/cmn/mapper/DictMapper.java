package com.biluo.yygh.cmn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biluo.yygh.model.cmn.Dict;

import java.util.List;

public interface DictMapper extends BaseMapper<Dict> {
	/**
	 * 根据指定父节点id查询子节点，然后查询出每个子节点的子节点数
	 * @param parentId 父节点id
	 */
	List<Integer> selectChildCountsByParentId(Long parentId);
}
