package com.yc.communitys.entity;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  17:04
 * @description: 分页
 */
public class Page {
    // 当前页码
    private int current = 1;

    // 每页显示上限
    private int limit = 10;

    // 数据总数
    // (用于计算 总页数 ：Total = (rows % limit) == 0 ? rows/limit : rows/limit+1)
    private int rows;

    // 查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    // 不能为负数
    public void setCurrent(int current) {
        if(current >= 1) {
            this.current = current;
        }
    }

    // 获取每页显示上限
    public int getLimit() {
        return limit;
    }

    // 获取每页显示上限 1~100
    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    // 获取总行数
    public int getRows() {
        return rows;
    }

    // 设置总行数-非负数
    public void setRows(int rows) {
        if(rows >= 0) {
            this.rows = rows;
        }
    }

    // 获取分页路径
    public String getPath() {
        return path;
    }

    // 设置分页路径
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行:
     * (当前页码-1) * 每页显示上限
     * @return 获取当前页的起始行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * = (数据总数 / 每页显示上限 == 0) ? (数据总数 / 每页显示上限) : (数据总数 / 每页显示上限 + 1)
     * @return 获取总页数
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * 从 '上一页' 到 当前页码 最多间隔两个
     * @return 起始页码
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     * 从 当前页码 到 '下一页' 最多间隔两个
     * @return 结束页码
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
