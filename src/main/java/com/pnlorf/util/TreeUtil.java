package com.pnlorf.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 把一个list集合，里面的bean含有parentId转为树形式
 * <p>
 * Created by 冰诺莫语 on 2015/10/23.
 */
public class TreeUtil {

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return
     */
    public List<TreeObject> getChildTreeObjects(List<TreeObject> list, int parentId) {
        List<TreeObject> treeObjectList = new ArrayList<TreeObject>();
        for (Iterator<TreeObject> iterator = list.iterator(); iterator.hasNext(); ) {
            TreeObject treeObject = iterator.next();
            // 根据穿入的某个父节点ID，遍历该父节点的所有子节点
            if (treeObject.getParentId() == parentId) {
                recursionFn(list, treeObject);
                treeObjectList.add(treeObject);
            }
        }
        return treeObjectList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<TreeObject> list, TreeObject t) {
        List<TreeObject> childList = getChildList(list, t);// 得到子节点列表
        t.setChildren(childList);
        for (TreeObject tChild : childList) {
            if (hasChild(list, tChild)) {// 判断是否有子节点
                //returnList.add(TreeObject);
                Iterator<TreeObject> it = childList.iterator();
                while (it.hasNext()) {
                    TreeObject n = (TreeObject) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    // 得到子节点列表
    private List<TreeObject> getChildList(List<TreeObject> list, TreeObject t) {

        List<TreeObject> tlist = new ArrayList<TreeObject>();
        Iterator<TreeObject> it = list.iterator();
        while (it.hasNext()) {
            TreeObject n = (TreeObject) it.next();
            if (n.getParentId() == t.getId()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    List<TreeObject> returnList = new ArrayList<TreeObject>();

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list   分类表
     * @param typeId 传入的父节点ID
     * @param prefix 子节点前缀
     */
    public List<TreeObject> getChildTreeObjects(List<TreeObject> list, int typeId, String prefix) {
        if (list == null) return null;
        for (Iterator<TreeObject> iterator = list.iterator(); iterator.hasNext(); ) {
            TreeObject node = (TreeObject) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (node.getParentId() == typeId) {
                recursionFn(list, node, prefix);
            }
            // 二、遍历所有的父节点下的所有子节点
            /*if (node.getParentId()==0) {
                recursionFn(list, node);
            }*/
        }
        return returnList;
    }

    private void recursionFn(List<TreeObject> list, TreeObject node, String p) {
        List<TreeObject> childList = getChildList(list, node);// 得到子节点列表
        if (hasChild(list, node)) {// 判断是否有子节点
            returnList.add(node);
            Iterator<TreeObject> it = childList.iterator();
            while (it.hasNext()) {
                TreeObject n = (TreeObject) it.next();
                n.setName(p + n.getName());
                recursionFn(list, n, p + p);
            }
        } else {
            returnList.add(node);
        }
    }

    // 判断是否有子节点
    private boolean hasChild(List<TreeObject> list, TreeObject t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    // 本地模拟数据测试
    public void main(String[] args) {
        /*long start = System.currentTimeMillis();
        List<TreeObject> TreeObjectList = new ArrayList<TreeObject>();

		TreeObjectUtil mt = new TreeObjectUtil();
		List<TreeObject> ns=mt.getChildTreeObjects(TreeObjectList,0);
		for (TreeObject m : ns) {
			System.out.println(m.getName());
			System.out.println(m.getChildren());
		}
		long end = System.currentTimeMillis();
		System.out.println("用时:" + (end - start) + "ms");*/
    }
}
