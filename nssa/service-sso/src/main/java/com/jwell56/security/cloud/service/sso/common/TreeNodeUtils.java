package com.jwell56.security.cloud.service.sso.common;


import com.jwell56.security.cloud.service.sso.entity.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class TreeNodeUtils {

    /**
     * id、pid转化成childre
     * @param dbList
     * @param pid
     * @return
     */
    public static List<TreeNode> getTreeList(List<TreeNode> dbList, Integer pid) {
        List<TreeNode> resultList = new ArrayList<>();
        for (TreeNode data : dbList) {
            if (data.getPid() == pid) {
                List<TreeNode> childList = getTreeList(dbList, data.getId());
                data.setChildren(childList);
                resultList.add(data);
            }
        }
        return resultList;
    }
}
